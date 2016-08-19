package com.yimeng.hyzchbczhwq.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.BaseFragmentPagerAdapter;
import com.yimeng.hyzchbczhwq.fragment.PrescriptionListFragment;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.view.ClearEditText;

import java.util.ArrayList;

/**
 * 医生主界面
 */
public class HomePharmacyActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private String[] titles = new String[]{
            "未取药处方",
            "已取药处方"
    };
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private PagerTabStrip pagerTabsStrip;
    private ImageView iv_search;
    private ClearEditText cet;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pharmacy_home;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        cet = (ClearEditText) findViewById(R.id.cet);
        pagerTabsStrip = (PagerTabStrip) findViewById(R.id.pagerTabsStrip);
        pagerTabsStrip.setTabIndicatorColorResource(R.color.colorAccent);
        pagerTabsStrip.setPadding(0, DensityUtil.dip2px(10), 0, DensityUtil.dip2px(10));
        pagerTabsStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        pagerTabsStrip.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    protected void setListener() {
        adapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragments) {
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };
        viewPager.setAdapter(adapter);
        iv_search.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // 更新首次运行标志
        SharedPreferences spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        if (spAccount.getBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, true)) {
            spAccount.edit().putBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, false).apply();
        }

        PrescriptionListFragment fragment1 = new PrescriptionListFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("recipe_flag", 0);
        fragment1.setArguments(bundle1);

        PrescriptionListFragment fragment2 = new PrescriptionListFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("recipe_flag", 1);
        fragment2.setArguments(bundle2);

        fragments.add(fragment1);
        fragments.add(fragment2);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                String prescription_id = cet.getText().toString().trim();
                if (!TextUtils.isEmpty(prescription_id)) {
                    startActivity(new Intent(this, PrescribeDetailActivity.class).putExtra("prescription_id", prescription_id));
                } else {
                    MyToast.show(String.format("%s%s", getString(R.string.prescription_id), getString(R.string.can_not_be_null)));
                    ObjectAnimator.ofFloat(cet, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                }
                break;
        }
    }

}
