package com.yimeng.hyzc.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;

/**
 * 医生主界面
 */
public class HomeDoctorActivity extends BaseActivity implements View.OnClickListener {

    private Button bt_appoint;
    private Button bt_personal;
    private Button bt_test;
    private long preTime;
    private Handler handler;
    private static final int WHAT_SHOW_TIP_DIALOG = 100;
    private static final int WHAT_DISMISS_TIP_DIALOG = 101;
    private AlertDialog alertDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_home;
    }

    @Override
    protected void initView() {
        bt_appoint = (Button)findViewById(R.id.bt_appoint);
        bt_personal = (Button)findViewById(R.id.bt_personal);
        bt_test = (Button)findViewById(R.id.bt_test);
    }

    @Override
    protected void setListener() {
        bt_appoint.setOnClickListener(this);
        bt_personal.setOnClickListener(this);
        bt_test.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        SharedPreferences spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        if(spAccount.getBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING,true)) {
            dealFirstRunningTip();
            spAccount.edit().putBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, false).apply();// 更新首次运行标志
        }
    }

    /**
     * 首次运行弹框提示
     */
    private void dealFirstRunningTip() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case WHAT_SHOW_TIP_DIALOG:
                        showTipDialog();
                        handler.sendEmptyMessageDelayed(WHAT_DISMISS_TIP_DIALOG,5000);
                        break;
                    case WHAT_DISMISS_TIP_DIALOG:
                        dismissTipDialog();
                }
            }
        };
        handler.sendEmptyMessageDelayed(WHAT_SHOW_TIP_DIALOG,500);
    }

    /**
     * 显示首次运行提示对话框
     */
    private void showTipDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        alertDialog = builder.setTitle("欢迎使用华医之春互联网医院")
                .setMessage("为了节省您的流量，首次运行应用时需要缓存部分必须数据，可能造成微小卡顿，请耐心等待几秒钟即可")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissTipDialog();
                    }
                })
                .create();
        alertDialog.show();
    }

    /**
     * 隐藏对话框
     */
    private void dismissTipDialog() {
        if (null != handler) {
            handler.removeMessages(WHAT_DISMISS_TIP_DIALOG);
        }
        if (null != alertDialog && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissTipDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_appoint:
                startActivity(new Intent(this,AppointHistoryActivity.class));
                break;
            case R.id.bt_personal:
                startActivity(new Intent(this,AccountInfoActivity.class));
                break;
            case R.id.bt_test:
                startActivity(new Intent(this,TestActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:// 按两次返回键退出应用
                long now = SystemClock.uptimeMillis();
                if (preTime == -1 || (now - preTime) > 2000) {
                    preTime = now;
                    MyToast.show(getString(R.string.once_more_to_quit));
                } else {
                    MyToast.close();
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
