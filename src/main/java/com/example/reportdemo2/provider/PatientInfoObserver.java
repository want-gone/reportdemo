package com.example.reportdemo2.provider;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;

/**
 * Created by WangGang on 2016/8/19.
 * 自定义的内容观察者
 */
public class PatientInfoObserver extends ContentObserver {
    private Handler handler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public PatientInfoObserver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    /**
     * 当ContentProvider中的uri发生改变时调用
     * @param selfChange
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Message message = new Message();
        handler.sendMessage(message);
    }
}
