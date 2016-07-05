package com.yimeng.hyzc.adapter;

import com.yimeng.hyzc.bean.AppointmentBean;
import com.yimeng.hyzc.holder.AppointmentHolder;
import com.yimeng.hyzc.holder.BaseHolder;

import java.util.List;

/**
 * 预约信息适配器
 */
public class AppointmentAdapter extends DefaultAdapter<AppointmentBean> {

    public AppointmentAdapter(List<AppointmentBean> data) {
        super(data);
    }

    @Override
    protected BaseHolder getHolder() {
        return new AppointmentHolder();
    }
}
