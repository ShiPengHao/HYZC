package com.yimeng.hyzchbczhwq.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.AboutActivity;
import com.yimeng.hyzchbczhwq.activity.AccountInfoActivity;
import com.yimeng.hyzchbczhwq.activity.AppointHistoryActivity;
import com.yimeng.hyzchbczhwq.activity.PatientListActivity;
import com.yimeng.hyzchbczhwq.activity.SettingActivity;
import com.yimeng.hyzchbczhwq.huanxin.ConversationListActivity;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.MyToast;

/**
 * 我的对应fragment
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout rl_appointment_history;
    //    private RelativeLayout rl_booking;
    private RelativeLayout rl_account_info;
    private RelativeLayout rl_conversation_history;
    private RelativeLayout rl_patient_list;
    private LinearLayout ll_settings;
    private LinearLayout ll_about;
    private LinearLayout ll_quit;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view) {
        rl_account_info = (RelativeLayout) view.findViewById(R.id.rl_account_info);
        rl_appointment_history = (RelativeLayout) view.findViewById(R.id.rl_appointment_history);
//        rl_booking = (RelativeLayout) view.findViewById(rl_booking);
        rl_conversation_history = (RelativeLayout) view.findViewById(R.id.rl_conversation_history);
        rl_patient_list = (RelativeLayout) view.findViewById(R.id.rl_patient_list);
        ll_settings = (LinearLayout) view.findViewById(R.id.ll_settings);
        ll_about = (LinearLayout) view.findViewById(R.id.ll_about);
        ll_quit = (LinearLayout) view.findViewById(R.id.ll_quit);
    }

    @Override
    protected void setListener() {
        rl_account_info.setOnClickListener(this);
        rl_appointment_history.setOnClickListener(this);
//        rl_booking.setOnClickListener(this);
        rl_conversation_history.setOnClickListener(this);
        rl_patient_list.setOnClickListener(this);
        ll_settings.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        ll_quit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_appointment_history:
                startActivity(new Intent(getActivity(), AppointHistoryActivity.class));
                break;
            case R.id.rl_account_info:
                startActivity(new Intent(getActivity(), AccountInfoActivity.class));
                break;
            case R.id.rl_patient_list:
                startActivity(new Intent(getActivity(), PatientListActivity.class)
                        .putExtra(PatientListActivity.EXTRA_CHOOSE_OR_QUERY, PatientListActivity.EXTRA_QUERY));
                break;
//            case rl_booking:
//                startActivity(new Intent(getActivity(), DoctorListActivity.class));
//                break;
            case R.id.rl_conversation_history:
                startActivity(new Intent(getActivity(), ConversationListActivity.class));
                break;
            case R.id.ll_settings:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.ll_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.ll_quit:
                MyToast.show(getString(R.string.app_exit));
                MyApp.getAppContext().finish();
                break;
        }
    }
}
