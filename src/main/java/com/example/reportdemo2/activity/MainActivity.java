package com.example.reportdemo2.activity;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.example.reportdemo2.R;
import com.example.reportdemo2.adapter.MyCheckBoxAdapter;
import com.example.reportdemo2.adapter.MyCursorAdapter;
import com.example.reportdemo2.bean.PatientInfo;
import com.example.reportdemo2.provider.PatientInfoObserver;
import com.example.reportdemo2.utils.TimeUtil;
import com.example.reportdemo2.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 信息显示界面
 */
public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener, View.OnClickListener {
    private List<PatientInfo> temp = new ArrayList<>();
    private ListView listView;
    private MyCursorAdapter myCursorAdapter;
    private MyCheckBoxAdapter myCheckBoxAdapter;
    private Button btnEdit;
    private Button btnPreview;
    private Button btnDelete;
    private Button btnTransmission;
    private Button btnQuery;
    private AlertDialog.Builder builder;
    private AlertDialog alert = null;
    private int index;
    private static final String AUTHORITY = "com.wang.patientInfo.provider";
    public static final Uri PATIENT_INFO_ALL_URI = Uri.parse("content://" + AUTHORITY + "/patientInfo");
    private PatientInfoObserver patientInfoObserver;

    /**
     * 一个Activity中Handler该有的样子
     * 通过弱引用防止因持有activity的上下文导致可能的内存泄漏
     */
    private class MyHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                MainActivity.this.query();
            }
        }
    }

    //没有优化的handler,可与上面优化的handler做一个比较
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            query();
//        }
//    };

    private View view_custom;
    private MyAsyncQueryHandler asyncQueryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
        patientInfoObserver = new PatientInfoObserver(new MyHandler(this));
        initView();
        initListener();
        query();
        getContentResolver().registerContentObserver(PATIENT_INFO_ALL_URI, true, patientInfoObserver); //监听数据库
    }

    /**
     * 点击事件的注册
     */
    private void initListener() {
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        btnEdit.setOnClickListener(this);
        btnPreview.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnTransmission.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        listView = (ListView) findViewById(R.id.lv_information);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnPreview = (Button) findViewById(R.id.btn_preview);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnTransmission = (Button) findViewById(R.id.btn_transmission);
        btnQuery = (Button) findViewById(R.id.btn_query);
    }

    /**
     * ListView的点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //当点击"编辑"按钮时，点击ListView的item会选中相对应的checkbox
        if (myCheckBoxAdapter.flag == true) {
            CheckBox box = (CheckBox) view.findViewById(R.id.checkbox_operate_data);    //（在第二层布局中找到checkbox控件）
            if (box.isChecked() == true) {
                box.setChecked(false);
                temp.get(position).isCheck = false;
            } else {
                box.setChecked(true);
                temp.get(position).isCheck = true;
            }
        //没有点击"编辑"按钮时，点击ListView的item会进入详情界面
        } else {
            index = position;
            //跳转到编辑页面
            Intent intent = new Intent(MainActivity.this, EditPatientInfoActivity.class);
            PatientInfo patientInfo = temp.get(index);
            intent.putExtra("patientInfo", patientInfo);
            startActivity(intent);
        }
    }

    /**
     * ListView的长按点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        index = position;
        builder = new AlertDialog.Builder(MainActivity.this);
        alert = builder.setTitle("提示：")
                .setMessage("确认删除该条数据？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除相对应的数据库中的数据（根据_id删除）
                        Uri delUri = ContentUris.withAppendedId(PATIENT_INFO_ALL_URI, temp.get(index).get_id());
                        asyncQueryHandler.startDelete(0, null, delUri, null, null);
                    }
                }).create();             //创建AlertDialog对象
        alert.show();                    //显示对话框
        return true;
    }

    /**
     * “查询”和“插入”按钮的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                //初始化Builder
                builder = new AlertDialog.Builder(MainActivity.this);
                //加载自定义的那个View,同时设置下
                view_custom = getLayoutInflater().inflate(R.layout.view_dialog_custom, null, false);
                final EditText queryNumber = (EditText) view_custom.findViewById(R.id.et_number);
                final EditText queryName = (EditText) view_custom.findViewById(R.id.et_name);
//                EditText queryTime = (EditText) view_custom.findViewById(R.id.et_time);
                builder.setView(view_custom)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(queryName.getText()) && !TextUtils.isEmpty(queryNumber.getText())) {

                                    asyncQueryHandler.startQuery(0, null, PATIENT_INFO_ALL_URI, null, "number= ?", new String[]{String.valueOf(queryNumber.getText())}, "time desc");

                                } else if (!TextUtils.isEmpty(queryName.getText()) && TextUtils.isEmpty(queryNumber.getText())) {

                                    asyncQueryHandler.startQuery(0, null, PATIENT_INFO_ALL_URI, null, "name= ?", new String[]{String.valueOf(queryName.getText())}, null);

                                } else if (!TextUtils.isEmpty(queryName.getText()) && !TextUtils.isEmpty(queryNumber.getText())) {

                                    asyncQueryHandler.startQuery(0, null, PATIENT_INFO_ALL_URI, null, "number= ? and name= ?", new String[]{String.valueOf(queryNumber.getText()), String.valueOf(queryName.getText())}, null);

                                } else {
                                    asyncQueryHandler.startQuery(0, null, PATIENT_INFO_ALL_URI, null, null, null, "time desc");
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alert = builder.create();
                alert.show();
                break;
            case R.id.btn_edit:
                //初始化Builder
                builder = new AlertDialog.Builder(MainActivity.this);
                //加载自定义的那个View,同时设置下
                view_custom = getLayoutInflater().inflate(R.layout.view_dialog_custom_insert, null, false);
                final EditText insertNumber = (EditText) view_custom.findViewById(R.id.et_insert_number);
                final EditText insertName = (EditText) view_custom.findViewById(R.id.et_insert_name);
                final EditText insertSex = (EditText) view_custom.findViewById(R.id.et_insert_sex);
                final EditText insertResult = (EditText) view_custom.findViewById(R.id.et_insert_result);
                builder.setView(view_custom)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("number", insertNumber.getText() + "");
                                contentValues.put("name", insertName.getText() + "");
                                contentValues.put("sex", insertSex.getText() + "");
                                contentValues.put("time", TimeUtil.getTime());
                                contentValues.put("result", insertResult.getText() + "");
                                asyncQueryHandler.startInsert(0, null, PATIENT_INFO_ALL_URI, contentValues);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alert = builder.create();
                alert.show();
                break;
            case R.id.btn_preview:
                myCheckBoxAdapter.flag = !myCheckBoxAdapter.flag;

                if (myCheckBoxAdapter.flag) {
                    btnPreview.setText("取消");
                } else {
                    for (int i = 0; i < temp.size(); i++) {
                        temp.get(i).isCheck = false;
                    }
                    btnPreview.setText("编辑");
                }

                myCheckBoxAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_delete:
                if (myCheckBoxAdapter.flag == false) {
                    ToastUtil.showToast(MainActivity.this, "请先点击编辑按钮！");
                    break;
                }
                final List<PatientInfo> datas = new ArrayList<>();
                if (myCheckBoxAdapter.flag) {
                    //将选中的数据都放入一个集合中
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).isCheck) {
                            datas.add(temp.get(i));
                        }
                    }
                    if (datas.size() == 0) {
                        ToastUtil.showToast(MainActivity.this, "请先选择要删除的记录！");
                        break;
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("确认删除" + datas.size() + "条记录？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    //在原有的数据中删除所有被选中的数据，并更新适配器
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (PatientInfo patientInfo : datas) {
                                            Uri delUri = ContentUris.withAppendedId(PATIENT_INFO_ALL_URI, patientInfo.get_id());
                                            asyncQueryHandler.startDelete(0, null, delUri, null, null);
                                        }
                                        temp.removeAll(datas);
                                        myCheckBoxAdapter.flag = false;
                                        btnPreview.setText("编辑");
                                        myCheckBoxAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        alert = builder.create();
                        alert.show();
                    }
                }
                break;
        }
    }

    /**
     * 异步查询所有数据（按时间倒序排序）
     */
    private void query() {
        asyncQueryHandler.startQuery(0, null, PATIENT_INFO_ALL_URI, null, null, null, "time desc");
    }

    /**
     * 获取数据库中中的数据
     *
     * @param cursor
     * @param temp
     */
    private void getData(Cursor cursor, List<PatientInfo> temp) {
        temp.clear();
        //将数据库的_id取出保存在ArrayList中
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String sex = cursor.getString(cursor.getColumnIndex("sex"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String result = cursor.getString(cursor.getColumnIndex("result"));
                PatientInfo patientInfo2 = new PatientInfo(id, number, name, sex, time, result);
                temp.add(patientInfo2);
            } while (cursor.moveToNext());
        }

    }

    /**
     * 异步操作数据库的Handler类
     */
    public class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * 异步查询完成时调用
         *
         * @param token
         * @param cookie
         * @param cursor
         */
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor.getCount() == 0) {
                ToastUtil.showToast(MainActivity.this, "没有记录！");
            }
            getData(cursor, temp);
