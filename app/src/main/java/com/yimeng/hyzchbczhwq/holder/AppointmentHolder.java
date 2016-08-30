package com.yimeng.hyzchbczhwq.holder;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.AppointmentBean;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 预约信息holder
 */
public class AppointmentHolder extends BaseHolder<AppointmentBean> {
    private TextView tv_appointmentId;
    private TextView tv_status;
    private TextView tv_doctor;
    private TextView tv_appointmentTime;
    private TextView tv_description;

    @Override
    protected View initView() {
        View view = UiUtils.inflate(R.layout.item_apponintment);
        tv_appointmentId = (TextView) view.findViewById(R.id.tv_appointmentId);
        tv_status = (TextView) view.findViewById(R.id.tv_appointStatus);
        tv_doctor = (TextView) view.findViewById(R.id.tv_doctor);
        tv_appointmentTime = (TextView) view.findViewById(R.id.tv_appointmentTime);
        tv_description = (TextView) view.findViewById(R.id.tv_description);
        return view;
    }

    @Override
    public void bindData(AppointmentBean bean) {
        MyApp context = MyApp.getAppContext();
        tv_doctor.setText(String.format("%s：%s", context.getString(R.string.doctor), bean.doctor_name));
        tv_appointmentId.setText(String.format("%s：%s", context.getString(R.string.appointment_id), bean.appointment_id));
        String description = bean.disease_description;
        if (TextUtils.isEmpty(description))
            tv_description.setText(String.format("%s：%s", context.getString(R.string.disease_description), context.getString(R.string.empty_content)));
        else if (description.contains("模板")) {
            tv_description.setText(description);
        } else {
            tv_description.setText(String.format("%s：%s", context.getString(R.string.disease_description), description));
        }
        if (bean.doctor_dispose == 0) {
            tv_status.setText(String.format("%s：%s", context.getString(R.string.appointment_status), context.getString(R.string.no_response)));
            tv_status.setTextColor(Color.RED);
        } else {
            tv_status.setText(String.format("%s：%s", context.getString(R.string.appointment_status), context.getString(R.string.has_response)));
            tv_status.setTextColor(MyApp.getAppContext().getResources().getColor(R.color.colorAccent));
        }
        try {
            String date = bean.add_time.substring(bean.add_time.indexOf("(") + 1, bean.add_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date)));
            tv_appointmentTime.setText(String.format("%s：%s", context.getString(R.string.appointment_add_time),
                    date));
        } catch (Exception e) {
            tv_appointmentTime.setText(context.getString(R.string.appointment_add_time));
        }
    }
}
