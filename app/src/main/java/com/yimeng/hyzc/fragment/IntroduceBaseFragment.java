package com.yimeng.hyzc.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.UiUtils;

import java.util.Random;

/**
 * Created by 依萌 on 2016/6/20.
 */
public abstract class IntroduceBaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = UiUtils.inflate(R.layout.activity_splash);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv);
        Picasso.with(MyApp.getAppContext()).load(getUrl()).into(imageView);
        Random r = new Random();
        view.setBackgroundColor(Color.argb(66,r.nextInt(150)+10,r.nextInt(150)+10,r.nextInt(150)+10));
        return view;
    }

    protected abstract String getUrl();
}
