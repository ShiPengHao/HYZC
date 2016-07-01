package com.yimeng.hyzc.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.BaseFragmentPagerAdapter;
import com.yimeng.hyzc.fragment.MyFragment;
import com.yimeng.hyzc.fragment.DrugFragment;
import com.yimeng.hyzc.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager vp;
    private LinearLayout ll_tab;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home;
    }

    protected void initView() {
        vp = (ViewPager) findViewById(R.id.vp);
        ll_tab = (LinearLayout) findViewById(R.id.ll_tab);
    }

    protected void setListener() {
        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View view = ll_tab.getChildAt(i);
            view.setOnClickListener(this);
            view.setId(i);
            if (i == 0) {
                view.setEnabled(false);
            }
        }
        adapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < ll_tab.getChildCount(); i++) {
                    ll_tab.getChildAt(i).setEnabled(i != position);
                }
            }
        });
    }

    protected void initData() {
        fragments.clear();
        fragments.add(new HomeFragment());
        fragments.add(new MyFragment());
        fragments.add(new DrugFragment());
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        vp.setCurrentItem(v.getId());
    }
}
