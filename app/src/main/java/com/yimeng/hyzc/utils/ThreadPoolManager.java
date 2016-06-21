package com.yimeng.hyzc.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理器
 */
public class ThreadPoolManager {

    private ThreadPoolProxy poolProxy;
    private ThreadPoolProxy shortThreadPool;

    /*单例 表示 只有一个线程池管理器*/
//    1.  私有化构造方法
    private ThreadPoolManager() {
    }

    //2.  懒汉式 饿汉式 (线程安全的)
    private static ThreadPoolManager instatnce = new ThreadPoolManager();

    public static ThreadPoolManager getInstatnce() {
        return instatnce;
    }


    /**
     * 1. 联网
     * 2. 文件操作 耗时较少
     * <p/>
     * <p/>
     * cpu * 2 + 1
     *
     * @return
     */
    public ThreadPoolProxy createThreadPool() {
        if (poolProxy == null)
            poolProxy = new ThreadPoolProxy(5, 5, 5000);
        return poolProxy;
    }


    public ThreadPoolProxy createShortThreadPool() {
        if (shortThreadPool == null)
            shortThreadPool = new ThreadPoolProxy(3, 3, 5000);
        return shortThreadPool;
    }


    /**
     * 线程池的配置
     */
    public class ThreadPoolProxy {
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;
        ThreadPoolExecutor threadPoolExecutor;

        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {

            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }


        /**
         * 执行线程任务
         *
         * @param runnable
         */
        public void execute(Runnable runnable) {
            if (threadPoolExecutor == null) {
                /**
                 * 1. corePoolSize 创建线程池的初始化线程数量
                 * 2. maximumPoolSize 除了 初始化的线程数量 额外可以开启的下次数量
                 * 3. keepAliveTime  线程存活时间  没有任务的时候
                 */
                threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime
                        , TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10));
            }

            threadPoolExecutor.execute(runnable);
        }

        /**
         * 取消任务
         *
         * @param runnable
         */
        public void cancel(Runnable runnable) {
            //   不为空  不崩溃  不停止
            if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown() && !threadPoolExecutor.isTerminated()) {
                threadPoolExecutor.remove(runnable);
            }
        }


    }

}
