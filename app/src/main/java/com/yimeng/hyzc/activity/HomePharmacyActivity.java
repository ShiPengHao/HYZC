package com.yimeng.hyzc.activity;

import android.content.SharedPreferences;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyConstant;

/**
 * 医生主界面
 */
public class HomePharmacyActivity extends BaseActivity {
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pharmacy_home;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        // 更新首次运行标志
        SharedPreferences spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        if (spAccount.getBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, true)) {
            //TODO 首次运行提示
            spAccount.edit().putBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, false).apply();
        }
    }
}
