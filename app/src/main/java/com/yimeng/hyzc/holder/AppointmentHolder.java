package com.yimeng.hyzc.holder;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AppointmentBean;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.UiUtils;

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
        View view = UiUtils.inflate(R.layout.item_apponintment_patient);
        tv_appointmentId =  (TextView) view.findViewById(R.id.tv_appointmentId);
        tv_status =  (TextView) view.findViewById(R.id.tv_appointStatus);
        tv_doctor =  (TextView) view.findViewById(R.id.tv_doctor);
        tv_appointmentTime =  (TextView) view.findViewById(R.id.tv_appointmentTime);
        tv_description =  (TextView) view.findViewById(R.id.tv_description);
        return view;
    }

    @Override
    public void bindData(AppointmentBean bean) {
        tv_doctor.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.doctor), bean.doctor_name));
        tv_appointmentId.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.appointment_id), bean.appointment_id));
        tv_description.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.disease_description), bean.disease_description.replace("\n","")));
        if (bean.doctor_dispose == 0){
            tv_status.setText("未回复");
            tv_status.setTextColor(Color.RED);
        }else{
            tv_status.setText("已回复");
            tv_status.setTextColor(Color.BLACK);
        }
        try {
            String date = bean.add_time.substring(bean.add_time.indexOf("(") + 1, bean.add_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(date)));
            tv_appointmentTime.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.appointment_time),
                    date));
        } catch (Exception e) {
            tv_appointmentTime.setText(MyApp.getAppContext().getString(R.string.appointment_time));
        }
    }
}
