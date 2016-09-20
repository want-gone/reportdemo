package com.example.reportdemo2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.reportdemo2.R;
import com.example.reportdemo2.bean.PatientInfo;

import java.util.List;

/**
 * 结合checkbox的adapter
 * Created by WangGang on 2016/8/12.
 */
public class MyCheckBoxAdapter extends BaseAdapter {

    private Context mContext;

    private List<PatientInfo> mData;

    private LayoutInflater mInflater;

    public boolean flag = false;


    public MyCheckBoxAdapter(Context mContext, List<PatientInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mData != null ? mData.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {

            view = mInflater.inflate(R.layout.item_listview_information, null);
            holder = new ViewHolder(view);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        final PatientInfo dataBean = mData.get(position);
        if (dataBean != null) {
            holder.tvPatientNumber.setText(dataBean.getNumber());
            holder.tvPatientName.setText(dataBean.getName());
            holder.tvPatientSex.setText(dataBean.getSex());
            holder.tvPatientTime.setText(dataBean.getTime());
            holder.tvPatientResult.setText(dataBean.getResult());

            // 根据isSelected来设置checkbox的显示状况
            if (flag) {
                holder.checkboxOperateData.setVisibility(View.VISIBLE);
            } else {
                holder.checkboxOperateData.setVisibility(View.GONE);
            }

            holder.checkboxOperateData.setChecked(dataBean.isCheck);

            //注意这里设置的不是onCheckedChangListener，还是值得思考一下的
            holder.checkboxOperateData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataBean.isCheck) {
                        dataBean.isCheck = false;
                    } else {
                        dataBean.isCheck = true;
                    }
                }
            });

        }
        return view;
    }

    static class ViewHolder {

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

