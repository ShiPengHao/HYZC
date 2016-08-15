package com.yimeng.hyzc.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.MedicineAdapter;
import com.yimeng.hyzc.bean.MedicineBean;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 医生回复界面
 */
public class DoctorResponseActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private TextView tv_prescribe;
    private TextView tv_doctor_way;
    private Button bt_prescribe;
    private Button bt_response;
    private Button bt_select;
    private EditText et_doctor_response;
    private EditText et_medicine_remark;
    private ListView listView;
    private int id;
    public static final int REQUEST_CODE_FOR_PRESCRIBE = 100;
    private ArrayList<MedicineBean> medicines = new ArrayList<>();
    private BaseAdapter medicineAdapter;
    private HashMap<String, Object> map;
    private TextView uploadTextView;
    private AlertDialog uploadDialog;
    private LinearLayout ll_remark;
    private ArrayList<String> ways = new ArrayList<>();
    private String response;
    private String way;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_response;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_prescribe = (TextView) findViewById(R.id.tv_prescribe);
        tv_prescribe.setVisibility(View.GONE);
        bt_prescribe = (Button) findViewById(R.id.bt_prescribe);
        bt_prescribe.setEnabled(false);
        bt_response = (Button) findViewById(R.id.bt_response);
        bt_select = (Button) findViewById(R.id.bt_select);

        et_doctor_response = (EditText) findViewById(R.id.et_doctor_response);
        tv_doctor_way = (TextView) findViewById(R.id.tv_doctor_way);
        et_medicine_remark = (EditText) findViewById(R.id.et_medicine_remark);
        listView = (ListView) findViewById(R.id.lv);

        ll_remark = (LinearLayout) findViewById(R.id.ll_remark);
        ll_remark.setVisibility(View.GONE);
        initUploadDialog();
    }

    /**
     * 初始化联网进度对话框
     */
    private void initUploadDialog() {
        AlertDialog.Builder uploadImgBuilder = new AlertDialog.Builder(this);
        uploadImgBuilder.setTitle("医生回应");
        uploadTextView = new TextView(this);
        uploadTextView.setTextSize(18);
        uploadTextView.setTextColor(Color.BLACK);
        uploadTextView.setPadding(10, 10, 0, 0);
        uploadTextView.setGravity(Gravity.CENTER);
        uploadImgBuilder.setView(uploadTextView);
        uploadImgBuilder.setCancelable(false);
        uploadDialog = uploadImgBuilder.create();
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_prescribe.setOnClickListener(this);
        bt_response.setOnClickListener(this);
        bt_select.setOnClickListener(this);
        medicineAdapter = new MedicineAdapter(medicines);
        listView.setAdapter(medicineAdapter);
    }

    @Override
    protected void initData() {
        ways.add("电子处方");
        ways.add("电话诊疗");
        ways.add("视频诊疗");
        ways.add("来院诊疗");
        try {
            id = getIntent().getIntExtra("id", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_prescribe:
                startActivityForResult(new Intent(this, DoctorPrescribeActivity.class), REQUEST_CODE_FOR_PRESCRIBE);
                break;
            case R.id.bt_response:
                requestMedicines();
                break;
            case R.id.bt_select:
                showWayDialog();
                break;
        }
    }


    /**
     * 弹出选择用法对话框，选择用法
     */
    private void showWayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择回复方式")
                .setSingleChoiceItems(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ways)
                        , 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv_doctor_way.setText(ways.get(which));
                                dialog.dismiss();
                                if (which != 0) {
                                    clearMedicines();
                                    bt_prescribe.setEnabled(false);
                                } else {
                                    bt_prescribe.setEnabled(true);
                                }
                            }
                        })
                .show();
    }

    /**
     * 清除药方有关的信息
     */
    private void clearMedicines() {
        tv_prescribe.setVisibility(View.GONE);
        ll_remark.setVisibility(View.GONE);
        medicines.clear();
        et_medicine_remark.setText("");
        et_medicine_remark.setHint(getString(R.string.medicine_remark));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }
        if (requestCode == REQUEST_CODE_FOR_PRESCRIBE) {// 获得药品，添加到集合中，更新药品列表，显示医嘱输入框
            try {
                MedicineBean bean = (MedicineBean) data.getSerializableExtra("medicine");
                medicines.add(bean);
                tv_prescribe.setVisibility(View.VISIBLE);
                ll_remark.setVisibility(View.VISIBLE);
                medicineAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String createMedicinesJson() throws Exception {
        JSONArray medicineArray = new JSONArray();
        MedicineBean bean;
        JSONObject medicineObject;
        for (int i = 0; i < medicines.size(); i++) {
            bean = medicines.get(i);
            medicineObject = new JSONObject();
            medicineObject.put("medicines_id", bean.Medicines_id);
            medicineObject.put("medicines_name", bean.CommonName == null ? "" : bean.CommonName);
            medicineObject.put("medicines_specification", null == bean.Specification ? "" : bean.Specification);
            medicineObject.put("medicines_unit", bean.Unit);
            medicineObject.put("medicines_quantity", bean.medicines_quantity);
            medicineObject.put("medicines_usage", bean.medicines_usage);
            medicineArray.put(medicineObject);
        }
        return medicineArray.toString();
    }

    /**
     * 提交药方，如果没有开药品，则直接提交回应；如果开药品，先上传药品，成功后再提交回应
     */
    private void requestMedicines() {
        response = et_doctor_response.getText().toString().trim();
        if (TextUtils.isEmpty(response)) {
            MyToast.show(String.format("%s%s", getString(R.string.doctor_response), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(et_doctor_response, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }
        way = tv_doctor_way.getText().toString().trim();
        if (TextUtils.isEmpty(way)) {
            MyToast.show(String.format("%s%s", getString(R.string.doctor_response_way), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(bt_select, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }
        if (id == 0) {
            MyToast.show("未知错误，请重新登陆应用再试");
            return;
        }
        if (medicines.size() == 0 && ways.get(0).equalsIgnoreCase(tv_doctor_way.getText().toString().trim())) {
            MyToast.show("您还未开处方");
            ObjectAnimator.ofFloat(bt_prescribe, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }
        if (medicines.size() == 0) {
            requestResponse(0);
            return;
        }
        String remark = et_medicine_remark.getText().toString().trim();
        if (TextUtils.isEmpty(remark)) {
            MyToast.show(String.format("%s%s", getString(R.string.medicine_remark), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(et_medicine_remark, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }
        if (null == map) {
            map = new HashMap<>();
        }
        map.clear();
        map.put("appointment_id", id);
        map.put("remark", remark);
        try {
            map.put("Medicine", createMedicinesJson());
        } catch (Exception e) {
            e.printStackTrace();
            MyToast.show("未知错误，请重新登陆应用再试");
            return;
        }

        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE,
                        (String) params[0], (HashMap<String, Object>) params[1]);
            }

            @Override
            protected void onPreExecute() {
                uploadTextView.setText("正在上传药方");
                uploadDialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                if (null == result) {
                    MyToast.show(getString(R.string.connect_error));
                    uploadDialog.dismiss();
                } else {
                    try {
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))
                                || "error_1".equalsIgnoreCase(object.optString("status"))) {
                            requestResponse(1);
                        } else {
                            MyToast.show(getString(R.string.connect_error));
                            uploadDialog.dismiss();
                        }
                    } catch (Exception e) {
                        MyToast.show(getString(R.string.connect_error));
                        uploadDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        }.execute("sub_Prescription", map);
    }

    /**
     * 提交医生回应
     */
    private void requestResponse(int hasMedicines) {
        if (null == map) {
            map = new HashMap<>();
        }
        map.clear();
        map.put("appointment_id", id);
        map.put("doctor_Responses", response);
        map.put("doctor_dispose", 1);
        map.put("doctor_Way", way);
        map.put("IsPrescribe", hasMedicines);
        new AsyncTask<Object, Object, String>() {

            @Override
            protected void onPreExecute() {
                if (!uploadDialog.isShowing()) {
                    uploadDialog.show();
                }
                uploadTextView.setText("正在上传医生回应");
            }

            @Override
            protected String doInBackground(Object... params) {
                String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE,
                        (String) params[0], (HashMap<String, Object>) params[1]);
                uploadDialog.dismiss();
                if (null == result) {
                    MyToast.show(getString(R.string.connect_error));
                } else {
                    try {
                        JSONObject object = new JSONObject(result);
                        if ("error".equalsIgnoreCase(object.optString("status")))
                            MyToast.show(object.optString("msg"));
                        if ("ok".equalsIgnoreCase(object.optString("status"))
                                || "error_1".equalsIgnoreCase(object.optString("status"))) {
                            setResult(AppointDetailActivity.REQUEST_CODE_DOCTOR_RESPONSE, new Intent());
                            SystemClock.sleep(500);
                            finish();
                        }
                    } catch (Exception e) {
                        MyToast.show(getString(R.string.connect_error));
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute("sub_Responses", map);
    }
}
