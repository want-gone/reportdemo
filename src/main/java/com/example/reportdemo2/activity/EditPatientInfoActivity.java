package com.example.reportdemo2.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reportdemo2.R;
import com.example.reportdemo2.bean.PatientInfo;
import com.example.reportdemo2.utils.ToastUtil;

/**
 * 信息修改界面
 */
public class EditPatientInfoActivity extends AppCompatActivity {

    private TextView tvNumber;
    private TextView tvBack;
    private TextView tvSave;
    private EditText etName;
    private EditText etSex;
    private EditText etAge;
    private EditText etHeight;
    private EditText etWeight;
    private EditText etResult;
    private PatientInfo patientInfo;
    private ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_infos);
        resolver = getContentResolver();
        patientInfo = (PatientInfo) getIntent().getSerializableExtra("patientInfo");
        initView();
        initData();
        initListener();

    }

    /**
     * 点击事件的注册
     */
    private void initListener() {

        /**
         * “返回”的点击事件
         */
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * “保存”的点击事件
         */
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("name", etName.getText().toString());
                values.put("sex", etSex.getText().toString());
                values.put("result", etResult.getText().toString());
                Uri updateUri = ContentUris.withAppendedId(MainActivity.PATIENT_INFO_ALL_URI, patientInfo.get_id());
                int update = getContentResolver().update(updateUri, values, null, null);
                if (update > 0){
                    ToastUtil.showToast(EditPatientInfoActivity.this, "更新成功！");
                }
            }
        });
    }

    /**
     * 数据的初始化
     */
    private void initData() {
        if (patientInfo != null) {
            tvNumber.setText(patientInfo.getNumber());
            etName.setText(patientInfo.getName());
            etSex.setText(patientInfo.getSex());
            etResult.setText(patientInfo.getResult());
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvSave = (TextView) findViewById(R.id.tv_save);
        etName = (EditText) findViewById(R.id.et_name);
        etSex = (EditText) findViewById(R.id.et_sex);
        etAge = (EditText) findViewById(R.id.et_age);
        etHeight = (EditText) findViewById(R.id.et_height);
        etWeight = (EditText) findViewById(R.id.et_weight);
        etResult = (EditText) findViewById(R.id.et_result);
    }
}
