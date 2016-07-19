package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyToast;

/**
 * 医生主界面
 */
public class HomeDoctorActivity extends BaseActivity implements View.OnClickListener {

    private Button bt_appoint;
    private Button bt_personal;
    private Button bt_test;
    private long preTime;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_home;
    }

    @Override
    protected void initView() {
        bt_appoint = (Button)findViewById(R.id.bt_appoint);
        bt_personal = (Button)findViewById(R.id.bt_personal);
        bt_test = (Button)findViewById(R.id.bt_test);
    }

    @Override
    protected void setListener() {
        bt_appoint.setOnClickListener(this);
        bt_personal.setOnClickListener(this);
        bt_test.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_appoint:
                startActivity(new Intent(this,AppointHistoryActivity.class));
                break;
            case R.id.bt_personal:
                startActivity(new Intent(this,AccountInfoActivity.class));
                break;
            case R.id.bt_test:
                startActivity(new Intent(this,TestActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long now = SystemClock.uptimeMillis();
                if (preTime == -1 || (now - preTime) > 2000) {
                    preTime = now;
                    MyToast.show(getString(R.string.once_more_to_quit));
                } else {
                    MyToast.close();
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
