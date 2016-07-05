package com.yimeng.hyzc.holder;

import android.view.View;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AppointmentBean;
import com.yimeng.hyzc.utils.UiUtils;

/**
 * 预约信息holder
 */
public class AppointmentHolder extends BaseHolder<AppointmentBean> {
    @Override
    protected View initView() {
        return UiUtils.inflate(R.layout.item_avatar_name);
    }

    @Override
    public void bindData(AppointmentBean data) {

    }
}
