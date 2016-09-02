package com.yimeng.hyzchbczhwq.huanxin;

import android.os.Bundle;

import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.BaseActivity;

/**
 * 会话列表
 */
public class ConversationListActivity extends BaseActivity {

    private ConversationListFragment conversationListFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (conversationListFragment == null) {
            conversationListFragment = new ConversationListFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, conversationListFragment).commit();
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

}
