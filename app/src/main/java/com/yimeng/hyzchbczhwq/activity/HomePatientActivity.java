package com.yimeng.hyzchbczhwq.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.BaseFragmentPagerAdapter;
import com.yimeng.hyzchbczhwq.fragment.DrugFragment;
import com.yimeng.hyzchbczhwq.fragment.HomeFragment;
import com.yimeng.hyzchbczhwq.fragment.MyFragment;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

public class HomePatientActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager vp;
    private LinearLayout ll_tab;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private View indicatorLine;
    private List<Drawable> tabPressedIcons = new ArrayList<>();
    private List<Drawable> tabNormalIcons = new ArrayList<>();
    private long preTime = -1l;
    private int lastPosition;
    private Handler handler;
    private AlertDialog alertDialog;
    private static final int WHAT_SHOW_TIP_DIALOG = 100;
    private static final int WHAT_DISMISS_TIP_DIALOG = 101;

    private class MyOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            float endPosition = indicatorLine.getWidth() * (position + positionOffset);
            ViewHelper.setTranslationX(indicatorLine, endPosition);
//			ViewPropertyAnimator.animate(indicatorLine).translationX(endPosition );
        }

        @Override
        public void onPageSelected(int position) {
            TextView currentView = (TextView) ll_tab.getChildAt(position);
            currentView.setTextColor(getResources().getColor(R.color.colorAccent));
            Drawable nowDrawable = tabPressedIcons.get(position);
            nowDrawable.setBounds(0, 0, nowDrawable.getMinimumWidth(), nowDrawable.getMinimumHeight());
            currentView.setCompoundDrawables(null, nowDrawable, null, null);
            ViewPropertyAnimator.animate(currentView).scaleX(1.1f).scaleY(1.1f);

            TextView lastView = (TextView) ll_tab.getChildAt(lastPosition);
            lastView.setTextColor(getResources().getColor(R.color.black));
            Drawable lastDrawable = tabNormalIcons.get(lastPosition);
            lastDrawable.setBounds(0, 0, lastDrawable.getMinimumWidth(), lastDrawable.getMinimumHeight());
            lastView.setCompoundDrawables(null, lastDrawable, null, null);
            ViewPropertyAnimator.animate(lastView).scaleX(1.0f).scaleY(1.0f);

            lastPosition = position;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_patient_home;
    }

    protected void initView() {
        vp = (ViewPager) findViewById(R.id.vp);
        ll_tab = (LinearLayout) findViewById(R.id.ll_tab);
        indicatorLine = findViewById(R.id.main_indicate_line);
    }

    protected void setListener() {
        indicatorLine.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                indicatorLine.getLayoutParams().width = ll_tab.getWidth() / ll_tab.getChildCount();
            }
        });
        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            TextView view = (TextView) ll_tab.getChildAt(i);
            view.setOnClickListener(this);
            view.setId(i);
            if (i == 0) {
                ViewPropertyAnimator.animate(view).scaleX(1.1f).scaleY(1.1f);
            }
        }
        adapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        vp.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    protected void initData() {
        // 更新首次运行标志
        SharedPreferences spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        if (spAccount.getBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, true)) {
            dealFirstRunningTip();
            spAccount.edit().putBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, false).apply();
        }

        fragments.clear();
        fragments.add(new HomeFragment());
        fragments.add(new DrugFragment());
        fragments.add(new MyFragment());
        adapter.notifyDataSetChanged();

        tabPressedIcons.clear();
        tabPressedIcons.add(getResources().getDrawable(R.mipmap.icon_home_check));
        tabPressedIcons.add(getResources().getDrawable(R.mipmap.icon_med_check));
        tabPressedIcons.add(getResources().getDrawable(R.mipmap.icon_user_check));

        tabNormalIcons.clear();
        tabNormalIcons.add(getResources().getDrawable(R.mipmap.icon_home_normal));
        tabNormalIcons.add(getResources().getDrawable(R.mipmap.icon_med_normal));
        tabNormalIcons.add(getResources().getDrawable(R.mipmap.icon_user_normal));
    }

    private void dealFirstRunningTip() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_SHOW_TIP_DIALOG:
                        showTipDialog();
                        handler.sendEmptyMessageDelayed(WHAT_DISMISS_TIP_DIALOG, 5000);
                        break;
                    case WHAT_DISMISS_TIP_DIALOG:
                        dismissTipDialog();
                }
            }
        };
        handler.sendEmptyMessageDelayed(WHAT_SHOW_TIP_DIALOG, 500);
    }

    private void showTipDialog() {
        alertDialog = new AlertDialog.Builder(this).setTitle("欢迎使用华医之春互联网医院")
                .setMessage("为了节省您的流量，首次运行应用时需要缓存部分必须数据，可能造成微小卡顿，请耐心等待几秒钟或者重试即可")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissTipDialog();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void dismissTipDialog() {
        if (null != handler) {
            handler.removeMessages(WHAT_DISMISS_TIP_DIALOG);
        }
        if (null != alertDialog && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissTipDialog();
    }


    @Override
    public void onClick(View v) {
        vp.setCurrentItem(v.getId());
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
