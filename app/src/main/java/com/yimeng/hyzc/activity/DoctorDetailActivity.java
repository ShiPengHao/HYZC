package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.DoctorBean;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 医生详情界面，展示医生详情介绍，提交病人预约申请
 */
public class DoctorDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_avatar;
    private EditText et_disease_description;
    private Button bt_appoint;
    private Button bt_chat;
    private Button bt_back;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_age;
    private TextView tv_email;
    private TextView tv_remark;
    private DoctorBean doctorBean;
    private Calendar calendar;
    private DatePicker.OnDateChangedListener onDateChangedListener;
    private String date;
    private ImageView iv_back;
    private Button bt_choose_date;
    private TextView tv_appointment_date;
    private TextView tv_phone;
    private TextView tv_wechat;
    //    private NumberPicker timePicker;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_detail;
    }

    @Override
    protected void initView() {
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_disease_description = (EditText) findViewById(R.id.et_disease_description);
        bt_appoint = (Button) findViewById(R.id.bt_appoint);
        bt_chat = (Button) findViewById(R.id.bt_chat);
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_choose_date = (Button) findViewById(R.id.bt_choose_date);
//        timePicker = (NumberPicker) findViewById(R.id.numberPicker1);


        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_wechat = (TextView) findViewById(R.id.tv_wechat);
        tv_appointment_date = (TextView) findViewById(R.id.tv_appointment_date);
    }

    @Override
    protected void setListener() {
        bt_appoint.setOnClickListener(this);
        bt_chat.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        bt_choose_date.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            doctorBean = (DoctorBean) intent.getSerializableExtra("doctor");
        }
        if (null == doctorBean) {
            return;
        }
        tv_name.setText(String.format("%s：%s", getString(R.string.name),
                isEmpty(doctorBean.doctor_name) ? getString(R.string.empty_content) : doctorBean.doctor_name));
        tv_sex.setText(String.format("%s：%s", getString(R.string.sex),
                isEmpty(doctorBean.doctor_sex) ? getString(R.string.male) : doctorBean.doctor_sex));
        tv_age.setText(String.format("%s：%s", getString(R.string.age),
                isEmpty(doctorBean.doctor_age) ? getString(R.string.empty_content) : doctorBean.doctor_age));
        tv_email.setText(String.format("%s：%s", getString(R.string.email),
                isEmpty(doctorBean.doctor_email) ? getString(R.string.empty_content) : doctorBean.doctor_email));
        tv_phone.setText(String.format("%s：%s", getString(R.string.phone),
                isEmpty(doctorBean.doctor_phone) ? getString(R.string.empty_content) : doctorBean.doctor_phone));
        tv_wechat.setText(String.format("%s：%s", getString(R.string.wechat),
                isEmpty(doctorBean.doctor_WeChat) ? getString(R.string.empty_content) : doctorBean.doctor_WeChat));
        tv_remark.setText(String.format("%s：%s", getString(R.string.doctor_introduce),
                isEmpty(doctorBean.remark) ? getString(R.string.empty_content) : doctorBean.remark.replace("\n", "")));
        Picasso.with(this)
                .load(MyConstant.NAMESPACE + doctorBean.doctor_avatar)
                .resize(DensityUtil.dip2px(96),DensityUtil.dip2px(96))
                .into(iv_avatar);
        calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 15) {//15点-24点，只能挂第二天的号，所以使用第二天的日历，日期为第二天日期，时间默认为8点，范围为8-17
            calendar.setTime(new Date(System.currentTimeMillis() + 9 * 60 * 60 * 1000));
//            timePicker.setValue(8);
//            timePicker.setMinValue(8);
//        } else {//0-15点，只能挂2个小时以后的号，而且必须在工作时间8点以后
//            timePicker.setValue(Math.max(hourOfDay + 2, 8));
//            timePicker.setMinValue(Math.max(hourOfDay + 2, 8));
        }
//        timePicker.setMaxValue(17);
        date = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        tv_appointment_date.setText(String.format("%s：%s", getString(R.string.appointment_date), date));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_appoint:
                requestAppoint();
                break;
            case R.id.bt_chat:
                MyToast.show(String.format("%s%s", getString(R.string.chat_online), getString(R.string.fun_undo)));
                break;
            case R.id.iv_back:
            case R.id.bt_back:
                finish();
                break;
            case R.id.bt_choose_date:
                showDatePickDialog();
                break;
        }
    }

    /**
     * 选择日期
     */
    private void showDatePickDialog() {
        calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay > 15) {//15点-24点，只能挂第二天的号，所以使用第二天的日历
            calendar.setTime(new Date(System.currentTimeMillis() + 9 * 60 * 60 * 1000));
        }
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            private boolean isFirst = true;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (isFirst) {
                    date = String.valueOf(year) + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    tv_appointment_date.setText(String.format("%s：%s", getString(R.string.appointment_date), date));
                    isFirst = false;
                }
            }
        }, year, monthOfYear, dayOfMonth);
        if (onDateChangedListener == null) {
            onDateChangedListener = new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int tempYear, int tempMonthOfYear, int tempDayOfMonth) {
                    Calendar tempCalendar = Calendar.getInstance();
                    tempCalendar.set(tempYear, tempMonthOfYear, tempDayOfMonth);
                    if (!tempCalendar.after(calendar) // 所选时间在目前时刻之前
                            || tempCalendar.getTimeInMillis() - calendar.getTimeInMillis() > 5 * 24 * 60 * 60 * 1000// 所选时间在5天之后
                            ) {
                        view.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
                    }
                }
            };
        }
        datePickerDialog.getDatePicker().init(year, monthOfYear, dayOfMonth, onDateChangedListener);
        datePickerDialog.show();
    }

    /**
     * 提交预约申请
     */
    private void requestAppoint() {
        String description = et_disease_description.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            MyToast.show("请填写病情描述");
            ObjectAnimator.ofFloat(et_disease_description, "translationX", -25, 25, -25, 25, 0).setDuration(500).start();
            return;
        }

        String patientId = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE).getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
        if (TextUtils.isEmpty(patientId)) {
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("patient_id", patientId);
        params.put("disease_description", description);
        params.put("select_doctor_id", doctorBean.doctor_id);
        params.put("registration_time", date);
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Patient_Sub_Applications",
                            (Map<String, Object>) params[0]);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result == null) {
                    MyToast.show(getString(R.string.connect_error));
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if ("ok".equalsIgnoreCase(object.optString("status"))
                            || "error_1".equalsIgnoreCase(object.optString("status"))) {
                        bt_appoint.setEnabled(false);
                        showOkTips();
                    } else {
                        MyToast.show(object.optString("msg"));
                    }
                } catch (Exception e) {
                    MyToast.show(getString(R.string.connect_error));
                    e.printStackTrace();
                }
            }
        }.execute(params);
    }

    /**
     * 显示预约成功的提示对话框
     */
    private void showOkTips() {
        new AlertDialog.Builder(this)
                .setTitle("预约成功！")
                .setMessage("祝你早日康复！")
                .setCancelable(true)
                .setPositiveButton("关闭页面", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
}
