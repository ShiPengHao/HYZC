package com.yimeng.hyzchbczhwq.holder;

import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.PrescriptionBean;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 预约信息holder
 */
public class PrescriptionHolder extends BaseHolder<PrescriptionBean> {
    private TextView tv_prescription_id;
    private TextView tv_doctor;
    private TextView tv_prescription_time;
    private TextView tv_patient;

    @Override
    protected View initView() {
        View view = UiUtils.inflate(R.layout.item_prescription);
        tv_prescription_id =  (TextView) view.findViewById(R.id.tv_prescription_id);
        tv_doctor =  (TextView) view.findViewById(R.id.tv_doctor);
        tv_prescription_time =  (TextView) view.findViewById(R.id.tv_prescription_time);
        tv_patient =  (TextView) view.findViewById(R.id.tv_patient);
        return view;
    }

    @Override
    public void bindData(PrescriptionBean bean) {
        MyApp context = MyApp.getAppContext();
        tv_doctor.setText(String.format("%s：%s", context.getString(R.string.doctor), bean.doctor_name));
        tv_prescription_id.setText(String.format("%s：%s", context.getString(R.string.prescription_id), bean.prescription_id));
        tv_patient.setText(String.format("%s：%s", context.getString(R.string.patient), bean.patient_name.replace("\n","")));
        try {
            String date = bean.sig_time.substring(bean.sig_time.indexOf("(") + 1, bean.sig_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date)));
            tv_prescription_time.setText(String.format("%s：%s", context.getString(R.string.prescription_time),date));
        } catch (Exception e) {
            tv_prescription_time.setText(context.getString(R.string.appointment_add_time));
        }
    }
}
