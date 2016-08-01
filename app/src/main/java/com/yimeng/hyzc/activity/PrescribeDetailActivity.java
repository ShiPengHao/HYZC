package com.yimeng.hyzc.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.PrescriptionMedicineAdapter;
import com.yimeng.hyzc.bean.PrescriptionBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 药方详情
 */
public class PrescribeDetailActivity extends BaseActivity implements View.OnClickListener {

    HashMap<String, Object> params = new HashMap<>();
    private ArrayList<PrescriptionBean> prescriptions = new ArrayList<>();
    private ImageView iv_back;
    private TextView tv_doctor;
    private TextView tv_medicine_remark;
    private TextView tv_patient;
    private TextView tv_phone;
    private TextView tv_prescribe_build_time;
    private TextView tv_prescribe_get_time;
    private TextView tv_prescribe_has_got;
    private ListView lv;
    private PrescriptionMedicineAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_prescribe_detail;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_doctor = (TextView) findViewById(R.id.tv_doctor);
        tv_medicine_remark = (TextView) findViewById(R.id.tv_medicine_remark);
        tv_patient = (TextView) findViewById(R.id.tv_patient);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_prescribe_build_time = (TextView) findViewById(R.id.tv_prescribe_build_time);
        tv_prescribe_get_time = (TextView) findViewById(R.id.tv_prescribe_get_time);
        tv_prescribe_has_got = (TextView) findViewById(R.id.tv_prescribe_has_got);
        lv = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        adapter = new PrescriptionMedicineAdapter(prescriptions, false);
        lv.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        int prescription_id = getIntent().getIntExtra("prescription_id", 0);
        if (prescription_id > 0) {
            params.clear();
            params.put("prescription_id", prescription_id);
            requestPrescriptionDetail("Get_Prescription", params);
        }
    }

    /**
     * 获取药方详情
     *
     * @param params 方法名+参数列表
     */
    private void requestPrescriptionDetail(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    return;
                }
                parseListResult(prescriptions, PrescriptionBean.class, s);
                adapter.notifyDataSetChanged();
                if (prescriptions.size() > 0) {
                    bindData();
                }
            }
        }.execute(params);
    }

    /**
     * 绑定药方信息
     */
    private void bindData() {
        PrescriptionBean bean = prescriptions.get(0);
        tv_doctor.setText(bean.doctor_name);
        tv_medicine_remark.setText(bean.remark);
        tv_patient.setText(bean.patient_name);
        tv_phone.setText(bean.patient_phone);
        if (bean.recipe_flag == 0){
            tv_prescribe_has_got.setText("否");
        }else{
            tv_prescribe_has_got.setText("是");
        }

        try {
            String date = bean.sig_time.substring(bean.sig_time.indexOf("(") + 1, bean.sig_time.indexOf(")"));
            tv_prescribe_build_time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date))));
        } catch (Exception e) {
            tv_prescribe_build_time.setText(context.getString(R.string.empty_content));
        }

        try {
            String date = bean.recipe_time.substring(bean.recipe_time.indexOf("(") + 1, bean.recipe_time.indexOf(")"));
            tv_prescribe_get_time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date))));
        } catch (Exception e) {
            tv_prescribe_get_time.setText(context.getString(R.string.empty_content));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
