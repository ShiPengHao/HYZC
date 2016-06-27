package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 登陆界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

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

    private final String NAMESPACE = "http://192.168.0.108:888/";
    private final String WEB_SERVICE_URL = "http://192.168.0.108:888/API/ymOR_WebService.asmx";
    private Map<String, Object> values = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        initView();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        // 回显上次登录账号
        et_username.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, ""));
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        // 回显上次登录账号
        et_pwd.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, ""));
        cb_remember = (CheckBox) findViewById(R.id.cb_remeber);
        // 回显上次登录账号
        cb_remember.setChecked(spAccount.getBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, false));
        cb_auto = (CheckBox) findViewById(R.id.cb_auto);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);

        rg_userType = (RadioGroup) findViewById(R.id.rg_type);
        rg_userType.check(R.id.rb_patient);

        cb_auto.setOnCheckedChangeListener(this);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                startActivity(new Intent(this, RegisterActivity.class).putExtra("checdId",rg_userType.getCheckedRadioButtonId()));
                break;
            case R.id.bt_login:
                login();
                break;
        }
    }

    /**
     * 登陆
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

//        OkHttpUtils.post().url(MyConstant.URL_LOGIN)
//                .addParams("usercode", username).addParams("password", pwd).addParams("expired","365")
//                .build().execute(new Callback() {
//            @Override
//            public Object parseNetworkResponse(Response response, int i) throws Exception {
//                String string = response.body().string();
//                JSONObject object = new JSONObject(string);
//                //{"ResultID":0,"ResultMsg":"登录成功","Succeed":true,"ResultData":null,"s":true,"emsg":"登录成功"}
//                MyToast.show(object.optString("ResultMsg"));
//                if(object.optBoolean("Succeed")){
//                    saveAccountInfo();
//                    goToHome();
//                }else {
//                    MyToast.show(getString(R.string.connet_error));
//                }
//                return null;
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int i) {
//                e.printStackTrace();
//                MyToast.show(getString(R.string.connet_error));
//            }
//
//            @Override
//            public void onResponse(Object o, int i) {
//
//            }
//        });

        values.clear();
        values.put("user", username);
        values.put("pwd", pwd);
        switch (rg_userType.getCheckedRadioButtonId()) {
            case R.id.rb_patient:
                request("Patient_Login", values);
                break;
            case R.id.rb_doctor:
                request("Doctor_Login", values);
                break;
            case R.id.rb_pharmacy:
                request("Shop_Login", values);
                break;
        }

    }

    /**
     * 保存登陆成功的信息到本地sp文件中
     */
    private void saveAccountInfo() {
        SharedPreferences.Editor editor = spAccount.edit();
        editor.putString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, username)
                .putBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, cb_remember.isChecked())
                .putBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, cb_auto.isChecked());
        if (cb_remember.isChecked()) {
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, pwd);
        } else {
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, "");
        }
        editor.apply();
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void request(Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    return WebServiceUtils.callWebService(WEB_SERVICE_URL, NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                } else if (params != null && params.length == 1) {
                    return WebServiceUtils.callWebService(WEB_SERVICE_URL, NAMESPACE, (String) params[0],
                            null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
//                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        MyToast.show(object.optString("msg"));
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            saveAccountInfo();
                            goToHome();
                        }
                    } catch (JSONException e) {
                        MyToast.show(getString(R.string.connet_error));
                        e.printStackTrace();
                    }
                } else {
                    MyToast.show(getString(R.string.connet_error));
                }
            }

        }.execute(params);
    }

    /**
     * 跳转到主页
     */
    private void goToHome() {
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
