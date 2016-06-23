package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AddressBean;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText et_username;
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private EditText et_phone;
    private Button bt_register;
    private EditText et_email;
    private EditText et_id;
    private RadioGroup rg_sex;
    private Spinner spinner_province;
    private Spinner spinner_city;
    private Spinner spinner_area;
    private EditText et_address_detail;

    private Map<String, String> values = new HashMap<String, String>();
    private final String NAMESPACE = "http://192.168.0.108:888/";
    private final String WEB_SERVICE_URL = "http://192.168.0.108:888/API/ymOR_WebService.asmx";
    private ArrayList<AddressBean> province = new ArrayList<>();
    private ArrayAdapter<AddressBean> provinceAdapter;
    private ArrayList<AddressBean> city = new ArrayList<>();
    private ArrayAdapter<AddressBean> cityAdapter;
    private ArrayList<AddressBean> area = new ArrayList<>();
    private ArrayAdapter<AddressBean> areaAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setListener();
        initData();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.email);
        et_id = (EditText) findViewById(R.id.et_id);
        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        et_address_detail = (EditText) findViewById(R.id.et_address_detail);

        spinner_province = (Spinner) findViewById(R.id.spinner_province);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_area = (Spinner) findViewById(R.id.spinner_area);

        bt_register = (Button) findViewById(R.id.bt_register);
    }

    private void setListener() {
        bt_register.setOnClickListener(this);
        rg_sex.check(R.id.rb_male);
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
    }

    public void initData() {
        requestAddress("GetProvince");
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）+flag标志
     */
    public void requestAddress(final Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length >= 2) {
                    return WebServiceUtils.callWebService(WEB_SERVICE_URL, NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                } else if (params != null && params.length == 1) {
                    return WebServiceUtils.callWebService(WEB_SERVICE_URL, NAMESPACE, (String) params[0],
                            null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                int flag = 0;
                if (result != null) {
                    if (params != null && params.length == 3) {
                        flag = (int) params[2];
                    }
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
            switch (flag) {
                case 0:
                    province.clear();
                    province.addAll(datas);
                    provinceAdapter.notifyDataSetChanged();
                    spinner_province.setSelection(0);
                    break;
                case 1:
                    city.clear();
                    city.addAll(datas);
                    cityAdapter.notifyDataSetChanged();
                    spinner_city.setSelection(0);

                    values.clear();
                    values.put("citycode", city.get(0).code);
                    requestAddress("GetArea", values, 2);
                    break;
                case 2:
                    area.clear();
                    area.addAll(datas);
                    areaAdapter.notifyDataSetChanged();
                    spinner_area.setSelection(0);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        values.clear();
        AddressBean bean;
        switch (parent.getId()) {
            case R.id.spinner_province:
                bean = province.get(position);
                values.put("provincecode", bean.code);
                requestAddress("GetCity", values, 1);
                break;
            case R.id.spinner_city:
                bean = city.get(position);
                values.put("citycode", bean.code);
                requestAddress("GetArea", values, 2);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                submit();
                break;
        }
    }

    /**
     * 检查输入数据格式，全部正常后提交注册
     */
    private void submit() {

        String username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            MyToast.show("用户名不能为空");
            ObjectAnimator.ofFloat(et_username, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            MyToast.show("密码不能为空");
            ObjectAnimator.ofFloat(et_pwd, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (pwd.length() < 6 || pwd.length() > 11) {
            MyToast.show("密码长度应在6-11位之间");
            ObjectAnimator.ofFloat(et_pwd, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String confirm = et_pwd_confirm.getText().toString().trim();
        if (!pwd.equals(confirm)) {
            MyToast.show("两次密码不一致");
            ObjectAnimator.ofFloat(et_pwd_confirm, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String email = et_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            MyToast.show("邮箱不能为空");
            ObjectAnimator.ofFloat(et_email, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (!email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
            MyToast.show("邮箱格式不正确");
            ObjectAnimator.ofFloat(et_email, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }


        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            MyToast.show("手机号不能为空");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (!phone.matches("[1][358]\\d{9}")) {
            MyToast.show("手机号格式不正确");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String id = et_id.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            MyToast.show("身份证号不能为空");
            ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (!id.matches("[0-9]{17}x") && !id.matches("[0-9]{15}") && !id.matches("[0-9]{18}")) {
            MyToast.show("身份证号格式不正确");
            ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }


        String detail = et_address_detail.getText().toString().trim();
        if (TextUtils.isEmpty(detail)) {
            MyToast.show("详细地址不能为空");
            ObjectAnimator.ofFloat(et_address_detail, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        register();
    }

    /**
     * 注册
     */
    private void register(final Object... params) {
//        new AsyncTask<Object, Object, String>() {
//            @Override
//            protected String doInBackground(Object... params) {
//                if (params != null && params.length >= 2) {
//                    return WebServiceUtils.callWebService(WEB_SERVICE_URL, NAMESPACE, (String) params[0],
//                            (Map<String, Object>) params[1]);
//                } else if (params != null && params.length == 1) {
//                    return WebServiceUtils.callWebService(WEB_SERVICE_URL, NAMESPACE, (String) params[0],
//                            null);
//                } else {
//                    return null;
//                }
//            }
//
//            protected void onPostExecute(String result) {
//                int flag = 0;
//                if (result != null) {
//                    if (params != null && params.length == 3) {
//                        flag = (int) params[2];
//                    }
//                    parseAddressJson(result, flag);
//                }
//            }
//
//        }.execute(params);
    }


}
