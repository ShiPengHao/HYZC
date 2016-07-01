package com.yimeng.hyzc.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.BitmapUtils;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.UiUtils;

/**
 * 引导页面fragment
 */
public class IntroduceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = UiUtils.inflate(R.layout.layout_imageview);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_viewpager_item);
        Bundle bundle;
        if ((bundle = getArguments()) == null){
            return view;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bundle.getInt("resId"));
        if (bitmap != null) {
            imageView.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, DensityUtil.SCREEN_WIDTH, DensityUtil.SCREEN_HEIGHT));
            bitmap.recycle();
        }else{
            Picasso.with(MyApp.getAppContext()).load(bundle.getString("url")).into(imageView);
        }
        return view;
    }

}
