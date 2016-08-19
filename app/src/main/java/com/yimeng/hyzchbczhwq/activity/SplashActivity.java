package com.yimeng.hyzchbczhwq.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.utils.BitmapUtils;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyLog;
import com.yimeng.hyzchbczhwq.utils.NetUtils;
import com.yimeng.hyzchbczhwq.utils.ThreadUtils;
import com.yimeng.hyzchbczhwq.utils.WebServiceUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 闪屏界面，处理apk版本更新，自动登陆，页面跳转等逻辑
 */
public class SplashActivity extends BaseActivity {

    private SharedPreferences spAccount;
    private Handler handler;
    private String downloadUrl;
    private String fileDir;
    private AlertDialog updateDialog;
    private int apkSize;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getImage();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        if (isFirstRunning()) {
            copyDataToLocal();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToIntroduce();
                }
            }, 2000);
        } else if (
//                isAutoUpdate() &&
                NetUtils.isConnected(this) && NetUtils.isWifi(this)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUpdate();
                }
            }, 2000);
        } else if (isAutoLogin()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attemptToLogin();
                }
            }, 2000);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, 2000);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 获得闪屏界面的背景图片
     */
    private void getImage() {
        ImageView iv = (ImageView) findViewById(R.id.iv);
        if (iv != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash);
            iv.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, DensityUtil.SCREEN_WIDTH, DensityUtil.SCREEN_HEIGHT));
            bitmap.recycle();
        }
    }

    /**
     * 将资源文件拷贝到本地
     */
    private void copyDataToLocal() {

    }


    /**
     * 自动登陆
     */
    private void attemptToLogin() {
        String username = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, "");
        String pwd = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, "");
        String type = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
        String method = null;
        if (type.equalsIgnoreCase("patient")) {
            method = "Patient_Login";
        } else if (type.equalsIgnoreCase("doctor")) {
            method = "Doctor_Login";
        } else if (type.equalsIgnoreCase("shop")) {
            method = "Shop_Login";
        }
        if (TextUtils.isEmpty(method)) {
            goToLogin();
            return;
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("user", username);
        param.put("pwd", pwd);
        requestLogin(method, param);
    }

    /**
     * 执行异步任务，登录
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void requestLogin(Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                    if (result == null) {
                        goToLogin();
                        return null;
                    }
                    try {
//                      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            String type = object.optString("type");
                            String id = object.optString("id");
                            setJPushAliasAndTag(type, id);
                        } else {
                            goToLogin();
                        }
                    } catch (Exception e) {
                        goToLogin();
                        e.printStackTrace();
                    }
                } else {
                    goToLogin();
                }
                return null;
            }
        }.execute(params);
    }

    /**
     * 登陆成功后为本应用用户绑定JPush的别名和标签，别名为账号类型+"-"+id，标签为账号类型，设置成功以后跳转到主页
     */
    private void setJPushAliasAndTag(final String type, final String id) {
        final HashSet<String> tags = new HashSet<>();
        tags.add(type);
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                JPushInterface.setAliasAndTags(MyApp.getAppContext(), type + "+" + id, tags, new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        if (i != 0) {
                            MyLog.i("JPush", "set alias and tag error");
                            goToLogin();
                        } else {
                            goToHome(type);
                        }
                    }
                });
            }
        });
    }

    /**
     * 查询最新版本，比较版本号，如果有新版本，提示用户
     */
    private void checkUpdate() {
        final String packageName = getPackageName();
        HashMap<String, Object> map = new HashMap<>();
        map.put("hospital_id", MyConstant.HOSPITAL_ID);
        map.put("departments_id", MyConstant.DEPARTMENT_ID);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    goToLogin();
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
                        goToLogin();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    goToLogin();
                }
            }
        }.execute("Get_VersionCode", map);
    }

    /**
     * 弹出一个对话框，提示用户更新，如果更新，则下载新版本，不更新则跳到登陆页面
     */
    private void showUpdateDialog() {
        // 限制对话框取消动作
        updateDialog = new AlertDialog.Builder(SplashActivity.this).setTitle("技术同学又出新版本啦!")
                .setMessage("现在更新？")
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
                        goToLogin();
                        dialog.dismiss();
                    }
                })
                .create();
        updateDialog.show();
    }

    /**
     * 下载apk安装包
     */
    public void downPackage() {
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
            fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            fileDir = getFilesDir().getAbsolutePath();
        }
        OkHttpUtils.get().url(downloadUrl).build().execute(new FileCallBack(fileDir, "hyzc.apk") {

            @Override
            public void onBefore(Request request, int id) {
                progressDialog = new ProgressDialog(SplashActivity.this);
                progressDialog.setMessage("拼命下载中...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setMax(apkSize);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (progress < apkSize)
                    progressDialog.setProgress((int) progress);
            }

            @Override
            public void onResponse(File file, int i) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
                progressDialog.dismiss();
                goToLogin();
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

    /**
     * 跳转到登陆页面
     */
    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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

    /**
     * 跳转到引导界面
     */
    private void goToIntroduce() {
        startActivity(new Intent(this, IntroduceActivity.class));
        finish();
    }

    /**
     * 判断本应用是否在本机首次运行
     *
     * @return
     */
    private boolean isFirstRunning() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, true);
    }

    /**
     * 判断是否设置自动更新
     *
     * @return
     */
    private boolean isAutoUpdate() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_AUTOUPDATE, false);
    }

    /**
     * 判断是否设置自动登陆
     *
     * @return
     */
    private boolean isAutoLogin() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, false);
    }
}
