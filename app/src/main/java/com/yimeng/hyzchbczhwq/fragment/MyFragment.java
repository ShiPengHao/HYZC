package com.yimeng.hyzchbczhwq.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.AccountInfoActivity;
import com.yimeng.hyzchbczhwq.activity.AppointHistoryActivity;
import com.yimeng.hyzchbczhwq.activity.BookingActivity;

/**
 * 我的对应fragment
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout rl_appointment_history;
    private RelativeLayout rl_booking;
    private RelativeLayout rl_account_info;




    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view) {
        rl_account_info = (RelativeLayout) view.findViewById(R.id.rl_account_info);
        rl_appointment_history = (RelativeLayout) view.findViewById(R.id.rl_appointment_history);
        rl_booking = (RelativeLayout) view.findViewById(R.id.rl_booking);
    }

    @Override
    protected void setListener() {
        rl_account_info.setOnClickListener(this);
        rl_appointment_history.setOnClickListener(this);
        rl_booking.setOnClickListener(this);
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
            case R.id.rl_booking:
                startActivity(new Intent(getActivity(), BookingActivity.class));
                break;
        }
    }
}
