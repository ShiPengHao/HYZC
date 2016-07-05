package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    private Button bt_upload_img;
    private String lastPath;
    private AlertDialog alertDialog;
    private Button bt_hospital;
    private Button bt_department;
    private Button bt_professional;
    private Button bt_spinner_test;
    private Button bt_pick_doctor;
    //http://192.168.0.108:888/API/ymOR_WebService.asmx?op=GetProvince


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("图片上传");
        builder.setCancelable(false);
        builder.setMessage("正在上传，请稍后。。。");
        alertDialog = builder.create();
    }

    @Override
    protected void onDestroy() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
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
        bt_upload_img = (Button) findViewById(R.id.bt_upload_img);
        bt_upload_img.setOnClickListener(this);
        bt_hospital = (Button) findViewById(R.id.bt_hospital);
        bt_hospital.setOnClickListener(this);
        bt_department = (Button) findViewById(R.id.bt_department);
        bt_department.setOnClickListener(this);
        bt_professional = (Button) findViewById(R.id.bt_professional);
        bt_professional.setOnClickListener(this);
        bt_pick_doctor = (Button) findViewById(R.id.bt_pick_doctor);
        bt_pick_doctor.setOnClickListener(this);
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
            case R.id.bt_upload_img:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b);
                if (bitmap == null) {
                    return;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes;
                int quality = 100;
                while (quality > 0) {
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)) {
                        if ((bytes = baos.toByteArray()).length > 2 * 1024 * 1024) {
                            quality -= 10;
                        } else {
                            if (alertDialog != null) {
                                alertDialog.show();
                            }
                            values.clear();
                            values.put("fileName", "1.jpg");
                            values.put("DelFilePath", lastPath);
                            values.put("image", Base64.encodeToString(bytes, Base64.DEFAULT));
                            request("upload_img", values, 1);
                            break;
                        }
                    }
                }
                bitmap.recycle();
                break;

            case R.id.bt_login:
                values.clear();
                values.put("user", "user001");
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
                values.put("age", "2012-2-15");
                values.put("province", "aa");
                values.put("city", "bb");
                values.put("area", "cc");
                values.put("address", "dfdf");
                values.put("identification", "410236196609155555");

                request("Patient_Register", values);
                break;
            case R.id.bt_hospital:
                values.clear();
                values.put("province", "410000");
                values.put("city", "410100");
                values.put("area", "410103");
                request("Load_Hospital", values);
                break;
            case R.id.bt_department:
                values.clear();
                values.put("hospital_id", "1");
                values.put("parentid", "0");
                request("Load_KS", values);
                break;
            case R.id.bt_professional:
                values.clear();
                values.put("hospital_id", "1");//北京
                values.put("parentid", "1");
                request("Load_KS", values);
                break;
            case R.id.bt_pick_doctor:
                values.clear();
                values.put("departments_id", 4);
                request("Load_Doctor", values);
                break;
        }
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void request(final Object... params) {
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
                if (result != null) {
                    tv.setText("服务器回复的信息 : " + result);
                    try {
//                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        if (params.length >= 3) {
                            if (alertDialog != null && alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                            lastPath = object.optString("data");
                        } else {
                            MyToast.show(object.optString("status"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(params);
    }

}
