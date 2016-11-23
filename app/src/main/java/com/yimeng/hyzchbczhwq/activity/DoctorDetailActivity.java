package com.yimeng.hyzchbczhwq.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;
import com.squareup.picasso.Picasso;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.DoctorBean;
import com.yimeng.hyzchbczhwq.huanxin.ChatActivity;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.KeyBoardUtils;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.WebServiceUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 医生详情界面，展示医生详情介绍，提交病人预约申请
 */
public class DoctorDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_FOR_DISEASE_MODULE = 100;
    private static final int REQUEST_CODE_FOR_PATIENT = 101;
    /**
     * 限制最大可预约天数
     */
    private static final int DATE_LIMIT_DAYS = 5;
    /**
     * 限制每天最晚可预约的时间
     */
    private static final int DATE_LIMIT_LAST_HOUR = 15;
    /**
     * 限制每天最早可预约的时间
     */
    private static final int DATE_LIMIT_FIRST_HOUR = 8;
    /**
     * 限制预约的时间与当前时间差，即现在只能预约n小时以后的号
     */
    private static final int DATE_LIMIT_AFTER_HOURS = 2;

    private String[] days = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    private ImageView iv_avatar;
    private EditText et_disease_description;
    private Button bt_appoint;
    private Button bt_chat;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_age;
    private TextView tv_email;
    private TextView tv_remark;
    private DoctorBean doctorBean;
//    private DatePicker.OnDateChangedListener onDateChangedListener;
    private String date;
    private ImageView iv_back;
    private LinearLayout ll_choose_date;
    private TextView tv_appointment_date;
    private TextView tv_phone;
    private TextView tv_wechat;
    private TextView tv_disease_description;
    private TextView tv_doctor_title;
    private TextView tv_choose_patient;
    private String module;
    private RatingBar rating_bar;
    private RelativeLayout rl_score;
    private String patient_id;
    private ArrayList<String> workDays = new ArrayList<>();
    private AlertDialog datePickerDialog;
    private String today;
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
        ll_choose_date = (LinearLayout) findViewById(R.id.ll_choose_date);
