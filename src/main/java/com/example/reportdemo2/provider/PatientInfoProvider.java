package com.example.reportdemo2.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.reportdemo2.db.DBHelper;

/**
 * Created by WangGang on 2016/8/19.
 * 自定义ContentProvider
 */
public class PatientInfoProvider extends ContentProvider {

    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.wang.patientInfo";
    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wang.patientInfo";
    private static String AUTHORITY = "com.wang.patientInfo.provider";
    public static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/patientInfo");
    private static final UriMatcher matcher;

    private static final int PERSON_ALL = 0;
    private static final int PERSON_ONE = 1;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "patientInfo", PERSON_ALL);
        matcher.addURI(AUTHORITY, "patientInfo/#", PERSON_ONE);
    }

    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = matcher.match(uri);
        switch (match) {
            case PERSON_ALL:
                return CONTENT_TYPE;
            case PERSON_ONE:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
    }

    //查询数据
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = helper.getReadableDatabase();
        int match = matcher.match(uri);
        switch (match) {
            case PERSON_ALL:
                break;
            case PERSON_ONE:
                long _id = ContentUris.parseId(uri);
                selection = "_id=?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        return db.query("patientInfo", projection, selection, selectionArgs, null, null, sortOrder);
    }

    //插入数据
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = matcher.match(uri);
        if (match != PERSON_ALL) {
            throw new IllegalArgumentException("Wrong URI:" + uri);
        }
        db = helper.getReadableDatabase();
        if (values == null) {
            values = new ContentValues();
            values.put("number", "00000000");
            values.put("name", "no name");
            values.put("sex", "null");
            values.put("time", "0000-00-00");
            values.put("result", "no info");
        }
        long rowId = db.insert("patientInfo", null, values);
        if (rowId > 0) {
            notifyDataChanged();
            return ContentUris.withAppendedId(uri, rowId);
        }
        return null;
    }

    //删除数据
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = helper.getWritableDatabase();
        int match = matcher.match(uri);
        switch (match) {
            case PERSON_ALL:
                break;
            case PERSON_ONE:
                long _id = ContentUris.parseId(uri);
                selection = "_id=?";
                selectionArgs = new String[]{String.valueOf(_id)};
        }
        int count = db.delete("patientInfo", selection, selectionArgs);
        if (count > 0) {
            notifyDataChanged();
        }
        return count;
    }

    //修改数据
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = helper.getReadableDatabase();
        int match = matcher.match(uri);
        switch (match) {
            case PERSON_ALL:
                break;
            case PERSON_ONE:
                long _id = ContentUris.parseId(uri);
                selection = "_id=?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        int count = db.update("patientInfo", values, selection, selectionArgs);
        if (count > 0) {
            notifyDataChanged();
        }
        return count;
    }

    /**
     * 通知监听者有数据更新
     */
    private void notifyDataChanged() {
        getContext().getContentResolver().notifyChange(NOTIFY_URI, null);
    }
}