//            myCursorAdapter = new MyCursorAdapter(MainActivity.this, cursor,true);
//            listView.setAdapter(myCursorAdapter);
            myCheckBoxAdapter = new MyCheckBoxAdapter(MainActivity.this, temp);
            listView.setAdapter(myCheckBoxAdapter);
            cursor.close();

        }

        /**
         * 异步插入完成时调用
         *
         * @param token
         * @param cookie
         * @param uri
         */
        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            if (uri != null) {
                ToastUtil.showToast(MainActivity.this, "插入成功！");
            } else {
                ToastUtil.showToast(MainActivity.this, "插入成功！");
            }
        }

        /**
         * 异步更新完成时调用
         *
         * @param token
         * @param cookie
         * @param result
         */
        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if (result > 0) {
                ToastUtil.showToast(MainActivity.this, "更新成功！");
            } else {
                ToastUtil.showToast(MainActivity.this, "更新失败！");
            }
        }

        /**
         * 异步删除完成时调用
         *
         * @param token
         * @param cookie
         * @param result
         */
        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if (result > 0) {
                ToastUtil.showToast(MainActivity.this, "删除成功！");
            } else {
                ToastUtil.showToast(MainActivity.this, "删除成功！");
            }
        }

    }

    /**
     * 重写activity的onRestart方法，更新数据
     */
    @Override
    protected void onRestart() {
        asyncQueryHandler.startQuery(0, null, PATIENT_INFO_ALL_URI, null, null, null, "time desc");
//        cursor = getContentResolver().query(PATIENT_INFO_ALL_URI, null, null, null, null);
//        getData(cursor, temp);
//        myCursorAdapter.changeCursor(cursor);
        myCheckBoxAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    /**
     * 点击"退出"
     *
     * @param view
     */
    public void exit(View view) {
        finish();
        System.exit(0);
    }


    private long exitTime = 0;

    /**
     * 再按一次退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showToast(this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(patientInfoObserver);
        super.onDestroy();
    }
}
