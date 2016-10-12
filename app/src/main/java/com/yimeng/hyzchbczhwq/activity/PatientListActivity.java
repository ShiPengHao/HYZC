package com.yimeng.hyzchbczhwq.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.DefaultAdapter;
import com.yimeng.hyzchbczhwq.bean.PatientBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.holder.PatientHolder;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 病人列表的activity
 */
public class PatientListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    public static final String EXTRA_CHOOSE_OR_QUERY = "EXTRA_CHOOSE_OR_QUERY";
    public static final int EXTRA_CHOOSE = 1;
    public static final int EXTRA_QUERY = 2;
    private static final int REQUEST_CODE_FOR_EDIT_PATIENT = 100;

    private int chooseOrQuery = EXTRA_CHOOSE;

    private ArrayList<PatientBean> patients = new ArrayList<>();

    private ImageView iv_back;
    private ListView listView;
    private TextView tv_tip;
    private TextView tv_add;
    private DefaultAdapter<PatientBean> patientAdapter;
    private String user_id;
    private HashMap<String, Object> hashMap = new HashMap<>();
    private AlertDialog patientDelDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_patient_list;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        listView = (ListView) findViewById(R.id.lv);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_tip.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        patientAdapter = new DefaultAdapter<PatientBean>(patients) {
            @Override
            protected BaseHolder getHolder() {
                return new PatientHolder();
            }
        };
        listView.setAdapter(patientAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        iv_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        user_id = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE).getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
        if (TextUtils.isEmpty(user_id))
            return;
        try {
            Intent intent = getIntent();
            chooseOrQuery = intent.getIntExtra(EXTRA_CHOOSE_OR_QUERY, EXTRA_CHOOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestPatientList();
    }

    /**
     * 根据账号id获得绑定到该账号的病人列表
     */
    public void requestPatientList() {
        hashMap.clear();
        hashMap.put("user_id", user_id);
        new SoapAsyncTask() {
            protected void onPostExecute(String result) {
                if (result != null) {
                    parseListResult(patients, PatientBean.class, result);
                    patientAdapter.notifyDataSetChanged();
                    if (patients.size() > 0) {
                        listView.setVisibility(View.VISIBLE);
                        tv_tip.setVisibility(View.GONE);
                    } else {
                        listView.setVisibility(View.GONE);
                        tv_tip.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute("Get_UserPatient_Msg", hashMap);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PatientBean bean = patients.get(position);
        switch (chooseOrQuery) {
            case EXTRA_QUERY:
                startActivityForResult(new Intent(this, PatientEditActivity.class).putExtra("patient", bean), REQUEST_CODE_FOR_EDIT_PATIENT);
                break;
            case EXTRA_CHOOSE:
                setResult(101, new Intent().putExtra("patient_id", bean.patient_id).putExtra("patient_name", bean.patient_name));
                finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_add:
                startActivityForResult(new Intent(this, PatientEditActivity.class).putExtra("user_id", user_id), REQUEST_CODE_FOR_EDIT_PATIENT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data)
            return;
        switch (requestCode) {
            case REQUEST_CODE_FOR_EDIT_PATIENT:
                requestPatientList();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showPatientDelDialog(patients.get(position));
        return true;
    }

    /**
     * 删除病人提示对话框
     */
    private void showPatientDelDialog(final PatientBean bean) {
        if (patientDelDialog == null) {
            patientDelDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.tip))
                    .setMessage("确定删除" + bean.patient_name + "吗？")
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPatientDel(bean.patient_id);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
        patientDelDialog.show();
    }

    /**
     * 请求服务器删除该病人的信息，成功后刷新列表
     *
     * @param patient_id 病人id
     */
    private void requestPatientDel(String patient_id) {
        hashMap.clear();
        hashMap.put("patient_id", patient_id);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        MyToast.show(object.optString("msg"));
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            requestPatientList();
                        }
                    } catch (Exception e) {
                        MyToast.show(getString(R.string.connect_error));
                        e.printStackTrace();
                    }
                } else {
                    MyToast.show(getString(R.string.connect_error));
                }
            }
        }.execute("Patient_Delete", hashMap);
    }

    @Override
    protected void onDestroy() {
        if (patientDelDialog != null && patientDelDialog.isShowing())
            patientDelDialog.dismiss();
        super.onDestroy();
    }
}
