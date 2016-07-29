package com.yimeng.hyzc.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyLog;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;
import com.yimeng.hyzc.view.ClearEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText et_username;
    private EditText et_pwd;
    private CheckBox cb_remember;
    private CheckBox cb_auto;
    private Button bt_register;
    private Button bt_login;
    private SharedPreferences spAccount;
    private String username;
    private String pwd;
    private RadioGroup rg_userType;
    private Map<String, Object> values = new HashMap<>();
    private LinearLayout ll_loading;
    private android.os.Handler handler;
    private static final int WHAT_SHOW_LOADING = 1;
    private static final int WHAT_DISMISS_LOADING = 2;
    private TextView tv_location;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    protected void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        cb_remember = (CheckBox) findViewById(R.id.cb_remeber);
        cb_auto = (CheckBox) findViewById(R.id.cb_auto);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);
        rg_userType = (RadioGroup) findViewById(R.id.rg_type);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        tv_location = (TextView) findViewById(R.id.tv_location);
    }

    @Override
    protected void setListener() {
        cb_auto.setOnCheckedChangeListener(this);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        et_username.addTextChangedListener(new ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_pwd.setText("");
            }
        });
        handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_SHOW_LOADING:
                        ll_loading.setVisibility(View.VISIBLE);
                        break;
                    case WHAT_DISMISS_LOADING:
                        handler.removeMessages(WHAT_SHOW_LOADING);
                        ll_loading.setVisibility(View.GONE);
                        break;
                }
            }
        };
//        initLocationService();
    }

    private void initLocationService() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(provider, 2000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateWithNewLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }

    private void updateWithNewLocation(Location location) {
        new AsyncTask<Location, Void, String>() {
            @Override
            protected String doInBackground(Location... params) {
                if (null == params || null == params[0]) {
                    return "无法获取地理信息";
                }
                Location location = params[0];
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String locationStr = "维度：" + lat + "\n经度：" + lng;
                List<Address> addList;
                Geocoder ge = new Geocoder(MyApp.getAppContext());
                try {
                    addList = ge.getFromLocation(lat, lng, 1);
                    if (addList != null && addList.size() > 0) {
                        Address ad = addList.get(0);
                        locationStr += "\n地址：" + ad.getAddressLine(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return locationStr;
            }

            @Override
            protected void onPostExecute(String s) {
//                tv_location.setText(s);
            }
        }.execute(location);
    }


    @Override
    protected void initData() {
        rg_userType.check(R.id.rb_patient);
        ll_loading.setVisibility(View.GONE);
        spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        // 回显账号
        et_username.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, ""));
        et_username.setSelection(et_username.getText().length());
        // 回显密码
        et_pwd.setText(spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, ""));
        // 回显记住密码
        boolean isRemember = spAccount.getBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, false);
        cb_remember.setChecked(isRemember);

        if (isRemember) {//回显用户类型
            String type = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
            if (type.equalsIgnoreCase("patient")) {
                rg_userType.check(R.id.rb_patient);
            } else if (type.equalsIgnoreCase("doctor")) {
                rg_userType.check(R.id.rb_doctor);
            } else if (type.equalsIgnoreCase("shop")) {
                rg_userType.check(R.id.rb_pharmacy);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                goToRegister();
                break;
            case R.id.bt_login:
                login();
                break;
        }
    }

    /**
     * 去注册，意图对象携带选择的注册类型信息，默认是普通病人
     */
    private void goToRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class).putExtra("checkedId", rg_userType.getCheckedRadioButtonId()), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case 100:
                et_username.setText(data.getStringExtra("username"));
                et_pwd.setText(data.getStringExtra("pwd"));
                rg_userType.check(data.getIntExtra("type", R.id.rb_patient));
                break;
        }
    }

    /**
     * 登陆信息核对无误后登陆
     */
    private void login() {
        username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            MyToast.show("用户名不能为空");
            ObjectAnimator.ofFloat(et_username, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            MyToast.show("密码不能为空");
            ObjectAnimator.ofFloat(et_pwd, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        values.clear();
        values.put("user", username);
        values.put("pwd", pwd);
        values.put("hospital_id", MyConstant.HOSPITAL_ID);
        values.put("departments_id", MyConstant.DEPARTMENTS_ID);
        switch (rg_userType.getCheckedRadioButtonId()) {
            case R.id.rb_patient:
                requestLogin("Patient_Login", values);
                break;
            case R.id.rb_doctor:
                requestLogin("Doctor_Login", values);
                break;
            case R.id.rb_pharmacy:
                requestLogin("Shop_Login", values);
                break;
        }

    }

    /**
     * 执行异步任务，登录
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void requestLogin(Object... params) {
        handler.sendEmptyMessageDelayed(WHAT_SHOW_LOADING, 500);
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                    if (null == result) {
                        MyToast.show(getString(R.string.connect_error));
                        dismissLoginDialog();
                        return null;
                    }
                    try {
//                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            String type = object.optString("type");
                            String id = object.optString("id");
                            setJPushAliasAndTag(type, id);
                        } else {
                            MyToast.show(object.optString("msg"));
                            dismissLoginDialog();
                        }
                    } catch (Exception e) {
                        MyToast.show(getString(R.string.connect_error));
                        dismissLoginDialog();
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute(params);
    }

    /**
     * 让登陆加载对话框消失
     */
    private void dismissLoginDialog() {
        handler.sendEmptyMessage(WHAT_DISMISS_LOADING);
    }

    /**
     * 登陆成功后为本应用用户绑定JPush的别名和标签，别名为账号类型+"-"+id，标签为账号类型，设置成功以后缓存登陆信息，跳转到主页
     */
    private void setJPushAliasAndTag(final String type, final String id) {
        final HashSet<String> tags = new HashSet<>();
        tags.add(type);
        JPushInterface.setAliasAndTags(MyApp.getAppContext(), type + "+" + id, tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i != 0) {
                    MyLog.i("JPush", "set alias and tag error");
                    dismissLoginDialog();
                    MyToast.show(getString(R.string.connect_error));
                } else {
                    saveAccountInfo(type, id);
                }
            }
        });
    }

    /**
     * 保存登陆成功的账号信息到本地sp文件中
     */
    private void saveAccountInfo(String type, String id) {
        SharedPreferences.Editor editor = spAccount.edit();
        editor.putString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, username)
                .putBoolean(MyConstant.KEY_ACCOUNT_LAST_REMEMBER, cb_remember.isChecked())
                .putBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, cb_auto.isChecked())
                .putString(MyConstant.KEY_ACCOUNT_LAST_ID, id)
                .putString(MyConstant.KEY_ACCOUNT_LAST_TYPE, type);
        if (cb_remember.isChecked()) {
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, pwd);
        } else {
            editor.putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, "");
        }
        editor.apply();
        goToHome(type);
    }

    /**
     * 跳转到主页，根据账号类型判断
     */
    private void goToHome(String type) {
        finish();
        if (type.equalsIgnoreCase("patient")) {
            startActivity(new Intent(this, HomePatientActivity.class));
        } else if (type.equalsIgnoreCase("doctor")) {
            startActivity(new Intent(this, HomeDoctorActivity.class));
        } else if (type.equalsIgnoreCase("shop")) {
            startActivity(new Intent(this, HomePharmacyActivity.class));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == cb_auto.getId() && cb_auto.isChecked()) {
            cb_remember.setChecked(true);
        }
    }
}
