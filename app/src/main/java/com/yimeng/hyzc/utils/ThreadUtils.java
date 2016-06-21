package com.yimeng.hyzc.utils;

import android.os.Handler;

public class ThreadUtils {

    private static Handler handler = new Handler();

    /**
     * 在主线程执行
     *
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable) {
        if (runnable != null){
            handler.post(runnable);
        }
    }

}
