package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AddressBean;
import com.yimeng.hyzc.utils.BitmapUtils;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_GALLERY_FOR_DOCTOR_CERT = 101;
    private static final int REQUEST_GALLERY_FOR_DOCTOR_SIGN = 102;
    private static final int PHOTO_REQUEST_CUT = 2;
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

    private Map<String, Object> values = new HashMap<>();
    private final String NAMESPACE = "http://192.168.0.108:888/";
    private final String WEB_SERVICE_URL = "http://192.168.0.108:888/API/ymOR_WebService.asmx";
    private ArrayList<AddressBean> province = new ArrayList<>();
    private ArrayAdapter<AddressBean> provinceAdapter;
    private ArrayList<AddressBean> city = new ArrayList<>();
    private ArrayAdapter<AddressBean> cityAdapter;
    private ArrayList<AddressBean> area = new ArrayList<>();
    private ArrayAdapter<AddressBean> areaAdapter;
    private boolean isIniting;
    private RadioGroup rg_type;
    private Button bt_doctor_cert;
    private ImageView iv_doctor_cert;
    private LinearLayout ll_cert_info;
    private EditText et_name;
    private String doctorCert;
    private LinearLayout ll_addressDetail;
    private Button bt_doctor_signature;
    private ImageView iv_doctor_signature;
    private android.support.v7.app.AlertDialog alertDialog;


    private String lastPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        isIniting = true;
        initView();
        setListener();
        initData();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_name = (EditText) findViewById(R.id.et_name);
        et_id = (EditText) findViewById(R.id.et_id);
        et_address_detail = (EditText) findViewById(R.id.et_address_detail);

        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        rg_type = (RadioGroup) findViewById(R.id.rg_type);

        spinner_province = (Spinner) findViewById(R.id.spinner_province);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_area = (Spinner) findViewById(R.id.spinner_area);

        bt_register = (Button) findViewById(R.id.bt_register);
        bt_doctor_cert = (Button) findViewById(R.id.bt_doctor_cert);
        bt_doctor_signature = (Button) findViewById(R.id.bt_doctor_signature);

        iv_doctor_cert = (ImageView) findViewById(R.id.iv_doctor_cert);
        iv_doctor_signature = (ImageView) findViewById(R.id.iv_doctor_signature);

        ll_cert_info = (LinearLayout) findViewById(R.id.ll_cert_info);
        ll_addressDetail = (LinearLayout) findViewById(R.id.ll_address_detail);


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("图片上传");
        builder.setCancelable(false);
        builder.setMessage("正在上传，请稍后。。。");
        alertDialog = builder.create();

    }

    private void setListener() {
        bt_register.setOnClickListener(this);
        bt_doctor_cert.setOnClickListener(this);
        bt_doctor_signature.setOnClickListener(this);

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

        rg_sex.check(R.id.rb_male);
        rg_type.setOnCheckedChangeListener(this);
        rg_type.check(getIntent().getIntExtra("checdId", R.id.rb_patient));
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
                    if (!isIniting) {
                        values.clear();
                        values.put("citycode", city.get(0).code);
                        requestAddress("GetArea", values, 2);
                    }
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
            case R.id.spinner_area:
                if (isIniting) {
                    isIniting = false;
                }
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
            case R.id.bt_doctor_cert:
                getGalleryImage(REQUEST_GALLERY_FOR_DOCTOR_CERT);
                break;
            case R.id.bt_doctor_signature:
                getGalleryImage(REQUEST_GALLERY_FOR_DOCTOR_SIGN);
                break;
        }
    }

    /**
     * 从手机图库中选择图片
     */
    private void getGalleryImage(int requestCode) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 照相
