package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.ThreadUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 闪屏界面，处理apk版本更新，自动登陆，页面跳转等逻辑
 */
public class SplashActivity extends AppCompatActivity {

    private SharedPreferences spAccount;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        spAccount = getSharedPreferences(MyConstant.SPREFS_ACCOUNT, MODE_PRIVATE);
        if (isFirstRunning()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToIntroduce();
                    spAccount.edit().putBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING,false).apply();
                }
            }, 2000);
        } else if (isAutoUpdate()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attempToUpdate();
                }
            }, 2000);
        } else if (isAutoLogin()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attempToLogin();
                }
            }, 2000);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, 2000);
        }
    }

    /**
     * 自动登陆
     */
    private void attempToLogin() {
        String username = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME,"");
        String pwd = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD,"");
        OkHttpUtils.post().url(MyConstant.URL_LOGIN)
                .addParams("usercode", username).addParams("password", pwd).addParams("expired","365")
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int i) throws Exception {
                String string = response.body().string();
                JSONObject object = new JSONObject(string);
                //{"ResultID":0,"ResultMsg":"登录成功","Succeed":true,"ResultData":null,"s":true,"emsg":"登录成功"}
                // MyToast.show(object.optString("ResultMsg"));
                if(object.optBoolean("Succeed")){
                    goToHome();
                }else{
                    goToLogin();
                }
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
                goToLogin();
            }

            @Override
            public void onResponse(Object o, int i) {

            }
        });
    }

    /**
     * 更新版本
     */
    private void attempToUpdate() {
    }

    /**
     * 跳转到登陆页面
     */
    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * 跳转到主页
     */
    private void goToHome() {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    /**
     * 跳转到引导界面
     */
    private void goToIntroduce() {
        startActivity(new Intent(this,IntroduceActivity.class));
        finish();
    }

    /**
     * 判断本应用是否在本机首次运行
     *
     * @return
     */
    private boolean isFirstRunning() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, true);
    }

    /**
     * 判断是否设置自动更新
     *
     * @return
     */
    private boolean isAutoUpdate() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_AUTOUPDATE, false);
    }

    /**
     * 判断是否设置自动登陆
     *
     * @return
     */
    private boolean isAutoLogin() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, false);
    }
}
