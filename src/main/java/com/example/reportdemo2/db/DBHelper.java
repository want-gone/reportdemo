package com.example.reportdemo2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.reportdemo2.bean.PatientInfo;
import com.example.reportdemo2.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangGang on 2016/8/19.
 * 数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper {
    String sql = " CREATE TABLE IF NOT EXISTS patientInfo" +
            " ( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            " number STRING," +
            " name STRING," +
            " sex STRING," +
            " time STRING," +
            " result STRING)";

    private static final String DATABASE_NAME = "ecg_file.db";
    private static final int DATABASE_VERSION = 1;
    private List<PatientInfo> patientInfos;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);//创建数据库
        initData(db);
    }

    /**
     * 该方法在版本升级时调用
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS patientInfo");
        onCreate(db);
    }

    /**
     * 初始化数据库
     * @param db
     */
    private void initData(SQLiteDatabase db) {
        patientInfos = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            PatientInfo patientInfo = new PatientInfo("2016082" + i, "张三" + i, "男" + i, TimeUtil.getTime(), "未知结果" + i);
            patientInfos.add(patientInfo);
        }
        db.beginTransaction();
        for (PatientInfo patientInfo : patientInfos) {
            ContentValues values = new ContentValues();
            values.put("number", patientInfo.getNumber());
            values.put("name", patientInfo.getName());
            values.put("sex", patientInfo.getSex());
            values.put("time", patientInfo.getTime());
            values.put("result", patientInfo.getResult());
            db.insertOrThrow("patientInfo", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
