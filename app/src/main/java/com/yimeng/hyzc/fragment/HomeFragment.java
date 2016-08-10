package com.yimeng.hyzc.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.BookingActivity;
import com.yimeng.hyzc.activity.HealthActivity;
import com.yimeng.hyzc.activity.TestActivity;
import com.yimeng.hyzc.utils.Cheeses;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyLog;
import com.yimeng.hyzc.view.AutoRollViewPager;
import com.yimeng.hyzc.view.FlowLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * 首页fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private AutoRollViewPager viewPager;
    private Button btTest;
    private Button bt_booking;
    private Button bt_health;
    private FlowLayout flowLayout;

    private ArrayList<String> data;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    protected void initView(View view) {
        viewPager = (AutoRollViewPager) view.findViewById(R.id.vp);
        btTest = (Button) view.findViewById(R.id.bt_test);
        bt_booking = (Button) view.findViewById(R.id.bt_booking);
        bt_health = (Button) view.findViewById(R.id.bt_health);
        flowLayout = (FlowLayout) view.findViewById(R.id.flowLayout);
    }

    @Override
    protected void setListener() {
        if (MyLog.DEBUG) {
            btTest.setOnClickListener(this);
        }else{
            btTest.setVisibility(View.GONE);
        }
        bt_booking.setOnClickListener(this);
        bt_health.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.startRoll();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.stopRoll();
    }

    @Override
    protected void initData() {
        data = new ArrayList<>();
        data.add(null);
        data.add(null);
        data.add(null);
        viewPager.setData(data);
        TextView tv;
        Random random = new Random();
        int padding = DensityUtil.dip2px(4);
        for (int i = 0; i < 5; i++) {
            tv = new TextView(context);
            tv.setBackgroundColor(Color.argb(200, random.nextInt(200) + 10, random.nextInt(200) + 10, random.nextInt(200) + 10));
            tv.setPadding(padding, padding, padding, padding);
            tv.setText(Cheeses.NAMES[i]);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setTextSize(2 * padding);
            tv.setGravity(Gravity.CENTER);
            flowLayout.addView(tv);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_test:
                startActivity(new Intent(getActivity(), TestActivity.class));// 测试
                break;
            case R.id.bt_booking:
                startActivity(new Intent(getActivity(), BookingActivity.class));// 预约挂号
                break;
            case R.id.bt_health:
                startActivity(new Intent(getActivity(), HealthActivity.class));// 健康教育
                break;
        }
    }
}
