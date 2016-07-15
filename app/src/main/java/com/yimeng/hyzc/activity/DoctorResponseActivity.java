package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 医生回复界面
 */
public class DoctorResponseActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private TextView tv_prescribe;
    private Button bt_prescribe;
    private Button bt_response;
    private EditText et_doctor_response;
    private EditText et_doctor_way;
    private ListView listView;

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
        bt_response = (Button) findViewById(R.id.bt_response);

        et_doctor_response = (EditText) findViewById(R.id.et_doctor_response);
        et_doctor_way = (EditText) findViewById(R.id.et_doctor_way);
        listView = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_prescribe.setOnClickListener(this);
        bt_response.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_prescribe:
                MyToast.show("开药");
                break;
            case R.id.bt_response:
                requestResponse();
                break;
        }
    }

    /**
     * 提交医生回应
     */
    private void requestResponse() {
        String response = et_doctor_response.getText().toString().trim();
        if (TextUtils.isEmpty(response)) {
            MyToast.show(String.format("%s%s", getString(R.string.doctor_response), getString(R.string.can_not_be_null)));
            return;
        }
        String way = et_doctor_way.getText().toString().trim();
        if (TextUtils.isEmpty(way)) {
            MyToast.show(String.format("%s%s", getString(R.string.doctor_response_way), getString(R.string.can_not_be_null)));
            return;
        }
        int id = 0;
        try {
            id = getIntent().getIntExtra("id", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (id == 0) {
            MyToast.show("未知错误，请重新登陆应用再试");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("appointment_id", id);
        map.put("doctor_Responses", response);
        map.put("doctor_dispose", 1);
        map.put("doctor_Way", way);
        map.put("IsPrescribe", 0);//TODO 药方
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE,
                        (String) params[0], (HashMap<String, Object>) params[1]);
                if (null == result){
                    MyToast.show(getString(R.string.connet_error));
                }else {
                    try {
                        JSONObject object = new JSONObject(result);
                        MyToast.show(object.optString("msg"));
                        if ("ok".equalsIgnoreCase(object.optString("status"))){
                            setResult(100,new Intent());
                            SystemClock.sleep(500);
                            finish();
                        }
                    } catch (Exception e) {
                        MyToast.show(getString(R.string.connet_error));
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute("sub_Responses", map);
    }
}
