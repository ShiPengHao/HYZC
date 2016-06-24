package com.yimeng.hyzc.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.TestActivity;
import com.yimeng.hyzc.view.AutoRollViewPager;

import java.io.File;
import java.io.FileOutputStream;
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

        File file1 = new File(getActivity().getFilesDir(), "/a.png");
        if (!file1.exists()){
            try {
                FileOutputStream outputStream = new FileOutputStream(file1);
                BitmapFactory.decodeResource(getResources(), R.drawable.a)
                        .compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File file2 = new File(getActivity().getFilesDir(), "/b.jpg");
        if (!file2.exists()){
            try {
                FileOutputStream outputStream = new FileOutputStream(file2);
                BitmapFactory.decodeResource(getResources(), R.drawable.b)
                        .compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File file3 = new File(getActivity().getFilesDir(), "/c.jpg");
        if (!file3.exists()){
            try {
                FileOutputStream outputStream = new FileOutputStream(file1);
                BitmapFactory.decodeResource(getResources(), R.drawable.c)
                        .compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> datas = new ArrayList<>();
        datas.add(Uri.fromFile(file1).toString());
        datas.add(Uri.fromFile(file2).toString());
        datas.add(Uri.fromFile(file3).toString());
        viewPager.setData(datas);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(),TestActivity.class));
    }
}
