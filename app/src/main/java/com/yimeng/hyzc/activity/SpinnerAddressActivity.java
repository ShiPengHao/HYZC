package com.yimeng.hyzc.activity;

import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AddressBean;
import com.yimeng.hyzc.bean.DepartmentBean;
import com.yimeng.hyzc.bean.HospitalBean;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpinnerAddressActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
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

    private boolean isIniting;
    private Spinner spinner_province;
    private Spinner spinner_city;
    private Spinner spinner_area;
    private Spinner spinner_hospital;
    private Spinner spinner_department;
    private Spinner spinner_professional;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_booking;
    }


    @Override
    protected void initView() {
        spinner_province = (Spinner) findViewById(R.id.spinner_province);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_area = (Spinner) findViewById(R.id.spinner_area);
        spinner_hospital = (Spinner) findViewById(R.id.spinner_hospital);
        spinner_department = (Spinner) findViewById(R.id.spinner_department);
        spinner_professional = (Spinner) findViewById(R.id.spinner_professional);
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
    }

    @Override
    protected void initData() {
        isIniting = true;
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
                    spinner_province.setSelection(0);
                }
                break;
            case 1:// 更新市，并且手动请求对应区的数据
                city.clear();
                city.addAll(datas);
                cityAdapter.notifyDataSetChanged();
                if (city.size() > 0) {
                    spinner_city.setSelection(0);
                    values.clear();
                    values.put("citycode", city.get(0).code);
                    requestAddress("GetArea", values, 2);
                }
                break;
            case 2:// 更新区，并且请求对应医院的信息
                area.clear();
                area.addAll(datas);
                areaAdapter.notifyDataSetChanged();
                if (area.size() > 0) {
                    spinner_area.setSelection(0);
                    values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                    values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
                    values.put("area", area.get(0).code);// 二七区 410103
                    requestHospital("Load_Hospital", values);
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
                            values.clear();
                            values.put("hospital_id", hospital.get(0).hospital_id);
                            values.put("parentid", 0);
                            requestDepartment("Load_KS", values, 0);
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
                values.clear();
                values.put("hospital_id", department.get(0).hospital_id);
                values.put("parentid", department.get(0).departments_id);
                requestDepartment("Load_KS", values, 1);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
