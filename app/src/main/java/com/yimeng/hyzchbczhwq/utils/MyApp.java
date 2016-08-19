package com.yimeng.hyzchbczhwq.utils;

import android.app.Application;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * 应用实例
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ThreadUtils.runOnUIThread(null);
//        Picasso.setSingletonInstance(Picasso.with(this));
        initHttpUtils();
        initJPush();
    }

    /**
     * 初始化jpush
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    /**
     * 初始化httputils
     */
    private void initHttpUtils() {
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    private static MyApp instance;

    public static MyApp getAppContext() {
        return instance;
    }



}
