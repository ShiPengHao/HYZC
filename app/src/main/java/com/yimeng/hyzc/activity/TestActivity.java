package com.yimeng.hyzc.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv;
    private Button bt_city;
    private Button bt_area;
    private Button bt_province;
    private Map<String, Object> values = new HashMap<>();
    private final String NAMESPACE = "http://192.168.0.108:888/";
    private final String WEB_SERVICE_URL = "http://192.168.0.108:888/API/ymOR_WebService.asmx";
    private Button bt_login;
    private Button bt_register;
    //http://192.168.0.108:888/API/ymOR_WebService.asmx?op=GetProvince


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
        bt_city = (Button) findViewById(R.id.bt_city);
        bt_city.setOnClickListener(this);
        bt_area = (Button) findViewById(R.id.bt_area);
        bt_area.setOnClickListener(this);
        bt_province = (Button) findViewById(R.id.bt_province);
        bt_province.setOnClickListener(this);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_province:
                request("GetProvince");
                break;
            case R.id.bt_city:
                values.clear();
                values.put("provincecode", "410000");//河南
                request("GetCity", values);
                break;
            case R.id.bt_area:
                values.clear();
                values.put("citycode", "410100");//郑州
                request("GetArea", values);
                break;

            case R.id.bt_login:
                values.clear();
                values.put("user", "zhaoziyu");
                values.put("pwd", "123456");
                request("Patient_Login", values);
                break;
            case R.id.bt_register:
                values.clear();
                values.put("user", "zhaoziy");
                values.put("pwd", "123456");
                values.put("email", "aaa@34.df");

                values.put("phone", "13345678900");
                values.put("name", "123456");
                values.put("sex", 1);
                values.put("age", "11");
                values.put("province", "aa");
                values.put("city", "bb");
                values.put("area", "cc");
                values.put("address", "dfdf");
                values.put("identification", "410236555555555555");

                request("Patient_Register", values);
                break;
        }
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void request(Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
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
                if (result != null) {
                    tv.setText("服务器回复的信息 : " + result);
                    try {
                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        MyToast.show(new JSONObject(result).optString("status"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(params);
    }

}
