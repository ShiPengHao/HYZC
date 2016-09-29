package com.yimeng.hyzchbczhwq.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.AccountInfoActivity;
import com.yimeng.hyzchbczhwq.activity.AppointHistoryActivity;
import com.yimeng.hyzchbczhwq.huanxin.ConversationListActivity;
import com.yimeng.hyzchbczhwq.huanxin.PreferenceManager;
import com.yimeng.hyzchbczhwq.utils.MyConstant;

import static android.content.Context.MODE_PRIVATE;
import static com.yimeng.hyzchbczhwq.R.id.tb_autoLogin;

/**
 * 我的对应fragment
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout rl_appointment_history;
    //    private RelativeLayout rl_booking;
    private RelativeLayout rl_account_info;
    private RelativeLayout rl_conversation_history;
    private ToggleButton tb_sound;
    private ToggleButton tb_vibrate;
    private ToggleButton tb_autoLogin;
    private SharedPreferences sharedPreferences;

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
        tb_sound = (ToggleButton) view.findViewById(R.id.tb_sound);
        tb_vibrate = (ToggleButton) view.findViewById(R.id.tb_vibrate);
        tb_sound.setChecked(PreferenceManager.getInstance().getSettingMsgSound());
        tb_vibrate.setChecked(PreferenceManager.getInstance().getSettingMsgVibrate());

        tb_autoLogin = (ToggleButton) view.findViewById(R.id.tb_autoLogin);
        sharedPreferences = context.getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        tb_autoLogin.setChecked(sharedPreferences.getBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN,false));
    }

    @Override
    protected void setListener() {
        rl_account_info.setOnClickListener(this);
        rl_appointment_history.setOnClickListener(this);
//        rl_booking.setOnClickListener(this);
        rl_conversation_history.setOnClickListener(this);
        tb_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getInstance().setSettingMsgSound(isChecked);
            }
        });
        tb_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getInstance().setSettingMsgVibrate(isChecked);
            }
        });
        tb_autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN,isChecked).apply();
            }
        });
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
//            case rl_booking:
//                startActivity(new Intent(getActivity(), DoctorListActivity.class));
//                break;
            case R.id.rl_conversation_history:
                startActivity(new Intent(getActivity(), ConversationListActivity.class));
                break;
        }
    }
}
