package com.example.reportdemo2.bean;

import java.io.Serializable;

/**
 * Created by WangGang on 2016/8/10.
 * 病人信息类
 */
public class PatientInfo implements Serializable{
    private int _id; //数据库id
    private String number; //编号
    private String name; //姓名
    private String sex; //性别
    private String time; //时间
    private String result; //结果
    public boolean isCheck;

    public PatientInfo() {

    }

    public PatientInfo(String number, String name, String sex, String time, String result) {
        this.number = number;
        this.name = name;
        this.sex = sex;
        this.time = time;
        this.result = result;
    }

    public PatientInfo(int _id,String number, String name, String sex, String time, String result) {
        this._id = _id;
        this.number = number;
        this.name = name;
        this.sex = sex;
        this.time = time;
        this.result = result;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "PatientInfo{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", time='" + time + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
