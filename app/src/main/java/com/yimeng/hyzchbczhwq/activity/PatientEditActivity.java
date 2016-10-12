package com.yimeng.hyzchbczhwq.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.PatientBean;
import com.yimeng.hyzchbczhwq.utils.MyLog;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import org.json.JSONObject;

import java.util.HashMap;

import static com.yimeng.hyzchbczhwq.R.string.age;
import static com.yimeng.hyzchbczhwq.R.string.sex;


/**
 * 编辑病人信息界面
 */

public class PatientEditActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_back;
    private EditText et_name;
    private EditText et_age;
    private EditText et_phone;
    private EditText et_id;
    private TextView tv_sex;
    private TextView tv_title;
    private Button bt_submit;
    private String user_id;
    private PatientBean patientBean;
    private final String[] genders = new String[]{"男", "女"};
    private AlertDialog selectSexDialog;
    private String name;
    private String identify;
    private static final Object[] CHECK_NUMBERS = new Object[]{1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2};
    private String phone;
    private HashMap<String, Object> hashMap = new HashMap<>();
    ;
    private LinearLayout ll_sex_and_age;
    private String age;
    private String sex;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_patient_edit;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_name = (EditText) findViewById(R.id.et_name);
        et_age = (EditText) findViewById(R.id.et_age);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_id = (EditText) findViewById(R.id.et_id);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        ll_sex_and_age = (LinearLayout) findViewById(R.id.ll_sex_and_age);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        tv_sex.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            Intent intent = getIntent();
            user_id = intent.getStringExtra("user_id");
            patientBean = (PatientBean) intent.getSerializableExtra("patient");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user_id == null)//编辑病人信息
            bindPatientBean();
        else {//添加病人信息
            tv_title.setText(getString(R.string.add_patient));
            ll_sex_and_age.setVisibility(View.GONE);
        }
    }

    /**
     * 绑定病人数据到控件
     */
    private void bindPatientBean() {
        et_name.setText(patientBean.patient_name);
        et_age.setText(patientBean.patient_age);
        et_phone.setText(patientBean.patient_phone);
        et_id.setText(patientBean.patient_identification);
        tv_sex.setText(patientBean.patient_sex);
        et_name.setSelection(et_name.getText().toString().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_sex:
                showSexSelectDialog();
                break;
            case R.id.bt_submit:
                checkInput();
                break;
        }
    }

    /**
     * 检查输入信息
     */
    private void checkInput() {
        name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            MyToast.show(String.format("%s%s", getString(R.string.name), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(et_name, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        identify = et_id.getText().toString().trim();
        if (TextUtils.isEmpty(identify)) {
            MyToast.show("身份证号不能为空");
            ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        if ((!identify.matches("[0-9]{17}x") && !identify.matches("[0-9]{15}") && !identify.matches("[0-9]{18}"))// 校验位数
//                    || (cityJsonObject != null && isEmpty(cityJsonObject.optString(identify.substring(0, 2))))// 校验省级区号
                ) {
            MyToast.show("身份证号格式不正确");
            ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        if (!MyLog.DEBUG && identify.length() == 18) {// 校验校验码
            int sum = 0;
            for (int i = 0; i < identify.length() - 1; i++) {
                sum += Integer.parseInt(identify.substring(i, i + 1)) * Math.pow(2, 17 - i);
            }
            if (!identify.substring(17, 18).equalsIgnoreCase(String.valueOf(CHECK_NUMBERS[sum % 11]))) {
                MyToast.show("身份证号不正确");
                ObjectAnimator.ofFloat(et_id, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
                return;
            }
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
        hashMap.clear();
        hashMap.put("patient_name", name);
        hashMap.put("patient_identification", identify);
        hashMap.put("patient_phone", phone);
        if (user_id != null) {//添加病人
            hashMap.put("user_id", user_id);
            request("Patient_Add", hashMap);
        } else {//修改病人信息
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
            hashMap.put("patient_sex",sex);
            hashMap.put("patient_age",age);
            hashMap.put("patient_id",patientBean.patient_id);
            request("Patient_update", hashMap);
        }

    }

    /**
     * 发起网络请求，添加或者修改病人信息，成功后关闭页面
     *
     * @param params 方法名+参数hash列表
     */
    private void request(Object... params) {
        bt_submit.setEnabled(false);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        MyToast.show(object.optString("msg"));
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            setResult(100, new Intent());
                            finish();
                        }
                    } catch (Exception e) {
                        MyToast.show(getString(R.string.connect_error));
                        e.printStackTrace();
                    }
                } else {
                    MyToast.show(getString(R.string.connect_error));
                }
            }
        }.execute(params);
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
                    })
                    .create();
        }

        selectSexDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (null != selectSexDialog && selectSexDialog.isShowing()) {
            selectSexDialog.dismiss();
        }
        super.onDestroy();
    }
}
