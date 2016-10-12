package com.yimeng.hyzchbczhwq.activity;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.utils.KeyBoardUtils;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 修改密码界面
 */

public class ChangePwdActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private EditText et_old_pwd;
    private Button bt_submit;
    private EditText et_new_pwd;
    private String id;
    private String type;

    private HashMap<String, Object> values = new HashMap<>();
    private SharedPreferences spAccount;
    private CheckBox cb_eye;
    private String newPwd;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_change_pwd;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        cb_eye = (CheckBox) findViewById(R.id.cb_eye);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        cb_eye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    et_old_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    et_old_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_old_pwd.setSelection(et_old_pwd.getText().toString().length());
            }
        });
    }

    @Override
    protected void initData() {
        spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        id = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
        type = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
        String readPwd = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, "");
        et_old_pwd.setText(readPwd);
        et_old_pwd.setSelection(readPwd.length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_submit:
                submit();
                break;
        }
    }

    /**
     * 提交
     */
    private void submit() {
        bt_submit.setEnabled(false);
        KeyBoardUtils.closeKeybord(et_new_pwd,this);
        String oldPwd = et_old_pwd.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            MyToast.show(String.format("%s%s", getString(R.string.old_pwd), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(et_old_pwd, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }
        newPwd = et_new_pwd.getText().toString();
        if (TextUtils.isEmpty(newPwd)) {
            MyToast.show(String.format("%s%s", getString(R.string.new_pwd), getString(R.string.can_not_be_null)));
            ObjectAnimator.ofFloat(et_new_pwd, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }

        if (oldPwd.equals(newPwd)) {
            MyToast.show(getString(R.string.pwd_new_equal_old_error));
            ObjectAnimator.ofFloat(et_new_pwd, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }
        values.clear();
        values.put("oldPwd", oldPwd);
        values.put("newPwd", newPwd);

        if (type.equalsIgnoreCase("patient")) {
            values.put("user_id", id);
            request("Update_User_PWD", values);
        } else if (type.equalsIgnoreCase("doctor")) {
            values.put("doctor_id", id);
            request("Update_Doctor_PWD", values);
        }
    }

    private void request(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                bt_submit.setEnabled(true);
                try {
                    JSONObject object = new JSONObject(s);
                    MyToast.show(object.optString("msg"));
                    if ("ok".equalsIgnoreCase(object.optString("status"))) {
                        spAccount.edit().putString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, newPwd).apply();
                        finish();
                    }
                } catch (Exception e) {
                    MyToast.show(getString(R.string.connect_error));
                    e.printStackTrace();
                }
            }
        }.execute(params);
    }


}
