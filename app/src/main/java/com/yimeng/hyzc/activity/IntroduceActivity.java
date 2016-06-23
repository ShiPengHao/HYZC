package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.fragment.Introduce1;
import com.yimeng.hyzc.fragment.Introduce2;
import com.yimeng.hyzc.utils.MyLog;
import com.yimeng.hyzc.view.LazyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导界面，应用第一次运行时展示应用信息
 */
public class IntroduceActivity extends AppCompatActivity {

    private LazyViewPager vp;
    private PagerAdapter adapter;
    private List<Fragment> fragments;
    private LazyViewPager.OnPageChangeListener onPageChangeListener;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        initView();
        initData();
        setListener();
    }

    /**
     * 设置数据适配器和监听器
     */
    private void setListener() {
        adapter = new IntroduceFragmentAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        onPageChangeListener = new LazyViewPager.OnPageChangeListener() {
            private int count = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                MyLog.i("introduce", "positon=" + position + "count=" + count);
                if (isRunning && position == vp.getAdapter().getCount() - 1) {
                    if (count > 2) {
                        isRunning = false;
                        startActivity(new Intent(IntroduceActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        count++;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                MyLog.i("introduce", "state=" + state);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    count = 0;
                }
            }
        };
        vp.setOnPageChangeListener(onPageChangeListener);
    }

    private void initData() {
        fragments = new ArrayList<>();
        fragments.add(new Introduce1());
        fragments.add(new Introduce2());
    }

    private void initView() {
        vp = (LazyViewPager) findViewById(R.id.vp);
    }

    private class IntroduceFragmentAdapter extends FragmentPagerAdapter {

        public IntroduceFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
