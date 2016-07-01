package com.yimeng.hyzc.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.DoctorBean;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.UiUtils;

import java.util.ArrayList;

/**
 * 医生头像适配器
 */
public class DoctorAdapter extends BaseAdapter {
    private ArrayList<DoctorBean> datas;

    public DoctorAdapter(ArrayList<DoctorBean> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.item_avatar_name);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DoctorBean doctorBean = datas.get(position);
        holder.item_function.setText(doctorBean.doctor_user);
        Picasso.with(MyApp.getAppContext()).load(MyConstant.NAMESPACE + doctorBean.doctor_avatar).into(holder.item_icon);
        return convertView;
    }

    private static class ViewHolder {
        public View rootView;
        public ImageView item_icon;
        public TextView item_function;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.item_icon = (ImageView) rootView.findViewById(R.id.item_icon);
            this.item_function = (TextView) rootView.findViewById(R.id.item_function);
        }

    }
}
