package com.yimeng.hyzchbczhwq.holder;

import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.PatientBean;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.UiUtils;

/**
 * 病人信息holder
 */
public class PatientHolder extends BaseHolder<PatientBean> {
    private TextView tv_patient_name;
    private TextView tv_patient_id;

    @Override
    protected View initView() {
        View view = UiUtils.inflate(R.layout.item_patient);
        tv_patient_name = (TextView) view.findViewById(R.id.tv_patient_name);
        tv_patient_id = (TextView) view.findViewById(R.id.tv_patient_id);
        return view;
    }

    @Override
    public void bindData(PatientBean bean) {
        MyApp context = MyApp.getAppContext();
        tv_patient_name.setText(String.format("%s：%s", context.getString(R.string.patient_name), bean.patient_name));
        tv_patient_id.setText(String.format("%s：%s", context.getString(R.string.id_number), bean.patient_identification));
    }
}
