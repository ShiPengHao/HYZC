package com.yimeng.hyzc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yimeng.hyzc.utils.UiUtils;

/**
 * fragment抽象基类
 */
public abstract class BaseFragment extends Fragment {
    protected Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (context == null) {
            context = getActivity();
        }
        View view = getView();
        if (null == view) {
            view = UiUtils.inflate(getLayoutResId());
            initView(view);
            setListener();
            initData();
        }
        return view;
    }


    /**
     * 获得布局id
     *
     * @return
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化控件
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 为控件设置监听
     */
    protected abstract void setListener();

    /**
     * 加载数据，绑定到控件
     */
    protected abstract void initData();
}
