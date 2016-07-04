package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.DoctorAdapter;
import com.yimeng.hyzc.bean.AddressBean;
import com.yimeng.hyzc.bean.DepartmentBean;
import com.yimeng.hyzc.bean.DoctorBean;
import com.yimeng.hyzc.bean.HospitalBean;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.UiUtils;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询医生，在线预约的activity
 */
public class BookingActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private Map<String, Object> values = new HashMap<>();
    private ArrayList<AddressBean> province = new ArrayList<>();
    private ArrayAdapter<AddressBean> provinceAdapter;
    private ArrayList<AddressBean> city = new ArrayList<>();
    private ArrayAdapter<AddressBean> cityAdapter;
    private ArrayList<AddressBean> area = new ArrayList<>();
    private ArrayAdapter<AddressBean> areaAdapter;
    private ArrayList<HospitalBean> hospital = new ArrayList<>();
    private ArrayAdapter<HospitalBean> hospitalAdapter;
    private ArrayList<DepartmentBean> department = new ArrayList<>();
    private ArrayAdapter<DepartmentBean> departmentAdapter;
    private ArrayList<DepartmentBean> professional = new ArrayList<>();
    private ArrayAdapter<DepartmentBean> professionalAdapter;
    private ArrayList<DoctorBean> doctor = new ArrayList<>();

    private boolean isIniting = true;
    private Spinner spinner_province;
    private Spinner spinner_city;
    private Spinner spinner_area;
    private Spinner spinner_hospital;
    private Spinner spinner_department;
    private Spinner spinner_professional;
    private GridView gridView;
    private DoctorAdapter doctorAdapter;
    private TextView tv_title;
    private PopupWindow popupWindow;
    private ImageView iv_avatar;
    private EditText et_disease_description;
    private Button bt_appoint;
    private Button bt_chat;
    private Button bt_back;
    private View popView;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_age;
    private TextView tv_email;
    private TextView tv_remark;
    private DoctorBean doctorBean;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private DatePicker.OnDateChangedListener onDateChangedListener;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_booking;
    }

    @Override
    protected void initView() {
        spinner_province = (Spinner) findViewById(R.id.spinner_province);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_area = (Spinner) findViewById(R.id.spinner_area);
        spinner_hospital = (Spinner) findViewById(R.id.spinner_hospital);
        spinner_department = (Spinner) findViewById(R.id.spinner_department);
        spinner_professional = (Spinner) findViewById(R.id.spinner_professional);
        gridView = (GridView) findViewById(R.id.gv);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected void setListener() {
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, province);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_province.setAdapter(provinceAdapter);
        spinner_province.setOnItemSelectedListener(this);

        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, city);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(cityAdapter);
        spinner_city.setOnItemSelectedListener(this);

        areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, area);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_area.setAdapter(areaAdapter);
        spinner_area.setOnItemSelectedListener(this);

        hospitalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hospital);
        hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_hospital.setAdapter(hospitalAdapter);
        spinner_hospital.setOnItemSelectedListener(this);

        departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, department);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_department.setAdapter(departmentAdapter);
        spinner_department.setOnItemSelectedListener(this);

        professionalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, professional);
        professionalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_professional.setAdapter(professionalAdapter);
        spinner_professional.setOnItemSelectedListener(this);

        doctorAdapter = new DoctorAdapter(doctor);
        gridView.setAdapter(doctorAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        requestAddress("GetProvince");
    }

    /**
     * 执行异步任务，请求省市区3级地址
     *
     * @param params 方法名+参数列表（哈希表形式）+flag标志
     */
    public void requestAddress(final Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length >= 2) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                } else if (params != null && params.length == 1) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                int flag = 0;
                if (params != null && params.length == 3) {
                    flag = (int) params[2];
                }
                if (result != null) {
                    parseAddressJson(result, flag);
                }
            }

        }.execute(params);
    }

    /**
     * 解析地址json数据
     *
     * @param flag   省市区的标志，分别为0，1，2
     * @param result json数据
     */
    private void parseAddressJson(String result, int flag) {
        try {
            JSONObject object = new JSONObject(result);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<AddressBean>>() {
            }.getType();
            ArrayList<AddressBean> datas = gson.fromJson(object.optString("data"), type);
            updateAddress(flag, datas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新地址数据和spinner适配器
     *
     * @param flag  省市区的标志，分别为0，1，2
     * @param datas 数据源
     */
    private void updateAddress(int flag, ArrayList<AddressBean> datas) {
        switch (flag) {
            case 0:// 更新省份数据
                province.clear();
                province.addAll(datas);
                provinceAdapter.notifyDataSetChanged();
                if (province.size() > 0) {
                    int i = province.size() - 1;
                    while (i >= 0) {
                        if ("410000".equalsIgnoreCase(province.get(i).code)) {
                            break;
                        }
                        i--;
                    }
                    spinner_province.setSelection(i);
                    if (!isIniting) {
                        values.clear();
                        values.put("provincecode", province.get(i).code);
                        requestAddress("GetCity", values, 1);
                    }
                }
                break;
            case 1:// 更新市，并且手动请求对应区的数据
                city.clear();
                city.addAll(datas);
                cityAdapter.notifyDataSetChanged();
                if (city.size() > 0) {
                    spinner_city.setSelection(0);
                    if (!isIniting) {
                        values.clear();
                        values.put("citycode", city.get(0).code);
                        requestAddress("GetArea", values, 2);
                    }
                }
                break;
            case 2:// 更新区，并且请求对应医院的信息
                area.clear();
                area.addAll(datas);
                areaAdapter.notifyDataSetChanged();
                if (area.size() > 0) {
                    spinner_area.setSelection(0);
                    if (!isIniting) {
                        values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                        values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
                        values.put("area", area.get(0).code);// 二七区 410103
                        requestHospital("Load_Hospital", values);
                    }
                }

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        values.clear();
        AddressBean bean;
        switch (parent.getId()) {
            case R.id.spinner_province:
                bean = province.get(position);
                values.put("provincecode", bean.code);// 河南 410000
                requestAddress("GetCity", values, 1);
                break;
            case R.id.spinner_city:
                bean = city.get(position);
                values.put("citycode", bean.code);// 郑州 410100
                requestAddress("GetArea", values, 2);
                break;
            case R.id.spinner_area:
                bean = area.get(position);
                values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
                values.put("area", bean.code);// 二七区 410103
                requestHospital("Load_Hospital", values);
                break;
            case R.id.spinner_hospital:
                values.clear();
                values.put("hospital_id", hospital.get(spinner_hospital.getSelectedItemPosition()).hospital_id);
                values.put("parentid", 0);
                requestDepartment("Load_KS", values, 0);
                break;
            case R.id.spinner_department:
                values.clear();
                values.put("hospital_id", department.get(spinner_department.getSelectedItemPosition()).hospital_id);
                values.put("parentid", department.get(spinner_department.getSelectedItemPosition()).departments_id);
                requestDepartment("Load_KS", values, 1);
                break;
            case R.id.spinner_professional:
                if (isIniting) {
                    isIniting = false;
                }
                values.clear();
                values.put("departments_id", professional.get(spinner_professional.getSelectedItemPosition()).departments_id);
                requestDoctor("Load_Doctor", values);
                break;
        }
    }

    /**
     * 根据选择的省市区，选择医院，解析成功后刷新界面，并请求科室信息
     *
     * @param params 方法名+参数列表
     */
    public void requestHospital(final Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length >= 2) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                } else if (params != null && params.length == 1) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        hospital.clear();
                        JSONObject object = new JSONObject(result);
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<HospitalBean>>() {
                        }.getType();
                        ArrayList<HospitalBean> datas = gson.fromJson(object.optString("data"), type);
                        hospital.addAll(datas);
                        hospitalAdapter.notifyDataSetChanged();
                        if (isIniting) {
                            return;
                        }
                        if (hospital.size() > 0) {
                            spinner_hospital.setSelection(0);
                            if (!isIniting) {
                                values.clear();
                                values.put("hospital_id", hospital.get(0).hospital_id);
                                values.put("parentid", 0);
                                requestDepartment("Load_KS", values, 0);
                            }
                        } else {
                            department.clear();
                            departmentAdapter.notifyDataSetChanged();

                            professional.clear();
                            professionalAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(params);
    }

    /**
     * 根据选择的医院请求科室数据
     *
     * @param params 方法名+参数列表+标志，0表示科室，1表示2级科室即专业
     */
    public void requestDepartment(final Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length >= 2) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                } else if (params != null && params.length == 1) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null && params.length == 3) {
                    switch ((int) params[2]) {
                        case 0:
                            parseDepartmentResult(result);
                            break;
                        case 1:
                            parseProfessionalResult(result);
                            break;
                    }
                }
            }
        }.execute(params);
    }

    /**
     * 解析科室信息
     *
     * @param result 科室信息json
     */
    private void parseDepartmentResult(String result) {
        try {
            department.clear();
            JSONObject object = new JSONObject(result);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DepartmentBean>>() {
            }.getType();
            ArrayList<DepartmentBean> datas = gson.fromJson(object.optString("data"), type);
            department.addAll(datas);
            departmentAdapter.notifyDataSetChanged();
            if (isIniting) {
                return;
            }
            if (department.size() > 0) {
                spinner_department.setSelection(0);
                if (!isIniting) {
                    values.clear();
                    values.put("hospital_id", department.get(0).hospital_id);
                    values.put("parentid", department.get(0).departments_id);
                    requestDepartment("Load_KS", values, 1);
                }
            } else {
                professional.clear();
                professionalAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析专业信息
     *
     * @param result 专业信息json
     */
    private void parseProfessionalResult(String result) {
        try {
            professional.clear();
            JSONObject object = new JSONObject(result);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DepartmentBean>>() {
            }.getType();
            ArrayList<DepartmentBean> datas = gson.fromJson(object.optString("data"), type);
            professional.addAll(datas);
            professionalAdapter.notifyDataSetChanged();
            if (professional.size() > 0) {
                spinner_professional.setSelection(0);
                if (!isIniting) {
                    values.clear();
                    values.put("departments_id", professional.get(0).departments_id);
                    requestDoctor("Load_Doctor", values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据选择的医院请求科室数据
     *
     * @param params 方法名+参数列表+标志，0表示科室，1表示2级科室即专业
     */
    public void requestDoctor(final Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length >= 2) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                } else if (params != null && params.length == 1) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    parseDoctorResult(result);
                }
            }
        }.execute(params);
    }

    /**
     * 解析专业信息
     *
     * @param result 专业信息json
     */
    private void parseDoctorResult(String result) {
        doctor.clear();
        try {
            JSONObject object = new JSONObject(result);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DoctorBean>>() {
            }.getType();
            ArrayList<DoctorBean> datas = gson.fromJson(object.optString("data"), type);
            doctor.addAll(datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doctorAdapter.notifyDataSetChanged();
        if (doctor.size() > 0) {
            gridView.setVisibility(View.VISIBLE);
        } else {
            gridView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        initPopView();
        bindPopView(position);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(DensityUtil.SCREEN_WIDTH, DensityUtil.SCREEN_HEIGHT);
            popupWindow.setContentView(popView);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } else if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        int[] location = new int[2];
        tv_title.getLocationInWindow(location);
        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1]);
    }

    /**
     * 为医生详情界面绑定数据
     *
     * @param position 所点击条目在doctor集合中的索引
     */
    private void bindPopView(int position) {
        doctorBean = doctor.get(position);
        tv_name.setText(String.format("%s：%s", getString(R.string.name), doctorBean.doctor_user == null ? "" : doctorBean.doctor_user));
        try {
            String birth = doctorBean.doctor_age.substring(doctorBean.doctor_age.indexOf("(") + 1, doctorBean.doctor_age.indexOf(")"));
            tv_age.setText(String.format("%s：%s", getString(R.string.age),
                    new Date().getYear() - new Date(Long.parseLong(birth)).getYear()));
        } catch (Exception e) {
            tv_age.setText(getString(R.string.age));
        }
        tv_email.setText(String.format("%s：%s", getString(R.string.email), doctorBean.doctor_user == null ? "" : doctorBean.doctor_email));
        tv_remark.setText(String.format("%s：%s", getString(R.string.remark),
                doctorBean.remark == null ? "" : doctorBean.remark.replace("\n", "")));
        tv_sex.setText(String.format("%s：%s", getString(R.string.sex),
                doctorBean.doctor_sex == 1 ? getString(R.string.male) : getString(R.string.female)));
    }

    /**
     * 初始化医生详情的PopWindow
     */
    private void initPopView() {
        if (popView == null) {
            popView = UiUtils.inflate(R.layout.popwindow_doctor_detail);
            iv_avatar = (ImageView) popView.findViewById(R.id.iv_avatar);
            et_disease_description = (EditText) popView.findViewById(R.id.et_disease_description);
            bt_appoint = (Button) popView.findViewById(R.id.bt_appoint);
            bt_chat = (Button) popView.findViewById(R.id.bt_chat);
            bt_back = (Button) popView.findViewById(R.id.bt_back);

            bt_appoint.setOnClickListener(this);
            bt_chat.setOnClickListener(this);
            bt_back.setOnClickListener(this);

            tv_name = (TextView) popView.findViewById(R.id.tv_name);
            tv_sex = (TextView) popView.findViewById(R.id.tv_sex);
            tv_age = (TextView) popView.findViewById(R.id.tv_age);
            tv_email = (TextView) popView.findViewById(R.id.tv_email);
            tv_remark = (TextView) popView.findViewById(R.id.tv_remark);
        }
    }

    @Override
    protected void onDestroy() {
        dismissPopWindow();
        super.onDestroy();
    }

    /**
     * 让PopWindow消失，防止内存泄漏
     */
    private void dismissPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_appoint:
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int monthOfYear = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (datePickerDialog == null) {
                    datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            requestAppoint(String.valueOf(year) + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }, year, monthOfYear, dayOfMonth);
                    onDateChangedListener = new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int tempYear, int tempMonthOfYear, int tempDayOfMonth) {
                            Calendar tempCalendar = Calendar.getInstance();
                            tempCalendar.set(tempYear, tempMonthOfYear, tempDayOfMonth);
                            if (!tempCalendar.after(calendar)) {
                                view.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
                            }
                        }
                    };
                }
                datePickerDialog.getDatePicker().init(year, monthOfYear, dayOfMonth, onDateChangedListener);
                datePickerDialog.show();
                break;
            case R.id.bt_chat:
                MyToast.show(getString(R.string.chat_online));
                break;
            case R.id.bt_back:
                dismissPopWindow();
                break;
        }
    }

    /**
     * 提交预约申请
     */
    private void requestAppoint(String date) {
        String description = et_disease_description.getText().toString().trim();
        if (TextUtils.isEmpty(description)){
            MyToast.show("请填写病情描述");
            ObjectAnimator.ofFloat(et_disease_description,"translationX",-25,25,-25,25,0).setDuration(500).start();
        }else {
            MyToast.show("request:registration_time=" + date+",disease_description="+description);
        }
    }
}
