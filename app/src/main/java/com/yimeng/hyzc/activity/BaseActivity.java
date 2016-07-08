package com.yimeng.hyzc.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyApp;

import cn.jpush.android.api.JPushInterface;

public abstract class BaseActivity extends AppCompatActivity {
    private View mStatusBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        initView();
        setListener();
        initData();
        setStatusBar();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    /**
     * 处理状态栏
     */
    protected void setStatusBar(){
        final int sdk = Build.VERSION.SDK_INT;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        if (sdk >= Build.VERSION_CODES.KITKAT) {
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            // 设置透明状态栏
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }

            // 设置状态栏颜色
            ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
            setupStatusBarView(contentLayout, getResources().getColor(R.color.colorStatusBar));

            // 设置Activity layout的fitsSystemWindows
            View contentChild = contentLayout.getChildAt(0);
            contentChild.setFitsSystemWindows(true);
        }
    }

    private void setupStatusBarView(ViewGroup contentLayout, int color) {
        if (mStatusBarView == null) {
            View statusBarView = new View(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this));
            contentLayout.addView(statusBarView, lp);

            mStatusBarView = statusBarView;
        }

        mStatusBarView.setBackgroundColor(color);
    }

    /**
     * 获得状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
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
