package com.yimeng.hyzc.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AddressBean;
import com.yimeng.hyzc.bean.DepartmentBean;
import com.yimeng.hyzc.bean.HospitalBean;
import com.yimeng.hyzc.utils.BitmapUtils;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.ThreadUtils;
import com.yimeng.hyzc.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_GALLERY_FOR_DOCTOR_CERT = 101;
    private static final int REQUEST_GALLERY_FOR_DOCTOR_SIGN = 102;
    private static final int REQUEST_GALLERY_FOR_PHARMACY_ORGANIZATION_CERT = 103;
    private static final int REQUEST_GALLERY_FOR_PHARMACY_LICENSE_CERT = 104;
    private static final int REQUEST_GALLERY_FOR_PHARMACY_PERMIT_CERT = 105;
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
    private RadioGroup rg_type;
    private Button bt_doctor_cert;
    private ImageView iv_doctor_cert;
    private LinearLayout ll_cert_info;
    private EditText et_name;
    private LinearLayout ll_addressDetail;
    private Button bt_doctor_signature;
    private ImageView iv_doctor_signature;


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

    private android.support.v7.app.AlertDialog alertDialog;
    private android.support.v7.app.AlertDialog.Builder uploadImgBuilder;
    private String lastDoctorSignPath;
    private String lastDoctorCertPath;
    private String lastPharmaryOrganizationPath;
    private String lastPharmaryLicensePath;
    private String lastPharmaryPermitPath;
    private String username;
    private String pwd;
    private String email;
    private String name;
    private String phone;
    private String identify;
    private String sbBirth;
    private TextView uploadImgTextView;
    private LinearLayout ll_remark;
    private EditText et_remark;
    private Spinner spinner_hospital;
    private Spinner spinner_department;
    private Spinner spinner_professional;
    private LinearLayout ll_hospital;
    private LinearLayout ll_doctor_cert;
    private LinearLayout ll_pharmacy_cert;
    private Button bt_organization_cert;
    private ImageView iv_organization_cert;
    private Button bt_license_cert;
    private ImageView iv_license_cert;
    private Button bt_permit_cert;
    private ImageView iv_permit_cert;
    private EditText et_pharmacy_name;
    private EditText et_pharmacy_corporation;
    private LinearLayout ll_personal_info;
    private Button bt_float_register;
    private TextView tv_remark;
    private CheckBox cb_work;
    private CheckBox cb_home;
    private CheckBox cb_farm;
    private ImageView iv_back;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_register;
    }

    protected void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_name = (EditText) findViewById(R.id.et_name);
        et_id = (EditText) findViewById(R.id.et_id);
        et_address_detail = (EditText) findViewById(R.id.et_address_detail);
        et_remark = (EditText) findViewById(R.id.et_remark);
        et_pharmacy_name = (EditText) findViewById(R.id.et_pharmacy_name);
        et_pharmacy_corporation = (EditText) findViewById(R.id.et_pharmacy_corporation);

        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        rg_type = (RadioGroup) findViewById(R.id.rg_type);
        cb_work = (CheckBox) findViewById(R.id.cb_work);
        cb_home = (CheckBox) findViewById(R.id.cb_home);
        cb_farm = (CheckBox) findViewById(R.id.cb_farm);

        spinner_province = (Spinner) findViewById(R.id.spinner_province);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        spinner_area = (Spinner) findViewById(R.id.spinner_area);
        spinner_hospital = (Spinner) findViewById(R.id.spinner_hospital);
        spinner_department = (Spinner) findViewById(R.id.spinner_department);
        spinner_professional = (Spinner) findViewById(R.id.spinner_professional);

        bt_register = (Button) findViewById(R.id.bt_register);
        bt_doctor_cert = (Button) findViewById(R.id.bt_doctor_cert);
        bt_doctor_signature = (Button) findViewById(R.id.bt_doctor_signature);
        bt_organization_cert = (Button) findViewById(R.id.bt_organization_cert);
        bt_license_cert = (Button) findViewById(R.id.bt_license_cert);
        bt_permit_cert = (Button) findViewById(R.id.bt_permit_cert);
        bt_float_register = (Button) findViewById(R.id.bt_float_register);

        iv_doctor_cert = (ImageView) findViewById(R.id.iv_doctor_cert);
        iv_doctor_signature = (ImageView) findViewById(R.id.iv_doctor_signature);
        iv_organization_cert = (ImageView) findViewById(R.id.iv_organization_cert);
        iv_license_cert = (ImageView) findViewById(R.id.iv_license_cert);
        iv_permit_cert = (ImageView) findViewById(R.id.iv_permit_cert);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        ll_cert_info = (LinearLayout) findViewById(R.id.ll_cert_info);
        ll_addressDetail = (LinearLayout) findViewById(R.id.ll_address_detail);
        ll_remark = (LinearLayout) findViewById(R.id.ll_remark);
        ll_hospital = (LinearLayout) findViewById(R.id.ll_hospital);
        ll_doctor_cert = (LinearLayout) findViewById(R.id.ll_doctor_cert);
        ll_pharmacy_cert = (LinearLayout) findViewById(R.id.ll_pharmacy_cert);
        ll_personal_info = (LinearLayout) findViewById(R.id.ll_personal_info);

        tv_remark = (TextView) findViewById(R.id.tv_remark);


        uploadImgBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        uploadImgBuilder.setTitle("图片上传");
        uploadImgTextView = new TextView(this);
        uploadImgTextView.setTextSize(18);
        uploadImgTextView.setTextColor(Color.BLACK);
        uploadImgTextView.setPadding(20, 10, 0, 0);
        uploadImgTextView.setGravity(Gravity.CENTER);
        uploadImgBuilder.setView(uploadImgTextView);
        uploadImgBuilder.setCancelable(false);
        alertDialog = uploadImgBuilder.create();

    }

    protected void setListener() {
        bt_register.setOnClickListener(this);
        bt_doctor_cert.setOnClickListener(this);
        bt_doctor_signature.setOnClickListener(this);
        bt_organization_cert.setOnClickListener(this);
        bt_license_cert.setOnClickListener(this);
        bt_permit_cert.setOnClickListener(this);
        bt_float_register.setOnClickListener(this);
        iv_back.setOnClickListener(this);


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

        rg_sex.check(R.id.rb_male);
        rg_type.setOnCheckedChangeListener(this);
        rg_type.check(getIntent().getIntExtra("checdId", R.id.rb_patient));
    }

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
                    int i = province.size() - 1;
                    while (i >= 0) {//默认选择河南省
                        if ("410000".equalsIgnoreCase(province.get(i).code)) {
                            break;
                        }
                        i--;
                    }
                    spinner_province.setSelection(i);
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
                    if (ll_hospital.getVisibility() == View.VISIBLE && !isIniting) {
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
                if (ll_hospital.getVisibility() == View.VISIBLE) {
                    bean = area.get(position);
                    values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                    values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
                    values.put("area", bean.code);// 二七区 410103
                    requestHospital("Load_Hospital", values);
                } else if (isIniting) {
                    isIniting = false;
                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
            case R.id.bt_float_register:
                checkInfo();
                break;
            case R.id.bt_doctor_cert:
                getGalleryImage(REQUEST_GALLERY_FOR_DOCTOR_CERT);
                break;
            case R.id.bt_doctor_signature:
                getGalleryImage(REQUEST_GALLERY_FOR_DOCTOR_SIGN);
                break;
            case R.id.bt_organization_cert:
                getGalleryImage(REQUEST_GALLERY_FOR_PHARMACY_ORGANIZATION_CERT);
                break;
            case R.id.bt_license_cert:
                getGalleryImage(REQUEST_GALLERY_FOR_PHARMACY_LICENSE_CERT);
                break;
            case R.id.bt_permit_cert:
                getGalleryImage(REQUEST_GALLERY_FOR_PHARMACY_PERMIT_CERT);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 从手机图库中选择图片
     *
     * @param requestCode 请求码
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

    /**
     * 处理从相册返回的数据
     *
     * @param requestCode 请求码
     * @param resultCode  响应码
     * @param data        意图对象
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        Bitmap bitmap = null;
        // 从相册返回的数据，得到图片的全路径
        if (uri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
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
     * @param bitmap 图片对象
     */
    private void saveBitmap(final int requestCode, final Bitmap bitmap) {
        ImageView iv = null;
        String path = null;
        switch (requestCode) {
            case REQUEST_GALLERY_FOR_DOCTOR_CERT:
                path = lastDoctorCertPath;
                iv = iv_doctor_cert;
                break;
            case REQUEST_GALLERY_FOR_DOCTOR_SIGN:
                path = lastDoctorSignPath;
                iv = iv_doctor_signature;
                break;
            case REQUEST_GALLERY_FOR_PHARMACY_ORGANIZATION_CERT:
                path = lastPharmaryOrganizationPath;
                iv = iv_organization_cert;
                break;
            case REQUEST_GALLERY_FOR_PHARMACY_LICENSE_CERT:
                path = lastPharmaryLicensePath;
                iv = iv_license_cert;
                break;
            case REQUEST_GALLERY_FOR_PHARMACY_PERMIT_CERT:
                path = lastPharmaryPermitPath;
                iv = iv_permit_cert;
                break;
        }
        if (iv == null) {
            return;
        }
        iv.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, DensityUtil.SCREEN_WIDTH, DensityUtil.dip2px(150)));
        iv.setVisibility(View.VISIBLE);
        final String pathCopy = path;
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                uploadImg(requestCode, bitmap, pathCopy);
                bitmap.recycle();
            }
        });
    }

    /**
     * 上传图片
     *
     * @param requestCode 请求码
     * @param bitmap      图片对象
     * @param path        历史图片路径
     */
    private void uploadImg(int requestCode, Bitmap bitmap, String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes;
        int quality = 100;
        while (quality > 0) {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                    && (bytes = baos.toByteArray()).length < 2 * 1024 * 1024) {

                if (alertDialog != null) {
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.show();
                            uploadImgTextView.setText("正在上传，请稍后。。。");
                        }
                    });
                }
                values.clear();
                values.put("fileName", "1.jpg");
                values.put("DelFilePath", path);
                values.put("image", Base64.encodeToString(bytes, Base64.DEFAULT));
                requestUploadImg("upload_img", values, requestCode);
                break;
            }
            quality -= 10;
        }
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）+flag标志
     */
    public void requestUploadImg(final Object... params) {
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
//                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            if (params != null && params.length == 3) {
                                int requestCode = (int) params[2];
                                switch (requestCode) {
                                    case REQUEST_GALLERY_FOR_DOCTOR_CERT:
                                        lastDoctorCertPath = object.optString("data");
                                        break;
                                    case REQUEST_GALLERY_FOR_DOCTOR_SIGN:
                                        lastDoctorSignPath = object.optString("data");
                                        break;
                                    case REQUEST_GALLERY_FOR_PHARMACY_ORGANIZATION_CERT:
                                        lastPharmaryOrganizationPath = object.optString("data");
                                        break;
                                    case REQUEST_GALLERY_FOR_PHARMACY_LICENSE_CERT:
                                        lastPharmaryLicensePath = object.optString("data");
                                        break;
                                    case REQUEST_GALLERY_FOR_PHARMACY_PERMIT_CERT:
                                        lastPharmaryPermitPath = object.optString("data");
                                        break;
                                }
                            }
                            uploadImgTextView.setText("上传成功!");
                        } else {
                            uploadImgTextView.setText("上传失败，请稍后重新选择图片再试!");
                        }

                        iv_doctor_cert.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(params);
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
        intent.putExtra("back-data", true);
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
    private void checkInfo() {

        if (checkGeneralInfoError()) return;
        String method = null;
        switch (rg_type.getCheckedRadioButtonId()) {
            case R.id.rb_patient:
                String detail = et_address_detail.getText().toString().trim();
                if (TextUtils.isEmpty(detail)) {
                    MyToast.show("详细地址不能为空");
                    return;
                }
                values.put("address", detail);
                values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
                values.put("area", area.get(spinner_area.getSelectedItemPosition()).code);

                method = "Patient_Register";
                break;
            case R.id.rb_pharmacy:
                String pharmacy_name = et_pharmacy_name.getText().toString().trim();
                if (TextUtils.isEmpty(pharmacy_name)) {
                    MyToast.show("药店名称不能为空");
                    return;
                }
                String pharmacy_corporation = et_pharmacy_corporation.getText().toString().trim();
                if (TextUtils.isEmpty(pharmacy_corporation)) {
                    MyToast.show("药店法人不能为空");
                    return;
                }
                if (lastPharmaryOrganizationPath == null) {
                    MyToast.show("请上传药店机构代码证");
                    return;
                }
                if (lastPharmaryLicensePath == null) {
                    MyToast.show("请上传药店营业执照");
                    return;
                }
                if (lastPharmaryPermitPath == null) {
                    MyToast.show("请上传药店经营许可证");
                    return;
                }
                detail = et_address_detail.getText().toString().trim();
                if (TextUtils.isEmpty(detail)) {
                    MyToast.show("详细地址不能为空");
                    return;
                }
                values.put("address", detail);
                values.put("shopname", pharmacy_name);
                values.put("corporate", pharmacy_corporation);
                values.put("organization", lastPharmaryOrganizationPath);
                values.put("license", lastPharmaryLicensePath);
                values.put("businesspermit", lastPharmaryPermitPath);
                values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
                values.put("area", area.get(spinner_area.getSelectedItemPosition()).code);
                values.put("remark", et_remark.getText().toString().trim());// 备注无需校验
                StringBuilder sb = new StringBuilder();
                boolean hasChecked = false;
                if (cb_farm.isChecked()) {
                    sb.append(cb_farm.getText().toString().trim());
                    hasChecked = true;
                }
                if (cb_home.isChecked()) {
                    if (hasChecked) {
                        sb.append(",");
                    } else {
                        hasChecked = true;
                    }
                    sb.append(cb_home.getText().toString().trim());
                }
                if (cb_work.isChecked()) {
                    if (hasChecked) {
                        sb.append(",");
                    }
                    sb.append(cb_work.getText().toString().trim());
                }
                values.put("flag", sb.toString());
                method = "Shop_Register";
                break;
            case R.id.rb_doctor:
                if (lastDoctorCertPath == null) {
                    MyToast.show("请上传医生资格证");
                    return;
                }
                if (lastDoctorSignPath == null) {
                    MyToast.show("请上传医生电子签名");
                    return;
                }
                values.put("qualification", lastDoctorCertPath);
                values.put("E_signature", lastDoctorSignPath);
                values.put("remark", et_remark.getText().toString().trim());// 备注无需校验
                int departments_id = 0;
                if (professional.size() > 0) {
                    departments_id = professional.get(spinner_professional.getSelectedItemPosition()).departments_id;
                } else if (department.size() > 0) {
                    departments_id = department.get(spinner_department.getSelectedItemPosition()).departments_id;
                }
                values.put("departments_id", departments_id);
                method = "Doctor_Register";
                break;
        }

        if (method != null) {
            register(method, values);
        }
    }

    /**
     * 把检查通过的通用信息放入map中，药店不需要身份证号，年龄，性别
     */
    private void mapGeneralInfo() {
        values.clear();
        values.put("user", username);
        values.put("pwd", pwd);
        values.put("email", email);
        values.put("phone", phone);

        if (rg_type.getCheckedRadioButtonId() != R.id.rb_pharmacy) {
            values.put("CnName", name);
            int sexCode = 0;
            if (rg_sex.getCheckedRadioButtonId() == R.id.rb_male) {
                sexCode = 1;
            }
            values.put("sex", sexCode);
            values.put("age", sbBirth);
            values.put("identification", identify);
        } else {
            values.put("contacts", name);
        }
    }

    /**
     * 检查用户填写的通用资料信息
     *
     * @return 有错误提示用户并返回true，全部符合格式规范返回false
     */
    private boolean checkGeneralInfoError() {
        username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            MyToast.show("用户名不能为空");
            ObjectAnimator.ofFloat(et_username, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }

        pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            MyToast.show("密码不能为空");
            ObjectAnimator.ofFloat(et_pwd, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }
        if (pwd.length() < 6 || pwd.length() > 11) {
            MyToast.show("密码长度应在6-11位之间");
            ObjectAnimator.ofFloat(et_pwd, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }

        String confirm = et_pwd_confirm.getText().toString().trim();
        if (!pwd.equals(confirm)) {
            MyToast.show("两次密码不一致");
            ObjectAnimator.ofFloat(et_pwd_confirm, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }

        email = et_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            MyToast.show("邮箱不能为空");
            ObjectAnimator.ofFloat(et_email, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }
        if (!email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")) {
            MyToast.show("邮箱格式不正确");
            ObjectAnimator.ofFloat(et_email, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }


        name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            MyToast.show("姓名不能为空");
            ObjectAnimator.ofFloat(et_name, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }

        phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            MyToast.show("手机号不能为空");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }
        if (!phone.matches("[1][358]\\d{9}")) {
            MyToast.show("手机号格式不正确");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }

        if (rg_type.getCheckedRadioButtonId() != R.id.rb_pharmacy) {
            identify = et_id.getText().toString().trim();
            if (TextUtils.isEmpty(identify)) {
                MyToast.show("身份证号不能为空");
                ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return true;
            }
            if (!identify.matches("[0-9]{17}x") && !identify.matches("[0-9]{15}") && !identify.matches("[0-9]{18}")) {
                MyToast.show("身份证号格式不正确");
                ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return true;
            }
            sbBirth = identify.substring(6, 10) + "-" +
                    identify.substring(10, 12) + "-" +
                    identify.substring(12, 14);
        }


        mapGeneralInfo();
        return false;
    }

    /**
     * 注册
     */
    private void register(final Object... params) {
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

    /**
     * 显示注册成功的提示对话框
     */
    private void showOkTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("注册成功！")
                .setMessage("欢迎你：" + username + "，祝你健康！")
                .setCancelable(true)
                .setPositiveButton("开始体验", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("稍作镇定", null);
        builder.show();
    }

    @Override
    /**
     * 用户选择注册类型的监听回调，用来控制不同内容的显示和隐藏
     */
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_type) {
            switch (checkedId) {
                case R.id.rb_patient:
                    ll_addressDetail.setVisibility(View.VISIBLE);
                    ll_cert_info.setVisibility(View.GONE);
                    ll_remark.setVisibility(View.GONE);
                    ll_hospital.setVisibility(View.GONE);
                    ll_personal_info.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_doctor:
                    tv_remark.setText(R.string.doctor_introduce);
                    isIniting = true;
                    if (province.size() > 0 && city.size() > 0 && area.size() > 0) {
                        values.put("province", province.get(spinner_province.getSelectedItemPosition()).code);
                        values.put("city", city.get(spinner_city.getSelectedItemPosition()).code);
                        values.put("area", area.get(spinner_area.getSelectedItemPosition()).code);// 二七区 410103
                        requestHospital("Load_Hospital", values);
                    }
                    ll_addressDetail.setVisibility(View.GONE);
                    ll_pharmacy_cert.setVisibility(View.GONE);
                    ll_doctor_cert.setVisibility(View.VISIBLE);
                    ll_cert_info.setVisibility(View.VISIBLE);
                    ll_remark.setVisibility(View.VISIBLE);
                    ll_hospital.setVisibility(View.VISIBLE);
                    ll_personal_info.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_pharmacy:
                    tv_remark.setText(R.string.pharmacy_introduce);
                    ll_doctor_cert.setVisibility(View.GONE);
                    ll_addressDetail.setVisibility(View.VISIBLE);
                    ll_pharmacy_cert.setVisibility(View.VISIBLE);
                    ll_cert_info.setVisibility(View.VISIBLE);
                    ll_remark.setVisibility(View.VISIBLE);
                    ll_hospital.setVisibility(View.GONE);
                    ll_personal_info.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
