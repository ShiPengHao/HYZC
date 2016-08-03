package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.DoctorAdapter;
import com.yimeng.hyzc.bean.DoctorBean;
import com.yimeng.hyzc.utils.MyConstant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 查询医生，在线预约的activity
 */
public class BookingActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ArrayList<DoctorBean> doctor = new ArrayList<>();
    private DoctorAdapter doctorAdapter;

    private ImageView iv_back;
    private ListView listView;
    private TextView tv_tip;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_booking;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        listView = (ListView) findViewById(R.id.lv);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_tip.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        doctorAdapter = new DoctorAdapter(doctor);
        listView.setAdapter(doctorAdapter);
        listView.setOnItemClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("departments_id", MyConstant.DEPARTMENT_ID);
        requestDoctor("Load_Doctor", hashMap);
    }

    /**
     * 根据科室请求医生信息
     *
     * @param params 方法名+参数列表
     */
    public void requestDoctor(Object... params) {
        new SoapAsyncTask() {
            protected void onPostExecute(String result) {
                if (result != null) {
                    parseListResult(doctor, DoctorBean.class, result);
                    doctorAdapter.notifyDataSetChanged();
                    if (doctor.size() > 0) {
                        listView.setVisibility(View.VISIBLE);
                        tv_tip.setVisibility(View.GONE);
                    } else {
                        listView.setVisibility(View.GONE);
                        tv_tip.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute(params);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DoctorBean doctorBean = doctor.get(position);
        startActivity(new Intent(this, DoctorDetailActivity.class).putExtra("doctor", doctorBean));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