//        timePicker = (NumberPicker) findViewById(R.id.numberPicker1);


        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_wechat = (TextView) findViewById(R.id.tv_wechat);
        tv_disease_description = (TextView) findViewById(R.id.tv_disease_description);
        tv_appointment_date = (TextView) findViewById(R.id.tv_appointment_date);
        tv_doctor_title = (TextView) findViewById(R.id.tv_doctor_title);
        tv_choose_patient = (TextView) findViewById(R.id.tv_choose_patient);

        rating_bar = (RatingBar) findViewById(R.id.rating_bar);
        rl_score = (RelativeLayout) findViewById(R.id.rl_score);
    }

    @Override
    protected void setListener() {
        bt_appoint.setOnClickListener(this);
        bt_chat.setOnClickListener(this);
        rl_score.setOnClickListener(this);
        ll_choose_date.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_disease_description.setOnClickListener(this);
        tv_choose_patient.setOnClickListener(this);
    }

    /**
     * 根据医生id获取这个医生的满意度平均分
     */
    private void requestCommentScore() {
        HashMap<String, Object> values = new HashMap<>();
        values.put("doctor_id", doctorBean.doctor_id);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    rating_bar.setRating(new JSONObject(s).optInt("AVG"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("get_doctor_AVG", values);
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
        requestCommentScore();
        String doctorTitle = context.getString(R.string.general_doctor);
        switch (doctorBean.doctor_title) {
            case 0:
                doctorTitle = context.getString(R.string.village_doctor);
                break;
            case 1:
                doctorTitle = context.getString(R.string.general_doctor);
                break;
            case 2:
                doctorTitle = context.getString(R.string.chief_doctor);
                break;
            case 3:
                doctorTitle = context.getString(R.string.vice_director_doctor);
                break;
            case 4:
                doctorTitle = context.getString(R.string.director_doctor);
                break;
        }
        tv_doctor_title.setText(String.format("%s：%s", getString(R.string.doctor_title), doctorTitle));
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
                .resize(DensityUtil.dip2px(96), DensityUtil.dip2px(96))
                .into(iv_avatar);
        getDoctorWorkDays();
    }

    /**
     * 获取此医生可预约的时间
     */
    private void getDoctorWorkDays() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (todayOfWeek == 0)
            todayOfWeek = 7;
        today = "今天：" + String.valueOf(calendar.get(Calendar.YEAR)) + "年" + (calendar.get(Calendar.MONTH) + 1)
                + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日,  星期" + days[todayOfWeek - 1];
        int i = 0;
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= DATE_LIMIT_LAST_HOUR) {//15点-24点，只能挂第二天的号，所以使用第二天的日历，日期为第二天日期，时间默认为8点，范围为8-17
            i = 1;
            calendar.setTime(new Date(System.currentTimeMillis() + (24 - DATE_LIMIT_LAST_HOUR) * 60 * 60 * 1000));
//            timePicker.setValue(DATE_LIMIT_FIRST_HOUR);
//            timePicker.setMinValue(DATE_LIMIT_FIRST_HOUR);
//        } else {//0-15点，只能挂2个小时以后的号，而且必须在工作时间8点以后
//            timePicker.setValue(Math.max(hourOfDay + DATE_LIMIT_AFTER_HOURS, DATE_LIMIT_FIRST_HOUR));
//            timePicker.setMinValue(Math.max(hourOfDay + DATE_LIMIT_AFTER_HOURS, DATE_LIMIT_FIRST_HOUR));
        }
//        timePicker.setMaxValue(DATE_LIMIT_LAST_HOUR+DATE_LIMIT_AFTER_HOURS);
//        date = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
//        tv_appointment_date.setText(String.format("%s：%s", getString(R.string.appointment_date), date));
        int dayOfWeek;
        workDays.clear();
        while (i < DATE_LIMIT_DAYS) {
            dayOfWeek = (todayOfWeek + i) % 7;
            if (dayOfWeek == 0)
                dayOfWeek = 7;
            if (doctorBean.Is_Order == 1 || doctorBean.weekday.contains(String.valueOf(dayOfWeek))) {// 当天坐诊
                workDays.add(String.valueOf(calendar.get(Calendar.YEAR)) + "-" + (calendar.get(Calendar.MONTH) + 1)
                        + "-" + calendar.get(Calendar.DAY_OF_MONTH) + ",  星期" + days[dayOfWeek - 1]);
            }
            calendar.setTime(new Date(System.currentTimeMillis() + (i + 1) * 24 * 60 * 60 * 1000));
            i++;
        }
        if (workDays.size() == 0)
            bt_appoint.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        KeyBoardUtils.closeKeybord(et_disease_description, this);
        switch (v.getId()) {
            case R.id.bt_appoint:
                requestAppoint();
                break;
            case R.id.bt_chat:
                startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, "doctor_" + doctorBean.doctor_id));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_choose_date:
                showDatePickDialog();
                break;
            case R.id.tv_disease_description:
                pickDiseaseModule();
                break;
            case R.id.tv_choose_patient:
                pickPatient();
                break;
            case R.id.rl_score:
                startActivity(new Intent(this, DoctorScoreDetailActivity.class).putExtra("doctor", doctorBean));
                break;
        }
    }

    /**
     * 选择当前账号下的病人
     */
    private void pickPatient() {
        startActivityForResult(new Intent(this, PatientListActivity.class)
                        .putExtra(PatientListActivity.EXTRA_CHOOSE_OR_QUERY, PatientListActivity.EXTRA_CHOOSE)
                , REQUEST_CODE_FOR_PATIENT);
    }

    /**
     * 选择疾病描述模板
     */
    private void pickDiseaseModule() {
        if (doctorBean == null)
            return;
        startActivityForResult(new Intent(this, DiseaseTemplateActivity.class)
                .putExtra("departments_id", doctorBean.departments_id), REQUEST_CODE_FOR_DISEASE_MODULE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_FOR_DISEASE_MODULE:
                ArrayList<String> checkedItems = (ArrayList<String>) data.getSerializableExtra(DiseaseTemplateActivity.EXTRA_CHECKED_ITEMS);
                if (checkedItems == null)
                    return;
                String module = "";
                for (int i = 0; i < checkedItems.size(); i++) {
                    module += checkedItems.get(i);
                    if (i != checkedItems.size() - 1)
                        module += "\r\n";
                }
                if (isEmpty(module))
                    module = "无";
                tv_disease_description.setText("病情模板：" + module);
                this.module = tv_disease_description.getText().toString();
                break;
            case REQUEST_CODE_FOR_PATIENT:
                patient_id = data.getStringExtra("patient_id");
                tv_choose_patient.setText(String.format("%s:  %s", getString(R.string.patient_name), data.getStringExtra("patient_name")));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择预约日期
     */
    private void showDatePickDialog() {
        if (workDays.size() == 0) {
            MyToast.show("该医生" + DATE_LIMIT_DAYS + "天内不可预约，看看其它医生吧！");
            return;
        }
        if (datePickerDialog == null)
            datePickerDialog = new AlertDialog.Builder(this)
                    .setSingleChoiceItems(new ArrayAdapter(this, android.R.layout.simple_list_item_1, workDays)
                            , 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    String s = workDays.get(which);
                                    date = s.substring(0, s.indexOf(","));
                                    tv_appointment_date.setText(String.format("%s：%s", getString(R.string.appointment_date), date));
                                }
                            })
                    .setTitle(today)
                    .create();
        datePickerDialog.show();

//        calendar = Calendar.getInstance();
//        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
//        if (hourOfDay > 15) {//15点-24点，只能挂第二天的号，所以使用第二天的日历
//            calendar.setTime(new Date(System.currentTimeMillis() + 9 * 60 * 60 * 1000));
//        }
//        int year = calendar.get(Calendar.YEAR);
//        int monthOfYear = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            private boolean isFirst = true;
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                if (isFirst) {
//                    date = String.valueOf(year) + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
//                    tv_appointment_date.setText(String.format("%s：%s", getString(R.string.appointment_date), date));
//                    isFirst = false;
//                }
//            }
//        }, year, monthOfYear, dayOfMonth);
//        if (onDateChangedListener == null) {
//            onDateChangedListener = new DatePicker.OnDateChangedListener() {
//                @Override
//                public void onDateChanged(DatePicker view, int tempYear, int tempMonthOfYear, int tempDayOfMonth) {
//                    Calendar tempCalendar = Calendar.getInstance();
//                    tempCalendar.set(tempYear, tempMonthOfYear, tempDayOfMonth);
//                    if (!tempCalendar.after(calendar) // 所选时间在目前时刻之前
//                            || tempCalendar.getTimeInMillis() - calendar.getTimeInMillis() > DATE_LIMIT_DAYS * 24 * 60 * 60 * 1000// 所选时间在5天之后
//                            ) {
//                        MyToast.show("当前日期不可预约，请选择" + DATE_LIMIT_DAYS + "天之内的时间");
//                        view.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
//                    }
//                }
//            };
//        }
//        datePickerDialog.getDatePicker().init(year, monthOfYear, dayOfMonth, onDateChangedListener);
//        datePickerDialog.show();
    }

    /**
     * 提交预约申请
     */
    private void requestAppoint() {

        String description = et_disease_description.getText().toString().trim();
//        if (TextUtils.isEmpty(description)) {
//            MyToast.show("请填写病情描述");
//            ObjectAnimator.ofFloat(et_disease_description, "translationX", -25, 25, -25, 25, 0).setDuration(500).start();
//            return;
//        }
        if (TextUtils.isEmpty(patient_id)) {
            MyToast.show("请选择就诊者");
            ObjectAnimator.ofFloat(tv_choose_patient, "translationX", -25, 25, -25, 25, 0).setDuration(500).start();
            return;
        }

        if (TextUtils.isEmpty(module)) {
            MyToast.show("请选择病情模板");
            ObjectAnimator.ofFloat(tv_disease_description, "translationX", -25, 25, -25, 25, 0).setDuration(500).start();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            MyToast.show("请选择预约日期");
            ObjectAnimator.ofFloat(ll_choose_date, "translationX", -25, 25, -25, 25, 0).setDuration(500).start();
            return;
        }


        HashMap<String, Object> params = new HashMap<>();
        params.put("patient_id", patient_id);
        params.put("disease_description", this.module + "\r\n病情描述：" + description);
        params.put("select_doctor_id", doctorBean.doctor_id);
        params.put("registration_time", date);
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE
                            , "Patient_Sub_Applications", (Map<String, Object>) params[0]);
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

    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    private void dismissDialog() {
        if (datePickerDialog != null && datePickerDialog.isShowing())
            datePickerDialog.dismiss();
    }

}
