package com.yimeng.hyzc.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimeng.hyzc.R;

/**
 * 个人资料页面
 */
public class AccountInfoActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_username;
    private Button bt_edit;
    private boolean status;
    private TextView tv_username;
    private ImageView iv_back;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_account_info;
    }

    @Override
    protected void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        tv_username = (TextView) findViewById(R.id.tv_username);
        bt_edit = (Button)findViewById(R.id.bt_edit);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    protected void setListener() {
        bt_edit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_edit:
                if (status){
                    et_username.setVisibility(View.INVISIBLE);
                    tv_username.setVisibility(View.VISIBLE);
                    tv_username.setText(et_username.getText());
                }else {
                    tv_username.setVisibility(View.INVISIBLE);
                    et_username.setVisibility(View.VISIBLE);
                    et_username.setText(tv_username.getText());
                    et_username.setSelection(et_username.getText().length());
                }

                status = !status;
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
