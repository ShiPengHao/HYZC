package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.BitmapUtils;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyLog;
import com.yimeng.hyzc.utils.ThreadUtils;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 闪屏界面，处理apk版本更新，自动登陆，页面跳转等逻辑
 */
public class SplashActivity extends BaseActivity {

    private SharedPreferences spAccount;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getImage();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        if (isFirstRunning()) {
            copyDataToLocal();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToIntroduce();
                }
            }, 2000);
        } else if (isAutoUpdate()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attemptToUpdate();
                }
            }, 2000);
        } else if (isAutoLogin()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attemptToLogin();
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

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 获得闪屏界面的背景图片
     */
    private void getImage() {
        ImageView iv = (ImageView) findViewById(R.id.iv);
        if (iv != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash);
            iv.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, DensityUtil.SCREEN_WIDTH, DensityUtil.SCREEN_HEIGHT));
            bitmap.recycle();
        }
    }

    /**
     * 将资源文件拷贝到本地
     */
    private void copyDataToLocal() {

    }


    /**
     * 自动登陆
     */
    private void attemptToLogin() {
        String username = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, "");
        String pwd = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, "");
        String type = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
        String method = null;
        if (type.equalsIgnoreCase("patient")){
            method = "Patient_Login";
        }else if(type.equalsIgnoreCase("doctor")){
            method = "Doctor_Login";
        }else if(type.equalsIgnoreCase("shop")){
            method = "Shop_Login";
        }
        if (TextUtils.isEmpty(method)){
            goToLogin();
            return;
        }
        HashMap<String ,Object> param = new HashMap<>();
        param.put("user", username);
        param.put("pwd", pwd);
        requestLogin(method,param);
    }

    /**
     * 执行异步任务，登录
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void requestLogin(Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                    if (result == null){
                        goToLogin();
                        return null;
                    }
                    try {
//                      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            String type = object.optString("type");
                            String id = object.optString("id");
                            setJPushAliasAndTag(type, id);
                        } else {
                            goToLogin();
                        }
                    } catch (Exception e) {
                        goToLogin();
                        e.printStackTrace();
                    }
                }else {
                    goToLogin();
                }
                return null;
            }
        }.execute(params);
    }

    /**
     * 登陆成功后为本应用用户绑定JPush的别名和标签，别名为账号类型+"-"+id，标签为账号类型，设置成功以后跳转到主页
     */
    private void setJPushAliasAndTag(final String type, final String id) {
        final HashSet<String> tags = new HashSet<>();
        tags.add(type);
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                JPushInterface.setAliasAndTags(MyApp.getAppContext(), type + "+" + id, tags, new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        if (i != 0) {
                            MyLog.i("JPush", "set alias and tag error");
                            goToLogin();
                        }else{
                            goToHome(type);
                        }
                    }
                });
            }
        });
    }

    /**
     * 更新版本
     */
    private void attemptToUpdate() {
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
    private void goToHome(String type) {
        startActivity(new Intent(this, HomePatientActivity.class));
        finish();
    }

    /**
     * 跳转到引导界面
     */
    private void goToIntroduce() {
        startActivity(new Intent(this, IntroduceActivity.class));
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
