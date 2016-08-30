package com.yimeng.hyzchbczhwq.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.MedicineBean;
import com.yimeng.hyzchbczhwq.bean.MedicineUsageBean;
import com.yimeng.hyzchbczhwq.utils.KeyBoardUtils;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.WebServiceUtils;
import com.yimeng.hyzchbczhwq.view.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 医生开药方界面
 */
public class DoctorPrescribeActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView iv_back;
    private ClearEditText cet;
    private TextView tv_medicine_name;
    private TextView tv_medicine_common_name;
    private TextView tv_medicine_specification;
    private TextView tv_medicine_unit;
    private TextView tv_medicine_origin;
    private TextView tv_medicine_care;
    private TextView tv_medicine_usage;
    private Button bt_submit;
    private Button bt_cancel;

    private ArrayList<MedicineBean> medicines = new ArrayList<>();
    private MedicineBean medicineBean;
    private HashMap<String, Object> keywordMap;
    private CopyOnWriteArrayList<AsyncTask> asyncTasks = new CopyOnWriteArrayList<>();
    private ArrayAdapter<MedicineBean> adapter;
    private ListView lv;
    private ArrayList<MedicineUsageBean> usages = new ArrayList<>();
    private MedicineUsageBean usage;
//    private Button bt_select;
    private LinearLayout ll_usage;
    private NumberPicker medicineNumberPicker;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_prescribe;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        cet = (ClearEditText) findViewById(R.id.cet);
        tv_medicine_name = (TextView) findViewById(R.id.tv_medicine_name);
        tv_medicine_common_name = (TextView) findViewById(R.id.tv_medicine_common_name);
        tv_medicine_specification = (TextView) findViewById(R.id.tv_medicine_specification);
        tv_medicine_unit = (TextView) findViewById(R.id.tv_medicine_unit);
        tv_medicine_origin = (TextView) findViewById(R.id.tv_medicine_origin);
        tv_medicine_care = (TextView) findViewById(R.id.tv_medicine_care);
        tv_medicine_usage = (TextView) findViewById(R.id.tv_medicine_usage);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
//        bt_select = (Button) findViewById(R.id.bt_select);
        ll_usage = (LinearLayout) findViewById(R.id.ll_usage);
        lv = (ListView) findViewById(R.id.lv);

        medicineNumberPicker = (NumberPicker) findViewById(R.id.np);
        medicineNumberPicker.setMaxValue(100);
        medicineNumberPicker.setMinValue(1);
        medicineNumberPicker.setValue(1);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        ll_usage.setOnClickListener(this);
        cet.addTextChangedListener(new ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 2) {
                    return;
                }
                AsyncTask task;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    task = asyncTasks.get(i);
                    if (!task.isCancelled() && task.cancel(true)) {
                        asyncTasks.remove(task);
                    }
                }
                requestMedicineList(s.toString());
            }
        });

        lv.setOnItemClickListener(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicines);
        lv.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        requestUsage();
    }

    /**
     * 根据药品用途列表
     */
    private void requestUsage() {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Load_Usage",
                        null);
                try {
                    JSONObject object = new JSONObject(result);
                    usages = new Gson().fromJson(object.optString("data"), new TypeToken<ArrayList<MedicineUsageBean>>() {
                    }.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /**
     * 根据输入获得相应的药品
     *
     * @param keyWord 输入的拼音码
     */
    private void requestMedicineList(final String keyWord) {
        final AsyncTask<Object, Object, String> asyncTask = new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (null == keywordMap) {
                    keywordMap = new HashMap<>();
                }
                keywordMap.put("keyword", keyWord);
                String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Search_Medicine",
                        keywordMap);
                try {
                    parseMedicineList(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                asyncTasks.remove(this);
                return null;
            }
        };
        asyncTask.execute();
        asyncTasks.add(asyncTask);
    }

    /**
     * 解析药品列表
     *
     * @param jsonString json数据
     * @throws JSONException
     */
    private synchronized void parseMedicineList(String jsonString) throws JSONException {
        JSONObject object = new JSONObject(jsonString);
        final ArrayList<MedicineBean> temp = new Gson().fromJson(object.optString("data"), new TypeToken<ArrayList<MedicineBean>>() {
        }.getType());
        medicines.clear();
        medicines.addAll(temp);
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              adapter.notifyDataSetChanged();
                              if (temp.size() > 0) {
                                  lv.setVisibility(View.VISIBLE);
                              } else {
                                  MyToast.show("未找到匹配的药品");
                                  lv.setVisibility(View.GONE);
                              }
                          }
                      }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_submit:
                submitMedicine();
                break;
            case R.id.bt_cancel:
                clearText();
                break;
            case R.id.ll_usage:
                showUsageDialog();
                break;
        }
    }

    /**
     * 弹出选择用法对话框，选择用法
     */
    private void showUsageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择药品用法")
                .setSingleChoiceItems(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usages)
                        , 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                usage = usages.get(which);
                                tv_medicine_usage.setText(usage.toString());
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    /**
     * 清除此药品信息，重新搜索选择药品
     */
    private void clearText() {
        MyToast.show("请重新输入拼音码搜索药品");
        cet.setText("");
        tv_medicine_care.setText("");
        tv_medicine_common_name.setText("");
        tv_medicine_name.setText("");
        tv_medicine_origin.setText("");
        tv_medicine_specification.setText("");
        tv_medicine_unit.setText("");
        medicineNumberPicker.setValue(1);
        medicineBean = null;
        usage = null;
        tv_medicine_usage.setText(getString(R.string.medicine_usage_select));
        hideMedicineList();
    }

    /**
     * 隐藏药品列表
     */
    private void hideMedicineList() {
        lv.setVisibility(View.GONE);
        KeyBoardUtils.closeKeybord(cet, this);
    }

    /**
     * 确认选择此药品
     */
    private void submitMedicine() {
        if (null == medicineBean) {
            MyToast.show("您还未选择任何药品");
            return;
        }

        if (null == usage) {
            MyToast.show(getString(R.string.medicine_usage_select));
            ObjectAnimator.ofFloat(tv_medicine_usage, "translationX", 15, -15, 15, -15, 0).setDuration(300).start();
            return;
        }
        medicineBean.medicines_quantity = String.valueOf(medicineNumberPicker.getValue());
        medicineBean.medicines_usage = usage.usage_code;
        hideMedicineList();
        setResult(100, new Intent().putExtra("medicine", medicineBean));
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        medicineBean = medicines.get(position);
        tv_medicine_name.setText(medicineBean.CnName);
        tv_medicine_unit.setText(medicineBean.Unit);
        tv_medicine_common_name.setText(medicineBean.CommonName);
        tv_medicine_specification.setText(null == medicineBean.Specification ? "无" : medicineBean.Specification);
        tv_medicine_care.setText(medicineBean.HealthCare);
        tv_medicine_origin.setText(medicineBean.Origin);
        hideMedicineList();
    }

}
