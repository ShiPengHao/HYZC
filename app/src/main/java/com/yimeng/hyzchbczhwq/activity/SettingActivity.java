package com.yimeng.hyzchbczhwq.activity;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.huanxin.PreferenceManager;
import com.yimeng.hyzchbczhwq.utils.MyConstant;


/**
 * 设置界面
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ToggleButton tb_sound;
    private ToggleButton tb_vibrate;
    private ToggleButton tb_autoLogin;
    private SharedPreferences sharedPreferences;
    private ImageView iv_back;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        tb_sound = (ToggleButton) findViewById(R.id.tb_sound);
        tb_vibrate = (ToggleButton) findViewById(R.id.tb_vibrate);
        tb_sound.setChecked(PreferenceManager.getInstance().getSettingMsgSound());
        tb_vibrate.setChecked(PreferenceManager.getInstance().getSettingMsgVibrate());

        tb_autoLogin = (ToggleButton) findViewById(R.id.tb_autoLogin);
        sharedPreferences = context.getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        tb_autoLogin.setChecked(sharedPreferences.getBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, false));

        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        tb_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getInstance().setSettingMsgSound(isChecked);
            }
        });
        tb_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getInstance().setSettingMsgVibrate(isChecked);
            }
        });
        tb_autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, isChecked).apply();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


}
