package com.yimeng.hyzc.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.UiUtils;
import com.yimeng.hyzc.view.AutoRollViewPager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 依萌 on 2016/6/20.
 */
public class HomeFragment extends Fragment {

    private AutoRollViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = UiUtils.inflate(R.layout.fragment_home);
        initView(view);
        return view;
    }

    private void initView(View view) {
        viewPager = (AutoRollViewPager)view.findViewById(R.id.vp);
        ArrayList<String> datas = new ArrayList<>();
        datas.add(Uri.fromFile(new File(getActivity().getFilesDir(),"/1.png")).toString());
        datas.add(Uri.fromFile(new File(getActivity().getFilesDir(),"/2.jpg")).toString());
        datas.add(Uri.fromFile(new File(getActivity().getFilesDir(),"/3.jpg")).toString());
        viewPager.setData(datas);
    }

}
