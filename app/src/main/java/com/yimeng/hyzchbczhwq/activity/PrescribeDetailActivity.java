package com.yimeng.hyzchbczhwq.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.PrescriptionMedicineAdapter;
import com.yimeng.hyzchbczhwq.bean.PrescriptionBean;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 药方详情
 */
public class PrescribeDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    HashMap<String, Object> params = new HashMap<>();
    private ArrayList<PrescriptionBean> prescriptions = new ArrayList<>();
    private ImageView iv_back;
    private TextView tv_doctor;
    private TextView tv_medicine_remark;
    private TextView tv_patient;
    private TextView tv_phone;
    private TextView tv_prescribe_build_time;
    private TextView tv_prescribe_get_time;
    private ListView lv;
    private PrescriptionMedicineAdapter adapter;
    private TextView tv_suggest_pharmacy;
    private CheckBox cb_prescribe_has_got;
    private boolean isPharmacy;
    private String prescription_id;
    private boolean flagChanged;
    private TextView tv_prescription_id;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_prescribe_detail;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_doctor = (TextView) findViewById(R.id.tv_doctor);
        tv_medicine_remark = (TextView) findViewById(R.id.tv_medicine_remark);
        tv_prescription_id = (TextView) findViewById(R.id.tv_prescription_id);
        tv_patient = (TextView) findViewById(R.id.tv_patient);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_prescribe_build_time = (TextView) findViewById(R.id.tv_prescribe_build_time);
        tv_prescribe_get_time = (TextView) findViewById(R.id.tv_prescribe_get_time);
        tv_suggest_pharmacy = (TextView) findViewById(R.id.tv_suggest_pharmacy);
        cb_prescribe_has_got = (CheckBox) findViewById(R.id.cb_prescribe_has_got);
        cb_prescribe_has_got.setClickable(false);
        lv = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        adapter = new PrescriptionMedicineAdapter(prescriptions, false);
        lv.setAdapter(adapter);
        cb_prescribe_has_got.setChecked(true);
        cb_prescribe_has_got.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        prescription_id = getIntent().getStringExtra("prescription_id");
        if (!TextUtils.isEmpty(prescription_id)) {
            requestPrescriptionDetail();
        }
        isPharmacy = getIntent().getBooleanExtra("isPharmacy", false);
    }

    /**
     * 获取药方详情
     */
    private void requestPrescriptionDetail() {
        this.params.clear();
        this.params.put("prescription_id", prescription_id);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    MyToast.show("没有此单号对应的药方!3秒后页面将自动关闭!");
                    tv_suggest_pharmacy.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                    return;
                }
                parseListResult(prescriptions, PrescriptionBean.class, s);
                adapter.notifyDataSetChanged();
                if (prescriptions.size() > 0) {
                    bindData();
                } else {
                    MyToast.show("没有此单号对应的药方，请检查后再试。2s后页面将关闭");
                    tv_suggest_pharmacy.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                }
            }
        }.execute("Get_Prescription", params);
    }

    /**
     * 绑定药方信息
     */
    private void bindData() {
        PrescriptionBean bean = prescriptions.get(0);
        tv_prescription_id.setText(bean.prescription_id);
        tv_doctor.setText(bean.doctor_name);
        tv_medicine_remark.setText(bean.remark);
        tv_patient.setText(bean.patient_name);
        tv_phone.setText(bean.patient_phone);
        tv_suggest_pharmacy.setText(bean.pharmacy_name + "(" + bean.pharmacy_adress + ")");
        if (bean.recipe_flag == 0) {
            cb_prescribe_has_got.setChecked(false);
            if (isPharmacy) {
                cb_prescribe_has_got.setClickable(true);
            }
        } else {
            cb_prescribe_has_got.setChecked(true);
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
                notifyFlagChanged();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                notifyFlagChanged();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 如果取药标志已经更改，则通知上个页面更新界面
     */
    private void notifyFlagChanged() {
        if (flagChanged) {
            setResult(1000, new Intent());
        }
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_prescribe_has_got:
                if (isChecked) {
                    cb_prescribe_has_got.setClickable(false);
                    showFlagChangedDialog();
                }
                break;
        }
    }

    /**
     * 显示确认更改取药标志对话框
     */
    public void showFlagChangedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("您确定病人已取药吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestSubRecipeFlag();
                    }
                })
                .setNegativeButton("不确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cb_prescribe_has_got.setChecked(false);
                        cb_prescribe_has_got.setClickable(true);
                    }
                })
                .show();
    }

    /**
     * 向服务器请求更新将药方标志设置为已取药，成功则刷新页面，失败则更新checkbox为false、可点击
     */
    public void requestSubRecipeFlag() {
        params.clear();
        params.put("prescription_id", prescription_id);
        params.put("recipe_flag", 1);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    MyToast.show(getResources().getString(R.string.connect_error));
                    cb_prescribe_has_got.setChecked(false);
                    cb_prescribe_has_got.setClickable(true);
                    return;
                }
                try {
                    JSONObject object = new JSONObject(s);
                    if (!"ok".equalsIgnoreCase(object.optString("status"))) {
                        cb_prescribe_has_got.setChecked(false);
                        cb_prescribe_has_got.setClickable(true);
                    } else {
                        flagChanged = true;
                        requestPrescriptionDetail();
                    }
                    MyToast.show(object.optString("msg"));
                } catch (Exception e) {
                    MyToast.show(getResources().getString(R.string.connect_error));
                    cb_prescribe_has_got.setChecked(false);
                    cb_prescribe_has_got.setClickable(true);
                }
            }
        }.execute("sub_Recipe_Flag", params);
    }

}
