package com.yimeng.hyzc.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.BookingActivity;
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
    private Button btTest;
    private ArrayList<String> datas;
    private Button bt_booking;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    protected void initView(View view) {
        viewPager = (AutoRollViewPager) view.findViewById(R.id.vp);
        btTest = (Button) view.findViewById(R.id.bt_test);
        bt_booking = (Button) view.findViewById(R.id.bt_booking);
    }

    @Override
    protected void setListener() {
        btTest.setOnClickListener(this);
        bt_booking.setOnClickListener(this);
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
            case R.id.bt_booking:
                startActivity(new Intent(getActivity(), BookingActivity.class));
                break;
        }
    }
}
