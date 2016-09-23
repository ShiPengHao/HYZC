package com.yimeng.hyzchbczhwq.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.DoctorAdapter;
import com.yimeng.hyzchbczhwq.bean.DoctorBean;
import com.yimeng.hyzchbczhwq.huanxin.ChatActivity;
import com.yimeng.hyzchbczhwq.utils.MyConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 医生列表的activity
 */
public class DoctorListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static final String EXTRA_CHAT_OR_BOOKING = "EXTRA_CHAT_OR_BOOKING";
    public static final int EXTRA_CHAT = 1;
    public static final int EXTRA_BOOKING = 2;

    private int chatOrBooking = EXTRA_BOOKING;

    private ArrayList<DoctorBean> doctor = new ArrayList<>();
    private DoctorAdapter doctorAdapter;

    private ImageView iv_back;
    private ListView listView;
    private TextView tv_tip;
    private TextView tv_title;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_list;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        listView = (ListView) findViewById(R.id.lv);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_title = (TextView) findViewById(R.id.tv_title);
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
        try {
            chatOrBooking = getIntent().getIntExtra(EXTRA_CHAT_OR_BOOKING, EXTRA_BOOKING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (chatOrBooking) {
            case EXTRA_CHAT:
                tv_title.setText(getString(R.string.chat_online));
                break;
            case EXTRA_BOOKING:
                tv_title.setText(getString(R.string.booking));
                break;
        }
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
                    Collections.sort(doctor);
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
        switch (chatOrBooking) {
            case EXTRA_BOOKING:
                startActivity(new Intent(this, DoctorDetailActivity.class).putExtra("doctor", doctorBean));
                break;
            case EXTRA_CHAT:
                startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, "doctor_" + doctorBean.doctor_id));
                break;
        }
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
