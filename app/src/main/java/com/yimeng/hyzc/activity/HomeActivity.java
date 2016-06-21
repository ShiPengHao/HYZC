package com.yimeng.hyzc.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.fragment.HomeFragment;
import com.yimeng.hyzc.fragment.Introduce1;
import com.yimeng.hyzc.fragment.Introduce2;
import com.yimeng.hyzc.view.LazyViewPager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private LazyViewPager vp;
    private TextView tv_home;
    private TextView tv_drug;
    private TextView tv_health;
    private LinearLayout ll_tab;
    private List<Fragment> fragments;

    private class MyHomePagerAdapter extends FragmentPagerAdapter {

        public MyHomePagerAdapter(FragmentManager fm) {
            super(getSupportFragmentManager());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
        setListener();
    }

    private void initData() {
        fragments= new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new Introduce1());
        fragments.add(new Introduce2());
    }

    private void setListener() {

        PagerAdapter adapter = new MyHomePagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        vp.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < ll_tab.getChildCount(); i++) {
                    ll_tab.getChildAt(i).setEnabled(i != position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        vp = (LazyViewPager) findViewById(R.id.vp);
        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_drug = (TextView) findViewById(R.id.tv_drug);
        tv_health = (TextView) findViewById(R.id.tv_health);
        ll_tab = (LinearLayout) findViewById(R.id.ll_tab);

        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View view = ll_tab.getChildAt(i);
            view.setOnClickListener(this);
            view.setId(i);
            if (i == 0) {
                view.setEnabled(false);
            }
        }

    }

    @Override
    public void onClick(View v) {
        vp.setCurrentItem(v.getId());
    }
}
