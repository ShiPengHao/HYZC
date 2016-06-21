package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spAccount = getSharedPreferences(MyConstant.SPREFS_ACCOUNT, MODE_PRIVATE);
        initView();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_username.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, ""));
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, ""));
        cb_remember = (CheckBox) findViewById(R.id.cb_remeber);
        cb_remember.setChecked(spAccount.getBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, false));
        cb_auto = (CheckBox) findViewById(R.id.cb_auto);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);

        cb_auto.setOnCheckedChangeListener(this);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                startActivity(new Intent(this, RegisterActivity.class));
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

        OkHttpUtils.post().url(MyConstant.URL_LOGIN)
                .addParams("usercode", username).addParams("password", pwd).addParams("expired","365")
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int i) throws Exception {
                String string = response.body().string();
                JSONObject object = new JSONObject(string);
                //{"ResultID":0,"ResultMsg":"登录成功","Succeed":true,"ResultData":null,"s":true,"emsg":"登录成功"}
                MyToast.show(object.optString("ResultMsg"));
                if(object.optBoolean("Succeed")){
                    saveAccountInfo();
                    goToHome();
                }
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object o, int i) {

            }
        });

    }

    /**
     * 保存登陆成功的信息到本地sp文件中
     */
    private void saveAccountInfo() {
        SharedPreferences.Editor editor = spAccount.edit();
        editor.putString(MyConstant.KEY_ACCOUNT_LAST_USERNAME,username)
                .putBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, cb_remember.isChecked())
                .putBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN,cb_auto.isChecked());
        if (cb_remember.isChecked()){
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD,pwd);
        }else{
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD,"");
        }
        editor.apply();
    }

    /**
     * 跳转到主页
     */
    private void goToHome() {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == cb_auto.getId() && cb_auto.isChecked()){
            cb_remember.setChecked(true);
        }
    }
}
