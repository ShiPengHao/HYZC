package com.yimeng.hyzc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yimeng.hyzc.R;

import cn.jpush.android.api.JPushInterface;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        initView();
        setListener();
        initData();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 获得布局资源id
     *
     * @return 资源id
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化控件，获得控件引用
     */
    protected abstract void initView();

    /**
     * 为控件设置监听和适配器
     */
    protected abstract void setListener();

    /**
     * 为控件绑定数据
     */
    protected abstract void initData();


}
