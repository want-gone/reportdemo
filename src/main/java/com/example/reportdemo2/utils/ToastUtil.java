package com.example.reportdemo2.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by WangGang on 2016/8/23.
 * 封装的Toast工具类
 */
public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}