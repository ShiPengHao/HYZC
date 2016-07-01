package com.yimeng.hyzc.adapter;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.DrugTypeBean;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.PinYinUtils;
import com.yimeng.hyzc.utils.UiUtils;

import java.util.List;

/**
 * 药品适配器
 */
public class DrugTypeAdapter extends BaseAdapter {

    private List<DrugTypeBean> datas;

    public DrugTypeAdapter(List<DrugTypeBean> datas){
        this.datas = datas;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.item_drug_type);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DrugTypeBean bean = datas.get(position);

        String icon = bean.icon;
        if (!TextUtils.isEmpty(icon)) {
            Picasso.with(MyApp.getAppContext()).load(MyConstant.URL_BASE + icon).into(holder.iv);
        } else {
            holder.iv.setImageBitmap(BitmapFactory.decodeResource(MyApp.getAppContext().getResources(), R.mipmap.ic_launcher));
        }

        holder.tv_drug.setText(bean.name);

        char charNow = PinYinUtils.getPinYin(bean.name).charAt(0);

        if (position != 0 && PinYinUtils.getPinYin(datas.get(position - 1).name).charAt(0) == charNow) {
            holder.tv_pinyin.setVisibility(View.GONE);
        } else {
            holder.tv_pinyin.setVisibility(View.VISIBLE);
            holder.tv_pinyin.setText(String.valueOf(charNow));
        }
        return convertView;
    }

    public class ViewHolder {
        public View rootView;
        public ImageView iv;
        public TextView tv_drug;
        public TextView tv_pinyin;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.iv = (ImageView) rootView.findViewById(R.id.iv);
            this.tv_drug = (TextView) rootView.findViewById(R.id.tv_drug);
            this.tv_pinyin = (TextView) rootView.findViewById(R.id.tv_pinyin);
        }

    }

}