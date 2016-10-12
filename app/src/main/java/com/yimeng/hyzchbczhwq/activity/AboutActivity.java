package com.yimeng.hyzchbczhwq.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.util.TextFormater;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyNetUtils;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;


/**
 * 关于 界面
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private TextView tv_version;
    private LinearLayout ll_check_update;
    private LinearLayout ll_suggest;
    private int apkSize;
    private String downloadUrl;
    private AlertDialog updateDialog;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_version = (TextView) findViewById(R.id.tv_version);
        ll_check_update = (LinearLayout) findViewById(R.id.ll_check_update);
        ll_suggest = (LinearLayout) findViewById(R.id.ll_suggest);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        ll_check_update.setOnClickListener(this);
        ll_suggest.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            PackageManager pm = MyApp.getAppContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(MyApp.getAppContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null && pi.versionName != null) {
                tv_version.setText(pi.versionName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_suggest:
                startActivity(new Intent(this, SuggestActivity.class));
                break;
            case R.id.ll_check_update:
                checkUpdate();
                break;
        }
    }

    /**
     * 查询最新版本，比较版本号，如果有新版本，提示用户
     */
    private void checkUpdate() {
        final String packageName = getPackageName();
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_type", MyConstant.ANDROID);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    MyToast.show(getString(R.string.connect_error));
                    return;
                }
                try {
                    int localVersionCode = getPackageManager().getPackageInfo(packageName, 0).versionCode;
                    JSONObject object = new JSONObject(s).optJSONArray("data").optJSONObject(0);
                    if (object.optInt("version_Number") > localVersionCode) {
                        apkSize = object.optInt("version_Size");
                        downloadUrl = object.optString("version_Url");
                        showUpdateDialog();
                    } else {
                        MyToast.show(getString(R.string.is_new));
                    }
                } catch (Exception e) {
                    MyToast.show(getString(R.string.connect_error));
                    e.printStackTrace();
                }
            }
        }.execute("Get_VersionCode", map);
    }

    /**
     * 弹出一个对话框，提示用户更新，如果更新，则下载新版本，不更新则跳到登陆页面
     */
    private void showUpdateDialog() {
        updateDialog = new AlertDialog.Builder(this).setTitle("发现新版本!")
                // 限制对话框取消动作
                .setCancelable(false)
                .setPositiveButton("我要！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downPackage();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        String wifiTip = "";
        if (!MyNetUtils.isWifi(this)) {
            wifiTip = "检测到您的手机当前并非在wifi环境下，";
        }
        updateDialog.setMessage(String.format("新版本安装包大小为%s，%s确定更新？", TextFormater.getDataSize(apkSize), wifiTip));
        updateDialog.show();
    }

    /**
     * 下载apk安装包
     */
    public void downPackage() {
        String fileDir;
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
            fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            fileDir = getFilesDir().getAbsolutePath();
        }
        OkHttpUtils.get().url(downloadUrl).build().execute(new FileCallBack(fileDir, getString(R.string.apk_name)) {

            @Override
            public void onBefore(Request request, int id) {
                progressDialog = new ProgressDialog(AboutActivity.this);
                progressDialog.setMessage("拼命下载中...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setMax(apkSize);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.setProgress(-(int) progress);
            }

            @Override
            public void onResponse(File file, int i) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
                MyApp.getAppContext().finish();
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (updateDialog != null && updateDialog.isShowing())
            updateDialog.dismiss();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }
}