//        Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        Bitmap bitmap = null;
        // 从相册返回的数据，得到图片的全路径
        if (uri != null) {
            try {
//                InputStream inputStream = getContentResolver().openInputStream(uri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                bitmap = extras.getParcelable("data");
            }
        }
        if (bitmap == null) {
            return;
        }
        saveBitmap(requestCode, bitmap);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 将用户用图库选择的图片展示到对应的控件，或者上传到服务器，保存结果
     *
     * @param bitmap
     */
    private void saveBitmap(int requestCode, Bitmap bitmap) {
        ImageView iv = null;
        switch (requestCode) {
            case REQUEST_GALLERY_FOR_DOCTOR_CERT:
                iv = iv_doctor_cert;
                break;
            case REQUEST_GALLERY_FOR_DOCTOR_SIGN:
                iv = iv_doctor_signature;
                break;
        }
        if (iv == null) {
            return;
        }
        iv.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, iv.getWidth(), iv.getHeight()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes;
        int quality = 100;
        while (quality > 0) {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                    && (bytes = baos.toByteArray()).length < 2 * 1024 * 1024) {
                if (alertDialog != null) {
                    alertDialog.show();
                }
                values.clear();
                values.put("fileName", "1.jpg");
                values.put("DelFilePath", lastPath);
                values.put("image", Base64.encodeToString(bytes, Base64.DEFAULT));
//                request("upload_img", values, 1);//TODO 请求框架抽取
                break;
            }
            quality -= 10;
        }

        bitmap.recycle();
    }



    /*
     * 剪切图片
     */

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


//    /*
//     * 从相机获取
//     */
//    public void camera(View view) {
//        // 激活相机
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        // 判断存储卡是否可以用，可用进行存储
//        if (hasSdcard()) {
//            tempFile = new File(Environment.getExternalStorageDirectory(),
//                    PHOTO_FILE_NAME);
//            // 从文件中创建uri
//            Uri uri = Uri.fromFile(tempFile);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        }
//        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
//        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
//    }


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


        String name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            MyToast.show("姓名不能为空");
            ObjectAnimator.ofFloat(et_name, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
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
        values.clear();
        values.put("user", username);
        values.put("pwd", pwd);
        values.put("email", email);
        values.put("phone", phone);
        values.put("name", name);
        int sexCode = 0;
        if (rg_sex.getCheckedRadioButtonId() == R.id.rb_male) {
            sexCode = 1;
        }
        values.put("sex", sexCode);
        StringBuilder sbBirth = new StringBuilder();
        sbBirth.append(id.substring(6, 10)).append("-")
                .append(id.substring(10, 12)).append("-")
                .append(id.substring(12, 14));
        values.put("age", sbBirth.toString());
        values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
        values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
        values.put("area", area.get(spinner_area.getSelectedItemPosition()).code);
        values.put("address", detail);
        values.put("identification", id);
        switch (rg_type.getCheckedRadioButtonId()) {
            case R.id.rb_patient:
                register("Patient_Register", values, 1);
                break;
            case R.id.rb_pharmacy:
                register("Shop_Register", values, 2);
                break;
            case R.id.rb_doctor:
                register("Doctor_Register", values, 3);
                break;
        }

    }

    /**
     * 注册
     */
    private void register(final Object... params) {
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
                    try {
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            showOkTips();
                        } else {
                            MyToast.show(object.optString("msg"));
                        }
                    } catch (JSONException e) {
                        MyToast.show(getString(R.string.connet_error));
                        e.printStackTrace();
                    }
                }
            }

        }.execute(params);
    }

    private void showOkTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("注册成功！")
                .setMessage("呜哈哈")
                .setCancelable(true)
                .setPositiveButton("去登陆", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("先待会儿", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_type) {
            switch (checkedId) {
                case R.id.rb_patient:
                    ll_addressDetail.setVisibility(View.VISIBLE);
                    ll_cert_info.setVisibility(View.GONE);
                    break;
                case R.id.rb_doctor:
                    ll_addressDetail.setVisibility(View.GONE);
                    ll_cert_info.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_pharmacy:
                    ll_addressDetail.setVisibility(View.VISIBLE);
                    ll_cert_info.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
