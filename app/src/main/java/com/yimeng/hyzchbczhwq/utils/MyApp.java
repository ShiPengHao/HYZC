package com.yimeng.hyzchbczhwq.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.huanxin.HuanXinHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.OkHttpClient;

/**
 * 应用实例
 */
public class MyApp extends Application implements Thread.UncaughtExceptionHandler {

    private CopyOnWriteArrayList<Activity> activities = new CopyOnWriteArrayList<>();
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private Toast toast;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ThreadUtils.runOnUIThread(null);
        activities.clear();

        Thread.currentThread().setUncaughtExceptionHandler(this);
//        Picasso.setSingletonInstance(Picasso.with(this));
        initHttpUtils();
        initJPush();
        HuanXinHelper.getInstance().init(this);
    }

    /**
     * 启动activity时添加至集合
     *
     * @param activity 加入的activity
     */
    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    /**
     * 销毁activity时从集合移除
     *
     * @param activity 销毁的activity
     */
    public void removeActivity(Activity activity) {
        if (activities.contains(activity))
            activities.remove(activity);
    }

    /**
     * 完全退出本应用
     */
    public void finish() {
        EMClient.getInstance().logout(true);
        stopJPush();
        for (int j = 0; j < activities.size(); j++) {
            activities.get(j).finish();
        }

        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        activityManager.killBackgroundProcesses(getPackageName());
    }

    /**
     * 用清空别名的方式停止极光推送服务
     */
    private void stopJPush() {
        JPushInterface.setAliasAndTags(MyApp.getAppContext(), "", new HashSet<String>(), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
    }

    /**
     * 初始化jpush
     */
    private void initJPush() {
        JPushInterface.setDebugMode(false);
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


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                toast = Toast.makeText(instance, "抱歉，" + getString(R.string.app_name) + "程序崩溃，应用将退出！", Toast.LENGTH_SHORT);
                toast.show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo();
        //保存日志文件
        saveCrashInfo2File(ex);
        SystemClock.sleep(2000);
        MyToast.close();
        if (toast != null)
            toast.cancel();
        finish();
    }

    /**
     * 收集设备参数信息
     */
    public void collectDeviceInfo() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到sdcard/crash/目录文件中
     *
     * @param ex 异常
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crash/" + getString(R.string.apk_short_name) + "/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
