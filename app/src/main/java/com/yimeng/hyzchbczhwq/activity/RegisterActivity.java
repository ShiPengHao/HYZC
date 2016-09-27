package com.yimeng.hyzchbczhwq.activity;

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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.AddressBean;
import com.yimeng.hyzchbczhwq.utils.BitmapUtils;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyLog;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.ThreadUtils;
import com.yimeng.hyzchbczhwq.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册activity
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final int REQUEST_GALLERY_FOR_DOCTOR_CERT = 101;
    private static final int REQUEST_GALLERY_FOR_DOCTOR_SIGN = 102;
    private static final int REQUEST_GALLERY_FOR_PHARMACY_ORGANIZATION_CERT = 103;
    private static final int REQUEST_GALLERY_FOR_PHARMACY_LICENSE_CERT = 104;
    private static final int REQUEST_GALLERY_FOR_PHARMACY_PERMIT_CERT = 105;
    public static final int REQUEST_ADDRESS_CODE = 106;
    public static final int REQUEST_DEPARTMENT_CODE = 107;
    private static final int PHOTO_REQUEST_CUT = 2;
    private static final Object[] CHECK_NUMBERS = new Object[]{1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2};
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private EditText et_phone;
    private Button bt_register;
    private EditText et_id;
    private EditText et_address_detail;
    private RadioGroup rg_type;
    private Button bt_doctor_cert;
    private ImageView iv_doctor_cert;
    private LinearLayout ll_cert_info;// 证书信息，包含医生资格证和药店资格证
    private EditText et_name;
    private LinearLayout ll_address_detail;// 详细地址
    private Button bt_doctor_signature;
    private ImageView iv_doctor_signature;


    private Map<String, Object> values = new HashMap<>();


    private android.support.v7.app.AlertDialog uploadImageDialog;
    private String lastDoctorSignPath;
    private String lastDoctorCertPath;
    private String lastPharmacyOrganizationPath;
    private String lastPharmacyLicensePath;
    private String lastPharmacyPermitPath;
    private String user;
    private String pwd;
    private String name;
    private String identify;
    private String hospital_id;
    private String departments_id;
    private TextView uploadImgTextView;
    private LinearLayout ll_remark;
    private EditText et_remark;
    private LinearLayout ll_doctor_cert;// 医生资格证
    private LinearLayout ll_pharmacy_cert;// 药店资格证
    private Button bt_organization_cert;
    private ImageView iv_organization_cert;
    private Button bt_license_cert;
    private ImageView iv_license_cert;
    private Button bt_permit_cert;
    private ImageView iv_permit_cert;
    private EditText et_pharmacy_name;
    private EditText et_pharmacy_corporation;
    private LinearLayout ll_personal_info;// 身份证号
    private TextView tv_remark;
    private TextView tv_select_address;
    private CheckBox cb_work;
    private CheckBox cb_home;
    private CheckBox cb_farm;
    private ImageView iv_back;

    private AlertDialog okDialog;
    private AddressBean addressBean;
    private LinearLayout ll_select_address;
    private RadioGroup rg_doctor_title;
    private TextView tv_select_department;
    //    private JSONObject cityJsonObject;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_register;
    }

    protected void initView() {
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_name = (EditText) findViewById(R.id.et_name);
        et_id = (EditText) findViewById(R.id.et_id);
        et_address_detail = (EditText) findViewById(R.id.et_address_detail);
        et_remark = (EditText) findViewById(R.id.et_remark);
        et_pharmacy_name = (EditText) findViewById(R.id.et_pharmacy_name);
        et_pharmacy_corporation = (EditText) findViewById(R.id.et_pharmacy_corporation);

        rg_type = (RadioGroup) findViewById(R.id.rg_type);
        rg_doctor_title = (RadioGroup) findViewById(R.id.rg_doctor_title);
        cb_work = (CheckBox) findViewById(R.id.cb_work);
        cb_home = (CheckBox) findViewById(R.id.cb_home);
        cb_farm = (CheckBox) findViewById(R.id.cb_farm);

        bt_register = (Button) findViewById(R.id.bt_register);
        bt_doctor_cert = (Button) findViewById(R.id.bt_doctor_cert);
        bt_doctor_signature = (Button) findViewById(R.id.bt_doctor_signature);
        bt_organization_cert = (Button) findViewById(R.id.bt_organization_cert);
        bt_license_cert = (Button) findViewById(R.id.bt_license_cert);
        bt_permit_cert = (Button) findViewById(R.id.bt_permit_cert);

        iv_doctor_cert = (ImageView) findViewById(R.id.iv_doctor_cert);
        iv_doctor_signature = (ImageView) findViewById(R.id.iv_doctor_signature);
        iv_organization_cert = (ImageView) findViewById(R.id.iv_organization_cert);
        iv_license_cert = (ImageView) findViewById(R.id.iv_license_cert);
        iv_permit_cert = (ImageView) findViewById(R.id.iv_permit_cert);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        ll_cert_info = (LinearLayout) findViewById(R.id.ll_cert_info);
        ll_address_detail = (LinearLayout) findViewById(R.id.ll_address_detail);
        ll_remark = (LinearLayout) findViewById(R.id.ll_remark);
        ll_doctor_cert = (LinearLayout) findViewById(R.id.ll_doctor_cert);
        ll_pharmacy_cert = (LinearLayout) findViewById(R.id.ll_pharmacy_cert);
        ll_personal_info = (LinearLayout) findViewById(R.id.ll_personal_info);
        ll_select_address = (LinearLayout) findViewById(R.id.ll_select_address);

        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_select_address = (TextView) findViewById(R.id.tv_select_address);
        tv_select_department = (TextView) findViewById(R.id.tv_select_department);

        initUploadDialog();

    }

    private void initUploadDialog() {
        android.support.v7.app.AlertDialog.Builder uploadImgBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        uploadImgBuilder.setTitle("图片上传");
        uploadImgTextView = new TextView(this);
        uploadImgTextView.setTextSize(18);
        uploadImgTextView.setTextColor(Color.BLACK);
        uploadImgTextView.setPadding(0, 10, 0, 0);
        uploadImgTextView.setGravity(Gravity.CENTER);
        uploadImgBuilder.setView(uploadImgTextView);
        uploadImgBuilder.setCancelable(false);
        uploadImageDialog = uploadImgBuilder.create();
    }

    protected void setListener() {
        bt_register.setOnClickListener(this);
        bt_doctor_cert.setOnClickListener(this);
        bt_doctor_signature.setOnClickListener(this);
        bt_organization_cert.setOnClickListener(this);
        bt_license_cert.setOnClickListener(this);
        bt_permit_cert.setOnClickListener(this);
        ll_select_address.setOnClickListener(this);
        tv_select_department.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        rg_type.setOnCheckedChangeListener(this);
        rg_type.check(getIntent().getIntExtra("checkedId", R.id.rb_patient));
    }

    protected void initData() {
//        String cityString = "{'11':'北京','12':'天津','13':'河北','14':'山西','15':'内蒙古'," +
//                "'21':'辽宁','22':'吉林','23':'黑龙江','31':'上海','32':'江苏','33':'浙江'," +
//                "'34':'安徽','35':'福建','36':'江西','37':'山东','41':'河南','42':'湖北',"
//                + "'43':'湖南','44':'广东','45':'广西','46':'海南','50':'重庆','51':'四川'," +
//                "'52':'贵州','53':'云南','54':'西藏','61':'陕西','62':'甘肃','63':'青海'," +
//                "'64':'宁夏','65':'新疆','71':'台湾','81':'香港','82':'澳门','91':'国外'}";

//        try {
//            cityJsonObject = new JSONObject(cityString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        for (int i = 0; i < rg_doctor_title.getChildCount(); i++) {
            rg_doctor_title.getChildAt(i).setId(i);
        }
        rg_doctor_title.check(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
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
            case R.id.ll_select_address:
                startActivityForResult(new Intent(this, AddressChoiceActivity.class)
                        .putExtra(MyConstant.REQUEST_CODE, REQUEST_ADDRESS_CODE), REQUEST_ADDRESS_CODE);
                break;
            case R.id.tv_select_department:
                startActivityForResult(new Intent(this, AddressChoiceActivity.class)
                        .putExtra(MyConstant.REQUEST_CODE, REQUEST_DEPARTMENT_CODE), REQUEST_DEPARTMENT_CODE);
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
        if (requestCode == REQUEST_ADDRESS_CODE) {
            addressBean = (AddressBean) data.getSerializableExtra("address");
            tv_select_address.setText(data.getStringExtra("name"));
            return;
        }
        if (requestCode == REQUEST_DEPARTMENT_CODE) {
            hospital_id = data.getStringExtra("hospital_id");
            departments_id = data.getStringExtra("departments_id");
            tv_select_department.setText(String.format("%s%s",data.getStringExtra("hospital_name"),data.getStringExtra("departments_name")));
            return;
        }
        Uri uri = data.getData();
        Bitmap bitmap = null;
        // 从相册返回的数据，得到图片的全路径
        if (uri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                assert inputStream != null;
                inputStream.close();
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
        uploadImageDialog.show();
        uploadImgTextView.setText("正在上传，请稍后。。。");
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
                path = lastPharmacyOrganizationPath;
                iv = iv_organization_cert;
                break;
            case REQUEST_GALLERY_FOR_PHARMACY_LICENSE_CERT:
                path = lastPharmacyLicensePath;
                iv = iv_license_cert;
                break;
            case REQUEST_GALLERY_FOR_PHARMACY_PERMIT_CERT:
                path = lastPharmacyPermitPath;
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
        String imageStr = BitmapUtils.compressBitmap2Base64String(bitmap);
        bitmap.recycle();
        if (null != imageStr) {
            values.clear();
            values.put("fileName", "1.jpg");
            values.put("DelFilePath", path);
            values.put("image", imageStr);
            requestUploadImg("upload_img", values, requestCode);
        } else {
            MyToast.show("上传失败，请稍后重新选择图片再试!");
            uploadImageDialog.dismiss();
        }
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）+flag标志
     */
    public void requestUploadImg(final Object... params) {
        new SoapAsyncTask() {
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
                                        lastPharmacyOrganizationPath = object.optString("data");
                                        break;
                                    case REQUEST_GALLERY_FOR_PHARMACY_LICENSE_CERT:
                                        lastPharmacyLicensePath = object.optString("data");
                                        break;
                                    case REQUEST_GALLERY_FOR_PHARMACY_PERMIT_CERT:
                                        lastPharmacyPermitPath = object.optString("data");
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
                                uploadImageDialog.dismiss();
                            }
                        }, 500);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        MyToast.show(getString(R.string.connect_error));
                        uploadImageDialog.dismiss();
                    }
                } else {
                    uploadImageDialog.dismiss();
                    MyToast.show(getString(R.string.connect_error));
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
                if (lastPharmacyOrganizationPath == null) {
                    MyToast.show("请上传药店机构代码证");
                    return;
                }
                if (lastPharmacyLicensePath == null) {
                    MyToast.show("请上传药店营业执照");
                    return;
                }
                if (lastPharmacyPermitPath == null) {
                    MyToast.show("请上传药店经营许可证");
                    return;
                }

                if (null == addressBean) {
                    MyToast.show("地址不能为空");
                    return;
                }

                String detail = et_address_detail.getText().toString().trim();
                if (TextUtils.isEmpty(detail)) {
                    MyToast.show("详细地址不能为空");
                    return;
                }
                values.put("address", detail);
                values.put("phone", user);
                values.put("province", addressBean.provincecode);
                values.put("city", addressBean.citycode);
                values.put("area", addressBean.code);
                values.put("shopname", pharmacy_name);
                values.put("corporate", pharmacy_corporation);
                values.put("organization", lastPharmacyOrganizationPath);
                values.put("license", lastPharmacyLicensePath);
                values.put("businesspermit", lastPharmacyPermitPath);
                values.put("remark", et_remark.getText().toString().trim());// 备注无需校验
                StringBuilder flag = new StringBuilder();// 药店标志
                boolean hasChecked = false;
                if (cb_farm.isChecked()) {
                    flag.append(cb_farm.getText().toString().trim());
                    hasChecked = true;
                }
                if (cb_home.isChecked()) {
                    if (hasChecked) {
                        flag.append(",");
                    } else {
                        hasChecked = true;
                    }
                    flag.append(cb_home.getText().toString().trim());
                }
                if (cb_work.isChecked()) {
                    if (hasChecked) {
                        flag.append(",");
                    }
                    flag.append(cb_work.getText().toString().trim());
                }
                values.put("flag", flag.toString());
                method = "Shop_Register";
                break;
            case R.id.rb_doctor:
                if (lastDoctorCertPath == null) {
                    MyToast.show("请上传医生资格证");
                    ObjectAnimator.ofFloat(bt_doctor_cert, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                    return;
                }
                if (lastDoctorSignPath == null) {
                    MyToast.show("请上传医生电子签名");
                    ObjectAnimator.ofFloat(bt_doctor_signature, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                    return;
                }
                if (isEmpty(hospital_id) || isEmpty(departments_id)) {
                    MyToast.show("请选择执业医院");
                    ObjectAnimator.ofFloat(tv_select_department, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                    return;
                }
                values.put("qualification", lastDoctorCertPath);
                values.put("E_signature", lastDoctorSignPath);
                values.put("hospital_id", hospital_id);
                values.put("departments_id", departments_id);
                values.put("doctor_title", rg_doctor_title.getCheckedRadioButtonId());//职称类型
                values.put("remark", et_remark.getText().toString().trim());// 备注无需校验
                method = "Doctor_Register";
                break;
        }

        if (null != method) {
            register(method, values);
        }
    }

    /**
     * 把检查通过的通用信息放入map中，药店不需要身份证号，年龄，性别
     */
    private void mapGeneralInfo() {
        values.clear();
        values.put("pwd", pwd);
        values.put("user", user);

        if (rg_type.getCheckedRadioButtonId() != R.id.rb_pharmacy) {
            values.put("name", name);
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
        user = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            MyToast.show("手机号不能为空");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }
        if (!user.matches("[1][358]\\d{9}")) {
            MyToast.show("手机号格式不正确");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
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


        name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            MyToast.show("姓名不能为空");
            ObjectAnimator.ofFloat(et_name, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return true;
        }


        if (rg_type.getCheckedRadioButtonId() != R.id.rb_pharmacy) {
            identify = et_id.getText().toString().trim();
            if (TextUtils.isEmpty(identify)) {
                MyToast.show("身份证号不能为空");
                ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return true;
            }
            if ((!identify.matches("[0-9]{17}x") && !identify.matches("[0-9]{15}") && !identify.matches("[0-9]{18}"))// 校验位数
//                    || (cityJsonObject != null && isEmpty(cityJsonObject.optString(identify.substring(0, 2))))// 校验省级区号
                    ) {
                MyToast.show("身份证号格式不正确");
                ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return true;
            }

            if (!MyLog.DEBUG && identify.length() == 18) {// 校验校验码
                int sum = 0;
                for (int i = 0; i < identify.length() - 1; i++) {
                    sum += Integer.parseInt(identify.substring(i, i + 1)) * Math.pow(2, 17 - i);
                }
                if (!identify.substring(17, 18).equalsIgnoreCase(String.valueOf(CHECK_NUMBERS[sum % 11]))) {
                    MyToast.show("身份证号不正确");
                    ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                    return true;
                }
            }
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
                            bt_register.setEnabled(false);
                            showOkTips(object.optString("type"), object.optString("msg"));
                        } else {
                            MyToast.show(object.optString("msg"));
                        }
                    } catch (JSONException e) {
                        MyToast.show(getString(R.string.connect_error));
                        e.printStackTrace();
                    }
                }
            }

        }.execute(params);
    }

    /**
     * 显示注册成功的提示对话框
     */
    private void showOkTips(final String type, String msg) {
        if (type.length() == 0) {
            return;
        }
        if (null == okDialog) {
            okDialog = new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("注册成功！")
                    .setCancelable(true)
                    .create();
        }
        okDialog.setMessage("恭喜" + name + msg);
        okDialog.setButton(AlertDialog.BUTTON_POSITIVE, "知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.equalsIgnoreCase("patient")) {
                    setResult(100,
                            new Intent()
                                    .putExtra("username", user)
                                    .putExtra("pwd", pwd)
                                    .putExtra("type", rg_type.getCheckedRadioButtonId())
                    );
                    finish();
                } else {
                    okDialog.dismiss();
                }
            }
        });
        okDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (null != okDialog && okDialog.isShowing()) {
            okDialog.dismiss();
        }

        if (null != uploadImageDialog && uploadImageDialog.isShowing()) {
            uploadImageDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    /**
     * 用户选择注册类型的监听回调，用来控制不同内容的显示和隐藏
     */
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_type) {
            switch (checkedId) {
                case R.id.rb_patient:
                    ll_address_detail.setVisibility(View.GONE);
                    ll_cert_info.setVisibility(View.GONE);
                    ll_remark.setVisibility(View.GONE);
                    ll_personal_info.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_doctor:
                    tv_remark.setText(R.string.doctor_introduce);
                    ll_address_detail.setVisibility(View.GONE);
                    ll_pharmacy_cert.setVisibility(View.GONE);
                    ll_doctor_cert.setVisibility(View.VISIBLE);
                    ll_cert_info.setVisibility(View.VISIBLE);
                    ll_remark.setVisibility(View.VISIBLE);
                    ll_personal_info.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_pharmacy:
                    tv_remark.setText(R.string.pharmacy_introduce);
                    ll_doctor_cert.setVisibility(View.GONE);
                    ll_address_detail.setVisibility(View.VISIBLE);
                    ll_pharmacy_cert.setVisibility(View.VISIBLE);
                    ll_cert_info.setVisibility(View.VISIBLE);
                    ll_remark.setVisibility(View.VISIBLE);
                    ll_personal_info.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
