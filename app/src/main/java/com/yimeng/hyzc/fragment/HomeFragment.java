package com.yimeng.hyzc.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.TestActivity;
import com.yimeng.hyzc.view.AutoRollViewPager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 依萌 on 2016/6/20.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private AutoRollViewPager viewPager;
    private TextView btTest;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    protected void initView(View view) {
        viewPager = (AutoRollViewPager)view.findViewById(R.id.vp);
        btTest = (TextView)view.findViewById(R.id.bt_test);
    }

    @Override
    protected void setListener() {
        btTest.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        ArrayList<String> datas = new ArrayList<>();
        datas.add(Uri.fromFile(new File(getActivity().getFilesDir(),"/1.png")).toString());
        datas.add(Uri.fromFile(new File(getActivity().getFilesDir(),"/2.jpg")).toString());
        datas.add(Uri.fromFile(new File(getActivity().getFilesDir(),"/3.jpg")).toString());
        viewPager.setData(datas);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(),TestActivity.class));
    }
}
