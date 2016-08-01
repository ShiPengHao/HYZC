package com.yimeng.hyzc.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AppointmentBean;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 预约详情界面
 */
public class AppointDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_description;
    private TextView tv_patient_name;
    private TextView tv_patient_sex;
    private TextView tv_patient_age;
    private TextView tv_patient_phone;
    private TextView tv_response;
    private TextView tv_response_time;
    private TextView tv_appointmentTime;
    private TextView tv_appointmentId;
    private TextView tv_doctor;
    private TextView tv_appointStatus;
    private TextView tv_response_way;
    private TextView tv_appointment_add_time;
    private ImageView iv_back;
    private AppointmentBean bean;
    private int appointment_id;
    private HashMap<String, Object> params = new HashMap<>();
    ;
    private String type;
    private Button bt_response;
    private Button bt_cancel;
    private Button bt_prescription;
    public static final int REQUEST_CODE_DOCTOR_RESPONSE = 100;
    private boolean updateFlag;
    private AlertDialog alertDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_appointment_detail;
    }

    @Override
    protected void initView() {
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_patient_name = (TextView) findViewById(R.id.tv_name);
        tv_patient_sex = (TextView) findViewById(R.id.tv_sex);
        tv_patient_age = (TextView) findViewById(R.id.tv_age);
        tv_patient_phone = (TextView) findViewById(R.id.tv_phone);
        tv_response = (TextView) findViewById(R.id.tv_response);
        tv_response_time = (TextView) findViewById(R.id.tv_response_time);
        tv_appointmentTime = (TextView) findViewById(R.id.tv_appointmentTime);
        tv_appointmentId = (TextView) findViewById(R.id.tv_appointmentId);
        tv_doctor = (TextView) findViewById(R.id.tv_doctor);
        tv_appointStatus = (TextView) findViewById(R.id.tv_appointStatus);
        tv_response_way = (TextView) findViewById(R.id.tv_response_way);
        tv_appointment_add_time = (TextView) findViewById(R.id.tv_appointment_add_time);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        bt_response = (Button) findViewById(R.id.bt_response);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_prescription = (Button) findViewById(R.id.bt_prescription);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_response.setOnClickListener(this);
        bt_prescription.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (null == getIntent()) {
            return;
        }
        appointment_id = getIntent().getIntExtra("id", 0);
        type = getIntent().getStringExtra("type");
        if ("doctor".equalsIgnoreCase(type)) {
            bt_response.setVisibility(View.VISIBLE);
            bt_cancel.setVisibility(View.GONE);
        } else {
            bt_response.setVisibility(View.GONE);
        }
        requestAppointmentDetail();
    }

    private void bindData() {
        tv_appointmentId.setText(String.format("%s：%s", getString(R.string.appointment_id), bean.appointment_id));
        tv_description.setText(String.format("%s：%s", getString(R.string.disease_description),
                isEmpty(bean.disease_description) ? getString(R.string.empty_content) : bean.disease_description));
        tv_doctor.setText(String.format("%s：%s", getString(R.string.doctor),
                isEmpty(bean.doctor_name) ? getString(R.string.empty_content) : bean.doctor_name));
        tv_response.setText(String.format("%s：%s", getString(R.string.doctor_response),
                isEmpty(bean.doctor_Responses) ? getString(R.string.empty_content) : bean.doctor_Responses));
        try {
            String date = bean.registration_time.substring(bean.registration_time.indexOf("(") + 1, bean.registration_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(date)));
            tv_appointmentTime.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.appointment_time),
                    date));
        } catch (Exception e) {
            tv_appointmentTime.setText(MyApp.getAppContext().getString(R.string.appointment_time));
        }
        try {
            String date = bean.add_time.substring(bean.add_time.indexOf("(") + 1, bean.add_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date)));
            tv_appointment_add_time.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.appointment_add_time),
                    date));
        } catch (Exception e) {
            tv_appointment_add_time.setText(MyApp.getAppContext().getString(R.string.appointment_add_time));
        }
        try {
            String date = bean.doctor_Responses_time.substring(bean.doctor_Responses_time.indexOf("(") + 1, bean.doctor_Responses_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date)));
            tv_response_time.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.doctor_response_time),
                    date));
        } catch (Exception e) {
            tv_response_time.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.doctor_response_time),
                    getString(R.string.empty_content)));
        }
        tv_patient_age.setText(String.format("%s：%s", getString(R.string.age), bean.patient_age));
        tv_patient_name.setText(String.format("%s：%s", getString(R.string.username),
                isEmpty(bean.patient_name) ? getString(R.string.empty_content) : bean.patient_name));
        tv_response_way.setText(String.format("%s：%s", getString(R.string.doctor_response_way),
                isEmpty(bean.doctor_Way) ? getString(R.string.empty_content) : bean.doctor_Way));
        tv_patient_phone.setText(String.format("%s：%s", getString(R.string.phone),
                isEmpty(bean.patient_phone) ? getString(R.string.empty_content) : bean.patient_phone));
        tv_patient_sex.setText(String.format("%s：%s", getString(R.string.sex), bean.patient_sex));
        if (bean.doctor_dispose == 0) {
            tv_appointStatus.setText(String.format("%s：%s", getString(R.string.appointment_status), getString(R.string.no_response)));
            tv_appointStatus.setTextColor(Color.RED);
            if (type.equalsIgnoreCase("patient")) {
                bt_cancel.setVisibility(View.VISIBLE);
            } else {
                bt_cancel.setVisibility(View.GONE);
            }
        } else {
            bt_cancel.setVisibility(View.GONE);
            bt_response.setVisibility(View.GONE);
            tv_appointStatus.setText(String.format("%s：%s", getString(R.string.appointment_status), getString(R.string.has_response)));
            tv_appointStatus.setTextColor(getResources().getColor(R.color.colorAccent));
            if (bean.prescription_id != 0) {
                bt_prescription.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 请求预约详情
     */
    private void requestAppointmentDetail() {
        params.clear();
        params.put("appointment_id", appointment_id);
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null) {
                    String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Load_patient_detail",
                            (Map<String, Object>) params[0]);
                    parseAppointDetail(result);
                    return result;
                } else {
                    return null;
                }
            }
        }.execute(params);
    }

    /**
     * 解析预约详情数据
     *
     * @param result json数据
     */
    private void parseAppointDetail(String result) {
        if (result == null) {
            MyToast.show(getString(R.string.connect_error));
            return;
        }
        try {
            JSONObject object = new JSONObject(result);
            if (object.optInt("total") == 1) {
                ArrayList<AppointmentBean> tempData = new Gson().fromJson(object.optString("data"),
                        new TypeToken<ArrayList<AppointmentBean>>() {
                        }.getType());
                if (tempData.size() > 0) {
                    bean = tempData.get(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bindData();
                        }
                    });
                } else {
                    MyToast.show(context.getString(R.string.connect_error));
                }
            } else {
                MyToast.show(context.getString(R.string.connect_error));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (updateFlag) {
                    setResult(100, new Intent());
                }
                finish();
                break;
            case R.id.bt_response:
                startActivityForResult(new Intent(this, DoctorResponseActivity.class).putExtra("id", appointment_id), REQUEST_CODE_DOCTOR_RESPONSE);
                break;
            case R.id.bt_cancel:
                showCancelDialog();
                break;
            case R.id.bt_prescription:
                startActivity(new Intent(this, PrescribeDetailActivity.class).putExtra("prescription_id", bean.prescription_id));
                break;
        }
    }

    /**
     * 显示取消预约对话框
     */
    private void showCancelDialog() {
        if (null == alertDialog) {
            alertDialog = new AlertDialog.Builder(this).setTitle("温馨提示")
                    .setMessage("您确定要取消此预约单吗？")
                    .setNegativeButton("确定取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestCancelAppointment();
                        }
                    })
                    .setPositiveButton("不取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("取消并且重新预约", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestCancelAppointment();
                            startActivity(new Intent(AppointDetailActivity.this, BookingActivity.class));
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }

    /**
     * 请求取消此预约单//TODO 病人取消预约单
     */
    private void requestCancelAppointment() {
//        params.clear();
//        params.put("appointment_id", appointment_id);
//        new AsyncTask<Object, Object, String>() {
//            @Override
//            protected String doInBackground(Object... params) {
//                if (params != null) {
//                    String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Load_patient_detail",
//                            (Map<String, Object>) params[0]);
//                    parseAppointDetail(result);
//                    return result;
//                } else {
//                    return null;
//                }
//            }
//        }.execute(params);
    }

    @Override
    protected void onDestroy() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_DOCTOR_RESPONSE:
                requestAppointmentDetail();
                updateFlag = true;
                break;
        }


    }
}
