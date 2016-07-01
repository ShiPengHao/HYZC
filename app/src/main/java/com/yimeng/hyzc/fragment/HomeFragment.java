package com.yimeng.hyzc.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.TestActivity;
import com.yimeng.hyzc.view.AutoRollViewPager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * 首页fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private AutoRollViewPager viewPager;
    private TextView btTest;
    private ArrayList<String> datas;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    protected void initView(View view) {
        viewPager = (AutoRollViewPager) view.findViewById(R.id.vp);
        btTest = (TextView) view.findViewById(R.id.bt_test);
    }

    @Override
    protected void setListener() {
        btTest.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        datas = new ArrayList<>();
        File file1 = new File(getActivity().getFilesDir(), "/a.png");
        File file2 = new File(getActivity().getFilesDir(), "/b.jpg");
        File file3 = new File(getActivity().getFilesDir(), "/c.jpg");
        datas.add(Uri.fromFile(file1).toString());
        datas.add(Uri.fromFile(file2).toString());
        datas.add(Uri.fromFile(file3).toString());
        viewPager.setData(datas);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_test:
                startActivity(new Intent(getActivity(), TestActivity.class));
                break;
        }
    }
}
