package com.yimeng.hyzchbczhwq.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.DoctorBean;
import com.yimeng.hyzchbczhwq.bean.UserBean;
import com.yimeng.hyzchbczhwq.qrcode.EncodingHandler;
import com.yimeng.hyzchbczhwq.utils.BitmapUtils;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.PickImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 个人资料页面，医师和患者端共用
 */
public class AccountInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private ImageView iv_avatar;
    private EditText et_name;
    private EditText et_limit;
    private EditText et_age;
    private EditText et_phone;
    private EditText et_wechat;
    private EditText et_email;
    private EditText et_introduce;
    private TextView tv_sex;
    private TextView tv_isOrder;
    private TextView tv_edit;
    private LinearLayout ll_isOrder;
    private LinearLayout ll_limit;
    private LinearLayout ll_introduce;

    private boolean edit_status;
    private String avatarUrl;
    private final String[] genders = new String[]{"男", "女"};
    private final String[] isOrderStates = new String[]{"否", "是"};
    private AlertDialog.Builder selectSexDialog;
    private AlertDialog.Builder selectOrderDialog;
    private String type;
    private UserBean userBean;
    private static final int REQUEST_GALLERY_FOR_AVATAR = 101;
    private AlertDialog uploadDialog;
    private TextView uploadTextView;
    private HashMap<String, Object> values = new HashMap<>();
    private String limit;
    private DoctorBean doctorBean;
    private RatingBar rating_bar;
    private RelativeLayout rl_score;
    private String id;
    private TextView tv_change_pwd;
    private TextView tv_invite_code;
    private LinearLayout ll_personal_info;
    private String name;
    private String age;
    private String sex;
    private String phone;
    private AlertDialog showQRCodeDialog;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_account_info;
    }

    @Override
    protected void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_limit = (EditText) findViewById(R.id.et_limit);
        et_age = (EditText) findViewById(R.id.et_age);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_wechat = (EditText) findViewById(R.id.et_wechat);
        et_email = (EditText) findViewById(R.id.et_email);
        et_introduce = (EditText) findViewById(R.id.et_introduce);

        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_isOrder = (TextView) findViewById(R.id.tv_isOrder);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_change_pwd = (TextView) findViewById(R.id.tv_change_pwd);
        tv_invite_code = (TextView) findViewById(R.id.tv_invite_code);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);

        ll_isOrder = (LinearLayout) findViewById(R.id.ll_isOrder);
        ll_limit = (LinearLayout) findViewById(R.id.ll_limit);
        ll_introduce = (LinearLayout) findViewById(R.id.ll_introduce);
        rl_score = (RelativeLayout) findViewById(R.id.rl_score);
        ll_personal_info = (LinearLayout) findViewById(R.id.ll_personal_info);

        rating_bar = (RatingBar) findViewById(R.id.rating_bar);
    }

    @Override
    protected void setListener() {
        tv_edit.setOnClickListener(this);
        tv_sex.setOnClickListener(this);
        tv_isOrder.setOnClickListener(this);
        tv_invite_code.setOnClickListener(this);
        tv_change_pwd.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        SharedPreferences spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        id = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
        type = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(type)) {
            MyToast.show("账号异常，请重新登陆再试");
            finish();
            return;
        }
        values.clear();
        if (type.equalsIgnoreCase("patient")) {
            rl_score.setVisibility(View.GONE);
            ll_personal_info.setVisibility(View.GONE);
            values.put("user_id", id);
            requestInfo("Get_User_Msg", values);
        } else if (type.equalsIgnoreCase("doctor")) {
            ll_isOrder.setVisibility(View.VISIBLE);
            ll_introduce.setVisibility(View.VISIBLE);
            rl_score.setVisibility(View.VISIBLE);
            rl_score.setOnClickListener(this);
            values.put("doctor_id", id);
            requestInfo("Get_Doctor_Msg", values);
            requestCommentScore();
        }
    }

    /**
     * 根据医生id获取这个医生的满意度平均分
     */
    private void requestCommentScore() {
        values.clear();
        values.put("doctor_id", id);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    rating_bar.setRating(new JSONObject(s).optInt("AVG"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("get_doctor_AVG", values);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flushViewState();
    }

    /**
     * 请求个人信息
     *
     * @param params 方法名+参数键值对
     */
    private void requestInfo(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (null == s) {
                    return;
                }
                if (type.equalsIgnoreCase("patient")) {
                    parsePatientInfo(s);
                } else if (type.equalsIgnoreCase("doctor")) {
                    ll_isOrder.setVisibility(View.VISIBLE);
                    parseDoctorInfo(s);
                }
            }
        }.execute(params);
    }

    /**
     * 解析病人数据
     *
     * @param s json数据
     */
    private void parsePatientInfo(String s) {
        ArrayList<UserBean> userBeanArrayList = new ArrayList<>();
        parseListResult(userBeanArrayList, UserBean.class, s);
        if (userBeanArrayList.size() > 0) {
            userBean = userBeanArrayList.get(0);
            bindPatientData();
        }
    }

    /**
     * 绑定病人数据到控件
     */
    private void bindPatientData() {
        avatarUrl = userBean.user_avatar;
        et_wechat.setText(isEmpty(userBean.user_WeChat) ? getString(R.string.empty_content) : userBean.user_WeChat);
        et_email.setText(isEmpty(userBean.user_email) ? getString(R.string.empty_content) : userBean.user_email);
        tv_invite_code.setText(isEmpty(userBean.user_ICode) ? getString(R.string.empty_content) : userBean.user_ICode);
        Picasso.with(this)
                .load(MyConstant.NAMESPACE + avatarUrl)
                .resize(DensityUtil.dip2px(96), DensityUtil.dip2px(96))
                .error(R.mipmap.ic_launcher)
                .into(iv_avatar);
    }

    /**
     * 解析医生数据
     *
     * @param s json数据
     */
    private void parseDoctorInfo(String s) {
        ArrayList<DoctorBean> arrayList = new ArrayList<>();
        parseListResult(arrayList, DoctorBean.class, s);
        if (arrayList.size() > 0) {
            doctorBean = arrayList.get(0);
            bindDoctorData();
        }
    }

    /**
     * 绑定病人数据到控件
     */
    private void bindDoctorData() {
        avatarUrl = doctorBean.doctor_avatar;
        et_name.setText(doctorBean.doctor_name);
        et_age.setText(isEmpty(doctorBean.doctor_age) ? getString(R.string.empty_content) : doctorBean.doctor_age);
        et_wechat.setText(isEmpty(doctorBean.doctor_WeChat) ? getString(R.string.empty_content) : doctorBean.doctor_WeChat);
        et_phone.setText(isEmpty(doctorBean.doctor_phone) ? getString(R.string.empty_content) : doctorBean.doctor_phone);
        et_email.setText(isEmpty(doctorBean.doctor_email) ? getString(R.string.empty_content) : doctorBean.doctor_email);
        et_introduce.setText(isEmpty(doctorBean.remark) ? getString(R.string.empty_content) : doctorBean.remark);
        tv_sex.setText(isEmpty(doctorBean.doctor_sex) ? getString(R.string.empty_content) : doctorBean.doctor_sex);
        tv_invite_code.setText(isEmpty(doctorBean.doctor_ICode) ? getString(R.string.empty_content) : doctorBean.doctor_ICode);
        if (doctorBean.Is_Order == 0) {
            tv_isOrder.setText("否");
            ll_limit.setVisibility(View.GONE);
        } else {
            tv_isOrder.setText("是");
            ll_limit.setVisibility(View.VISIBLE);
            et_limit.setText(doctorBean.doctor_UCL == -1 ? "不限" : doctorBean.doctor_UCL + "");
        }
        Picasso.with(this)
                .load(MyConstant.NAMESPACE + avatarUrl)
                .resize(DensityUtil.dip2px(96), DensityUtil.dip2px(96))
                .error(R.mipmap.ic_launcher)
                .into(iv_avatar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_invite_code:
                String codeString = tv_invite_code.getText().toString().trim();
                if (isEmpty(codeString)
                        || getString(R.string.empty_content).equalsIgnoreCase(codeString)
                        ) {
                    MyToast.show(getString(R.string.invite_code_error));
                    return;
                }
                showQRCodePopWindow(codeString);
                break;
            case R.id.tv_sex:
                showSexSelectDialog();
                break;
            case R.id.tv_isOrder:
                showOrderSelectDialog();
                break;
            case R.id.tv_edit:
                if (edit_status) {
                    checkInput();
                } else {
                    switchEditState();
                }
                break;
            case R.id.tv_change_pwd:
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_avatar:
                PickImageUtils.getGalleryImage(this, REQUEST_GALLERY_FOR_AVATAR);
                break;
            case R.id.rl_score:
                startActivity(new Intent(this, DoctorScoreDetailActivity.class).putExtra("doctor", doctorBean));
                break;
        }
    }

    /**
     * 显示邀请码的二维码
     */
    private void showQRCodePopWindow(String codeString) {
        try {
            if (null == showQRCodeDialog) {
                // 根据邀请码字符串和头像信息，生成二维码图像
                Bitmap bitmap = EncodingHandler.createQRCode(codeString, DensityUtil.dip2px(144), BitmapUtils.drawableToBitmap(iv_avatar.getDrawable()));
                if (bitmap == null)
                    return;
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(DensityUtil.dip2px(144), DensityUtil.dip2px(144)));
                imageView.setImageBitmap(bitmap);
                showQRCodeDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.invite_code_my)).setView(imageView).create();
            }
            showQRCodeDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY_FOR_AVATAR:
                Bitmap bitmap = PickImageUtils.onActivityResult(data, this);
                if (null == bitmap) {
                    return;
                }
                iv_avatar.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, iv_avatar.getMeasuredWidth(), iv_avatar.getMeasuredHeight()));
                String avatarString = BitmapUtils.compressBitmap2Base64String(bitmap);
                if (null != avatarString) {
                    values.clear();
                    values.put("fileName", "1.jpg");
                    values.put("DelFilePath", avatarUrl);
                    values.put("image", avatarString);
                    uploadAvatar("upload_img", values);
                } else {
                    MyToast.show("出错了，请重新选择图片");
                }
                break;
        }
    }

    /**
     * 上传头像
     *
     * @param params 方法名+参数
     */
    private void uploadAvatar(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPreExecute() {
                if (uploadDialog == null) {
                    initUploadDialog();
                }
                uploadTextView.setText("正在上传，请稍后。。。");
                uploadDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null) {
                    try {
//                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(s);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            avatarUrl = object.optString("data");
                            uploadTextView.setText("上传成功!");
                        } else {
                            uploadTextView.setText("上传失败，请稍后重新选择图片再试!");
                        }

                        iv_avatar.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uploadDialog.dismiss();
                            }
                        }, 500);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        MyToast.show(getString(R.string.connect_error));
                        uploadDialog.dismiss();
                    }
                } else {
                    uploadDialog.dismiss();
                    MyToast.show(getString(R.string.connect_error));
                }
            }
        }.execute(params);
    }

    /**
     * 初始化提交信息进度对话框
     */
    private void initUploadDialog() {
        AlertDialog.Builder uploadImgBuilder = new AlertDialog.Builder(this);
        uploadTextView = new TextView(this);
        uploadTextView.setTextSize(18);
        uploadTextView.setTextColor(Color.BLACK);
        uploadTextView.setPadding(0, 10, 0, 0);
        uploadTextView.setGravity(Gravity.CENTER);
        uploadImgBuilder.setView(uploadTextView);
        uploadImgBuilder.setCancelable(false);
        uploadDialog = uploadImgBuilder.create();
        uploadDialog.setTitle("上传信息");
    }

    @Override
    protected void onDestroy() {
        if (null != uploadDialog && uploadDialog.isShowing()) {
            uploadDialog.dismiss();
        }
        if (null != showQRCodeDialog && showQRCodeDialog.isShowing()) {
            showQRCodeDialog.dismiss();
        }
        super.onDestroy();
    }

    /**
     * 显示选择性别对话框
     */
    private void showSexSelectDialog() {
        if (null == selectSexDialog) {
            selectSexDialog = new AlertDialog.Builder(this)
                    .setTitle("请选择性别")
                    .setSingleChoiceItems(genders, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            tv_sex.setText(genders[which]);
                        }
                    });
        }
        selectSexDialog.show();
    }

    /**
     * 显示选择医生是否坐诊对话框
     */
    private void showOrderSelectDialog() {
        if (null == selectOrderDialog) {
            selectOrderDialog = new AlertDialog.Builder(this)
                    .setTitle("请选择是否坐诊")
                    .setSingleChoiceItems(isOrderStates, 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            tv_isOrder.setText(isOrderStates[which]);
                            if (which == 0) {
                                ll_limit.setVisibility(View.GONE);
                            } else {
                                ll_limit.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
        selectOrderDialog.show();
    }

    /**
     * 检查可编辑项的文本，准备提交信息
     */
    private void checkInput() {
        if (ll_personal_info.getVisibility() == View.VISIBLE) {
            name = et_name.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                MyToast.show(String.format("%s%s", getString(R.string.name), getString(R.string.can_not_be_null)));
                ObjectAnimator.ofFloat(et_name, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
            age = et_age.getText().toString().trim();
            if (TextUtils.isEmpty(age)) {
                MyToast.show(String.format("%s%s", getString(R.string.age), getString(R.string.can_not_be_null)));
                ObjectAnimator.ofFloat(et_age, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
            sex = tv_sex.getText().toString().trim();
            if (TextUtils.isEmpty(sex)) {
                MyToast.show(String.format("%s%s", getString(R.string.sex), getString(R.string.can_not_be_null)));
                ObjectAnimator.ofFloat(tv_sex, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
            phone = et_phone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                MyToast.show(String.format("%s%s", getString(R.string.phone), getString(R.string.can_not_be_null)));
                ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
            if (!phone.matches("[1][358]\\d{9}")) {
                MyToast.show("手机号格式不正确");
                ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
        }
        String email = et_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            MyToast.show(String.format("%s%s", getString(R.string.email), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(et_email, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (!email.equalsIgnoreCase(getString(R.string.empty_content)) && !email.matches("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})")) {
            MyToast.show("邮箱格式不正确");
            ObjectAnimator.ofFloat(et_email, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        String weChat = et_wechat.getText().toString().trim();
        if (TextUtils.isEmpty(weChat)) {
            MyToast.show(String.format("%s%s", getString(R.string.wechat), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(et_wechat, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if (ll_isOrder.getVisibility() == View.VISIBLE) {
            String isOrder = tv_isOrder.getText().toString().trim();
            if (TextUtils.isEmpty(isOrder)) {
                MyToast.show(String.format("%s%s", getString(R.string.isOrder), getString(R.string.can_not_be_null)));
                ObjectAnimator.ofFloat(tv_isOrder, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
            if (isOrder.equalsIgnoreCase(isOrderStates[0])) {
                doctorBean.Is_Order = 0;
            } else {
                doctorBean.Is_Order = 1;
            }
        }
        if (ll_limit.getVisibility() == View.VISIBLE) {
            limit = et_limit.getText().toString().trim();
            if (TextUtils.isEmpty(limit)) {
                MyToast.show(String.format("%s%s", getString(R.string.appointment_limit), getString(R.string.can_not_be_null)));
                ObjectAnimator.ofFloat(et_limit, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
        }

        String remark = et_introduce.getText().toString().trim();

        values.clear();
        if (type.equalsIgnoreCase("patient")) {
            values.put("user_id", userBean.user_id);
            values.put("user_avatar", avatarUrl);
            values.put("user_email", email);
            values.put("user_WeChat", weChat);
            uploadInfo("Update_User_Msg", values);
        } else if (type.equalsIgnoreCase("doctor")) {
            values.put("doctor_id", doctorBean.doctor_id);
            values.put("doctor_avatar", avatarUrl);
            values.put("doctor_name", name);
            values.put("doctor_sex", sex);
            values.put("doctor_age", age);
            values.put("doctor_phone", phone);
            values.put("doctor_email", email);
            values.put("doctor_WeChat", weChat);
            values.put("remark", remark);
//            values.put("doctor_Audit", 1);//审核状态,1代表审核通过，这里必然为1
            try {
                limit = String.valueOf(Integer.parseInt(limit));
            } catch (Exception e) {
                limit = "-1";
            }
            values.put("doctor_UCL", limit);
            values.put("Is_Order", doctorBean.Is_Order);

            uploadInfo("Update_Doctor_Msg", values);
        }
    }

    /**
     * 上传个人信息
     *
     * @param params 方法名+参数
     */
    private void uploadInfo(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPreExecute() {
                if (uploadDialog == null) {
                    initUploadDialog();
                }
                uploadTextView.setText("正在上传，请稍后。。。");
                uploadDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null) {
                    try {
//                        new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(s);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            switchEditState();
                            uploadTextView.setText("上传成功!");
                        } else {
                            uploadTextView.setText("上传失败，请稍后再试!");
                        }

                        iv_avatar.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uploadDialog.dismiss();
                            }
                        }, 500);

                    } catch (Exception e) {
                        e.printStackTrace();
                        MyToast.show(getString(R.string.connect_error));
                        uploadDialog.dismiss();
                    }
                } else {
                    uploadDialog.dismiss();
                    MyToast.show(getString(R.string.connect_error));
                }
            }
        }.execute(params);
    }

    /**
     * 切换界面上需要改变状态的所有控件的状态
     */
    private void switchEditState() {
        if (edit_status) {
            tv_edit.setText(getString(R.string.edit));
        } else {
            tv_edit.setText(getString(R.string.finish));
        }
        edit_status = !edit_status;

        flushViewState();
    }

    /**
     * 刷新控件状态
     */
    private void flushViewState() {
        iv_avatar.setEnabled(edit_status);
        tv_isOrder.setEnabled(edit_status);
        tv_sex.setEnabled(edit_status);

        et_name.setFocusable(edit_status);
        et_name.setFocusableInTouchMode(edit_status);
        et_age.setFocusable(edit_status);
        et_age.setFocusableInTouchMode(edit_status);
        et_email.setFocusable(edit_status);
        et_email.setFocusableInTouchMode(edit_status);
        et_limit.setFocusable(edit_status);
        et_limit.setFocusableInTouchMode(edit_status);
        et_phone.setFocusable(edit_status);
        et_phone.setFocusableInTouchMode(edit_status);
        et_wechat.setFocusable(edit_status);
        et_wechat.setFocusableInTouchMode(edit_status);
        et_introduce.setFocusable(edit_status);
        et_introduce.setFocusableInTouchMode(edit_status);

        if (edit_status) {
            et_name.setSelection(et_name.getText().length());
        }
    }

}
