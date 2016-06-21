package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyToast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int WHAT_VERIFY_TIMER = 1;
    private EditText et_username;
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private EditText et_phone;
    private EditText et_verification;
    private Button bt_verify;
    private Button bt_register;
    Handler handler;
    private int timeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case WHAT_VERIFY_TIMER:
                        if (timeCount > 0) {
                            bt_verify.setText(String.format("%s%s", timeCount--, getString(R.string.reVerify)));
                            handler.sendEmptyMessageDelayed(WHAT_VERIFY_TIMER,1000);
                        }else{
                            bt_verify.setText(getString(R.string.verify));
                            bt_verify.setEnabled(true);
                        }
                        break;
                }
            }
        };
        initView();
    }

    @Override
    protected void onDestroy() {
        if (handler != null){
            handler.removeMessages(WHAT_VERIFY_TIMER);
        }
        super.onDestroy();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_verification = (EditText) findViewById(R.id.et_verification);
        bt_verify = (Button) findViewById(R.id.bt_verify);
        bt_register = (Button) findViewById(R.id.bt_register);

        bt_verify.setOnClickListener(this);
        bt_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_verify:
                getVerification();
                break;
            case R.id.bt_register:
                register();
                break;
        }
    }

    /**
     * 向服务器请求验证码
     */
    private void getVerification() {
        bt_verify.setEnabled(false);
        timeCount = 60;
        handler.sendEmptyMessageDelayed(WHAT_VERIFY_TIMER,1000);
    }

    private void register() {

        String username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            MyToast.show("用户名不能为空");
            ObjectAnimator.ofFloat(et_username, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            MyToast.show("密码不能为空");
            ObjectAnimator.ofFloat(et_pwd, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String confirm = et_pwd_confirm.getText().toString().trim();
        if (TextUtils.isEmpty(confirm)) {
            MyToast.show("密码不能为空");
            ObjectAnimator.ofFloat(et_pwd_confirm, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (!confirm.equals(pwd)) {
            MyToast.show("两次密码不一致");
            ObjectAnimator.ofFloat(et_pwd_confirm, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            MyToast.show("手机号不能为空");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (!phone.matches("[1][358]\\d{9}")) {
            MyToast.show("手机号格式不正确");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String verification = et_verification.getText().toString().trim();
        if (TextUtils.isEmpty(verification)) {
            MyToast.show("验证码不能为空");
            ObjectAnimator.ofFloat(et_verification, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        //TODO 注册接口
        MyToast.show("注册");
    }
}
