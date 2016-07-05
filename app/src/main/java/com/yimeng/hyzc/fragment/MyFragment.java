package com.yimeng.hyzc.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hp.hpl.sparta.Text;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.AppointmentAdapter;
import com.yimeng.hyzc.bean.AppointmentBean;
import com.yimeng.hyzc.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的对应fragment
 */
public class MyFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {


    private RelativeLayout rl_account_center;
    private RelativeLayout rl_appointment_history;
    private ImageView iv_appoint_arrow;
    private ListView listView;
    private boolean listViewToogle;
    private boolean accountToogle;
    private RelativeLayout rl_account_info;
    private ImageView iv_account_arrow;
    private Button bt_modify;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_age;
    private TextView tv_phone;

    private List<AppointmentBean> data = new ArrayList<>();
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view) {
        rl_account_center = (RelativeLayout) view.findViewById(R.id.rl_account_center);
        rl_account_info = (RelativeLayout) view.findViewById(R.id.rl_account_info);
        rl_appointment_history = (RelativeLayout) view.findViewById(R.id.rl_appointment_history);

        listView = (ListView) view.findViewById(R.id.lv);
        iv_appoint_arrow = (ImageView) view.findViewById(R.id.iv_appoint_arrow);
        iv_account_arrow = (ImageView) view.findViewById(R.id.iv_account_arrow);

        bt_modify = (Button) view.findViewById(R.id.bt_modify);

        tv_name = (TextView)view.findViewById(R.id.tv_name);
        tv_sex = (TextView)view.findViewById(R.id.tv_sex);
        tv_age = (TextView)view.findViewById(R.id.tv_age);
        tv_phone = (TextView)view.findViewById(R.id.tv_phone);
    }

    @Override
    protected void setListener() {
        listViewToogle = true;
        accountToogle = false;
        switchAccountDisplay();
        switchAppointmentListDisplay();
        rl_account_center.setOnClickListener(this);
        rl_appointment_history.setOnClickListener(this);
        bt_modify.setOnClickListener(this);
        appointmentAdapter = new AppointmentAdapter(data);
        listView.setAdapter(appointmentAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        data.clear();
        AppointmentBean bean = new AppointmentBean();
        for (int i = 0; i < 20; i++) {
            data.add(bean);
        }
        appointmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_account_center:
                switchAccountDisplay();
                break;
            case R.id.rl_appointment_history:
                switchAppointmentListDisplay();
                break;
            case R.id.bt_modify:
                MyToast.show(getString(R.string.fun_undo));
                break;
        }
    }

    /**
     * 切换账户信息的展示与否
     */
    private void switchAccountDisplay() {
        if (accountToogle) {
            rl_account_info.setVisibility(View.VISIBLE);
        } else {
            rl_account_info.setVisibility(View.GONE);
        }
        accountToogle = !accountToogle;
        iv_account_arrow.setEnabled(accountToogle);
    }

    /**
     * 切换预约历史的展示与否
     */
    private void switchAppointmentListDisplay() {
        if (listViewToogle) {
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.INVISIBLE);
        }
        listViewToogle = !listViewToogle;
        iv_appoint_arrow.setEnabled(listViewToogle);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
