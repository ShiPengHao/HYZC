package com.yimeng.hyzc.utils;

import android.app.Application;

import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by 依萌 on 2016/6/20.
 */
public class MyApp extends Application {

    private static OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ThreadUtils.runOnUIThread(null);
//        Picasso.setSingletonInstance(Picasso.with(this));
        initHttpUtils();
    }

    private void initHttpUtils() {
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));

        //                .addInterceptor(new LoggerInterceptor("TAG"))
//其他配置
        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    private static MyApp instance;

    public static MyApp getAppContext() {
        return instance;
    }

    public static OkHttpClient getHttpClient(){return okHttpClient;}



}
