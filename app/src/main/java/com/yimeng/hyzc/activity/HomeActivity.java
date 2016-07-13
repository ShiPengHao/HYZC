package com.yimeng.hyzc.activity;

import android.graphics.drawable.Drawable;
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
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.BaseFragmentPagerAdapter;
import com.yimeng.hyzc.fragment.DrugFragment;
import com.yimeng.hyzc.fragment.HomeFragment;
import com.yimeng.hyzc.fragment.MyFragment;
import com.yimeng.hyzc.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager vp;
    private LinearLayout ll_tab;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private View indicatorLine;
    private List<Drawable> tabPressedIcons = new ArrayList<>();
    private List<Drawable> tabNormalIcons = new ArrayList<>();
    private long preTime = -1l;
    private int lastPosition;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home;
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
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
                nowDrawable.setBounds(0, 0, nowDrawable.getMinimumWidth(),nowDrawable.getMinimumHeight());
                currentView.setCompoundDrawables(null, nowDrawable, null, null);
                ViewPropertyAnimator.animate(currentView).scaleX(1.1f).scaleY(1.1f);


                TextView lastView = (TextView) ll_tab.getChildAt(lastPosition);
                lastView.setTextColor(getResources().getColor(R.color.black));
                Drawable lastDrawable = tabNormalIcons.get(lastPosition);
                lastDrawable.setBounds(0, 0, lastDrawable.getMinimumWidth(),lastDrawable.getMinimumHeight());
                lastView.setCompoundDrawables(null, lastDrawable, null, null);
                ViewPropertyAnimator.animate(lastView).scaleX(1.0f).scaleY(1.0f);

                lastPosition = position;
            }
        });
    }

    protected void initData() {
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
