package com.yimeng.hyzc.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Created by 依萌 on 2016/6/13.
 */
public class MyLog {
    /**
     * 日志打印的开关
     */
    private static final boolean ENABLE = true;

    public static void i(String tag, String msg) {
        if (ENABLE) {
            Log.i(tag, msg);
        }
    }

    public static void i(Class<?> cls, String msg) {
        if (ENABLE) {
            Log.i(cls.getSimpleName(), msg);
        }
    }

}
