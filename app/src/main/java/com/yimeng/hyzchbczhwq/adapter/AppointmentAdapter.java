package com.yimeng.hyzchbczhwq.adapter;

import com.yimeng.hyzchbczhwq.bean.AppointmentBean;
import com.yimeng.hyzchbczhwq.holder.AppointmentHolder;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;

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
