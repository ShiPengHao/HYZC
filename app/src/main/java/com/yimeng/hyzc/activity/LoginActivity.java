package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyLog;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.ThreadUtils;
import com.yimeng.hyzc.utils.WebServiceUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText et_username;
    private EditText et_pwd;
    private CheckBox cb_remember;
    private CheckBox cb_auto;
    private Button bt_register;
    private Button bt_login;
    private SharedPreferences spAccount;
    private String username;
    private String pwd;
    private RadioGroup rg_userType;
    private Map<String, Object> values = new HashMap<>();
    private LinearLayout ll_loading;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    protected void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        cb_remember = (CheckBox) findViewById(R.id.cb_remeber);
        cb_auto = (CheckBox) findViewById(R.id.cb_auto);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);
        rg_userType = (RadioGroup) findViewById(R.id.rg_type);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

    }

    @Override
    protected void setListener() {
        cb_auto.setOnCheckedChangeListener(this);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        rg_userType.check(R.id.rb_patient);
        ll_loading.setVisibility(View.GONE);
        spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        // 回显上次登录账号
        et_username.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, ""));
        // 回显上次登录账号密码
        et_pwd.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, ""));
        // 回显上次登录账号记住密码
        cb_remember.setChecked(spAccount.getBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, false));

        testOldInterface();
    }

    /**
     * 用老登陆接口登陆，获得cookie，方便在主页中请求药品数据
     */
    private void testOldInterface() {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpUtils.post()
                            .url(MyConstant.URL_LOGIN)
                            .addParams("usercode", "sysadmin")
                            .addParams("password", "123")
                            .addParams("expired", "365")
                            .build()
                            .execute()
                            .close();
                } catch (IOException e) {
                    e.printStackTrace();
                    MyLog.i("old", "fail");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                goToRegister();
                break;
            case R.id.bt_login:
                login();
                break;
        }
    }

    /**
     * 去注册，意图对象携带选择的注册类型信息，默认是普通病人
     */
    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class).putExtra("checdId", rg_userType.getCheckedRadioButtonId()));
    }

    /**
     * 登陆信息核对无误后登陆//TODO 线程阻塞
     */
    private void login() {
        username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            MyToast.show("用户名不能为空");
            ObjectAnimator.ofFloat(et_username, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            MyToast.show("密码不能为空");
            ObjectAnimator.ofFloat(et_pwd, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        values.clear();
        values.put("user", username);
        values.put("pwd", pwd);
        switch (rg_userType.getCheckedRadioButtonId()) {
            case R.id.rb_patient:
                requestLogin("Patient_Login", values);
                break;
            case R.id.rb_doctor:
                requestLogin("Doctor_Login", values);
                break;
            case R.id.rb_pharmacy:
                requestLogin("Shop_Login", values);
                break;
        }

    }

    /**
     * 执行异步任务，登录
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void requestLogin(Object... params) {
        ll_loading.setVisibility(View.VISIBLE);
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                } else if (params != null && params.length == 1) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        MyLog.i("result", result);
//                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            String type = object.optString("type");
                            String id = object.optString("id");
                            setJPushAliasAndTag(type, id);
                            saveAccountInfo(type, id);
                            goToHome(type);
                        } else {
                            MyToast.show(object.optString("msg"));
                            ll_loading.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        MyToast.show(getString(R.string.connet_error));
                        ll_loading.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    ll_loading.setVisibility(View.GONE);
                    MyToast.show(getString(R.string.connet_error));
                }
            }
        }.execute(params);
    }

    /**
     * 登陆成功后为本应用用户绑定JPush的别名和标签，别名为账号类型+"-"+id，标签为账号类型
     */
    private void setJPushAliasAndTag(String type, String id) {
        HashSet<String> tags = new HashSet<>();
        tags.add(type);
        JPushInterface.setAliasAndTags(MyApp.getAppContext(), type + "+" + id, tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i != 0) {
                    MyLog.i("JPush", "set alias and tag error");
                }
            }
        });
    }

    /**
     * 保存登陆成功的账号信息到本地sp文件中
     */
    private void saveAccountInfo(String type, String id) {
        SharedPreferences.Editor editor = spAccount.edit();
        editor.putString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, username)
                .putBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, cb_remember.isChecked())
                .putBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, cb_auto.isChecked())
                .putString(MyConstant.KEY_ACCOUNT_LAST_ID, id)
                .putString(MyConstant.KEY_ACCOUNT_LAST_TYPE, type);
        if (cb_remember.isChecked()) {
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, pwd);
        } else {
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, "");
        }
        editor.apply();
    }

    /**
     * 跳转到主页，根据账号类型判断
     */
    private void goToHome(String type) {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == cb_auto.getId() && cb_auto.isChecked()) {
            cb_remember.setChecked(true);
        }
    }
}
