package com.yimeng.hyzc.utils;

import android.os.Handler;

import java.util.concurrent.ScheduledThreadPoolExecutor;

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


    /**
     * 在子线程执行
     * @param runnable 要执行的代码块
     */
    public static void runOnBackThread(Runnable runnable){
        //  show ---> 新的线程 (线程池)
        ThreadPoolManager.getInstatnce().createThreadPool().execute(runnable);
    }

}
