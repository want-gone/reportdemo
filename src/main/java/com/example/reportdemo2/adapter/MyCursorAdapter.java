package com.example.reportdemo2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.reportdemo2.R;

/**
 * Created by WangGang on 2016/8/19.
 * 自定义游标适配器
 */
public class MyCursorAdapter extends CursorAdapter {

    public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listview_information, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        if (cursor != null) {

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String sex = cursor.getString(cursor.getColumnIndex("sex"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String result = cursor.getString(cursor.getColumnIndex("result"));

            viewHolder.tvPatientNumber.setText(number);
            viewHolder.tvPatientName.setText(name);
            viewHolder.tvPatientSex.setText(sex);
            viewHolder.tvPatientTime.setText(time);
            viewHolder.tvPatientResult.setText(result);

        }
    }

    class ViewHolder {

        public TextView tvPatientNumber;
        public TextView tvPatientName;
        public TextView tvPatientSex;
        public TextView tvPatientTime;
        public TextView tvPatientResult;
        public CheckBox checkboxOperateData;

        public ViewHolder(View view) {
            view.setTag(this);
            tvPatientNumber = (TextView) view.findViewById(R.id.tv_patient_number);
            tvPatientName = (TextView) view.findViewById(R.id.tv_patient_name);
            tvPatientSex = (TextView) view.findViewById(R.id.tv_patient_sex);
            tvPatientTime = (TextView) view.findViewById(R.id.tv_patient_time);
            tvPatientResult = (TextView) view.findViewById(R.id.tv_patient_result);
            checkboxOperateData = (CheckBox) view.findViewById(R.id.checkbox_operate_data);
        }
    }
}
