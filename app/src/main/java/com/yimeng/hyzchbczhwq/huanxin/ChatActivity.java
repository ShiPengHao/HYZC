package com.yimeng.hyzchbczhwq.huanxin;

import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.BaseActivity;

/**
 * 聊天界面，只管理一个聊天的fragment
 */
public class ChatActivity extends BaseActivity {

    private String toChatUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        setChatFragment(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EaseUI.getInstance().getNotifier().reset(getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_empty;
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

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        // make sure only one chat activity is opened
        String username = intent.getStringExtra(EaseConstant.EXTRA_USER_ID);
        if (toChatUsername.equals(username)) {
            super.onNewIntent(intent);
        }else{
            toChatUsername = username;
        }
        setChatFragment(intent);
    }

    private void setChatFragment(Intent intent) {
        //new出EaseChatFragment或其子类的实例
        EaseChatFragment chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(intent.getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, chatFragment).commit();
    }

}
