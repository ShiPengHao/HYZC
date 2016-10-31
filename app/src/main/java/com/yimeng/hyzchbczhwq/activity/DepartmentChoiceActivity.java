package com.yimeng.hyzchbczhwq.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.DepartmentBean;
import com.yimeng.hyzchbczhwq.bean.HospitalBean;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 选择医院科室的activity
 */

public class DepartmentChoiceActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView iv_back;
    private ListView lv_hospital;
    private ListView lv_department;

    private ArrayAdapter<HospitalBean> hospitalAdapter;
    private ArrayAdapter<DepartmentBean> departmentAdapter;
    private ArrayList<HospitalBean> hospital = new ArrayList<>();
    private ArrayList<DepartmentBean> department = new ArrayList<>();
    private HashMap<String, Object> params = new HashMap<>();
    private HospitalBean hospitalBean;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_department_choice;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv_hospital = (ListView) findViewById(R.id.lv_hospital);
        lv_department = (ListView) findViewById(R.id.lv_department);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        hospitalAdapter = new ArrayAdapter<>(this, R.layout.item_text1, hospital);
        lv_hospital.setAdapter(hospitalAdapter);
        lv_hospital.setOnItemClickListener(this);
        departmentAdapter = new ArrayAdapter<>(this, R.layout.item_text1, department);
        lv_department.setAdapter(departmentAdapter);
        lv_department.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            ArrayList<HospitalBean> hospital = (ArrayList<HospitalBean>) getIntent().getSerializableExtra("hospital");
            this.hospital.clear();
            this.hospital.addAll(hospital);
            hospitalAdapter.notifyDataSetChanged();
            if (hospital.size()>0) {
                hospitalBean = hospital.get(0);
                params.clear();
                params.put("hospital_id", hospitalBean.hospital_id);
                params.put("parentid", 0);
                requestDepartment("Load_KS", params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得医院
     *
     * @param params 方法名+参数
     */
    private void requestDepartment(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (null == s)
                    return;
                parseListResult(department, DepartmentBean.class, s);
                departmentAdapter.notifyDataSetChanged();
            }
        }.execute(params);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_hospital:// 点击某个医院，获得科室的信息
                params.clear();
                hospitalBean = hospital.get(position);
                params.put("hospital_id", hospitalBean.hospital_id);
                params.put("parentid", 0);
                requestDepartment("Load_KS", params);
                break;
            case R.id.lv_department:
                DepartmentBean departmentBean = department.get(position);
                Intent intent = new Intent();
                intent.putExtra("hospital_id", String.valueOf(departmentBean.hospital_id));
                intent.putExtra("departments_id", String.valueOf(departmentBean.departments_id));
                intent.putExtra("hospital_name", hospitalBean.hospital_name);
                intent.putExtra("departments_name", departmentBean.departments_name);
                int chatOrBooking = getIntent().getIntExtra(DoctorListActivity.EXTRA_CHAT_OR_BOOKING, -1);
                if (chatOrBooking == -1)
                    setResult(RESULT_OK, intent);
                else
                    startActivity(intent.setClass(this,DoctorListActivity.class)
                            .putExtra(DoctorListActivity.EXTRA_CHAT_OR_BOOKING, chatOrBooking));
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
