package com.yimeng.hyzc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.DoctorBean;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.UiUtils;

import java.util.ArrayList;

/**
 * 医生头像适配器
 */
public class DoctorAdapter extends BaseAdapter {
    private ArrayList<DoctorBean> data;
    private final Context context = MyApp.getAppContext();

    public DoctorAdapter(ArrayList<DoctorBean> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.item_doctor);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DoctorBean doctorBean = data.get(position);
        holder.item_name.setText(doctorBean.doctor_name);
        holder.item_limit.setText(String.format("%s:%s", context.getString(R.string.appointment_limit)
                , doctorBean.doctor_UCL == -1 ? "不限" : doctorBean.doctor_UCL));
        holder.item_introduce.setText(String.format("%s:%s", context.getString(R.string.doctor_introduce)
                , doctorBean.remark == null ? context.getString(R.string.empty_content) : doctorBean.remark));
        Picasso.with(MyApp.getAppContext())
                .load(MyConstant.NAMESPACE + doctorBean.doctor_avatar)
                .resize(DensityUtil.dip2px(64),DensityUtil.dip2px(64))
                .into(holder.item_icon);
        return convertView;
    }

    private static class ViewHolder {
        public View rootView;
        public ImageView item_icon;
        public TextView item_name;
        public TextView item_limit;
        public TextView item_introduce;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.item_icon = (ImageView) rootView.findViewById(R.id.item_icon);
            this.item_name = (TextView) rootView.findViewById(R.id.item_name);
            this.item_limit = (TextView) rootView.findViewById(R.id.item_limit);
            this.item_introduce = (TextView) rootView.findViewById(R.id.item_introduce);
        }

    }
}
