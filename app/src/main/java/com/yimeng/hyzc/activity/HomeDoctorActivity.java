package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.yimeng.hyzc.R;

/**
 * 医生主界面
 */
public class HomeDoctorActivity extends BaseActivity implements View.OnClickListener {

    private Button bt_appoint;
    private Button bt_personal;
    private Button bt_test;

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
}
