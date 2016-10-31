package com.yimeng.hyzchbczhwq.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.DefaultAdapter;
import com.yimeng.hyzchbczhwq.bean.CommentBean;
import com.yimeng.hyzchbczhwq.bean.DoctorBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.holder.CommentHolder;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.view.PullDownToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 医生满意度评分详情界面
 */
public class DoctorScoreDetailActivity extends BaseActivity implements View.OnClickListener, PullDownToRefreshListView.OnRefreshListener {

    private TextView tv_doctor;
    private int doctor_id;
    private HashMap<String, Object> params = new HashMap<>();
    private int pageCount;
    private int itemCount;
    private ArrayList<CommentBean> comments = new ArrayList<>();
    private PullDownToRefreshListView lv;
    private final int DEFAULT_NUMBER_PER_PAGE = 10;
    private DefaultAdapter<CommentBean> adapter;
    private ImageView iv_back;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_score_detail;
    }

    @Override
    protected void initView() {
        tv_doctor = (TextView) findViewById(R.id.tv_doctor);
        lv = (PullDownToRefreshListView) findViewById(R.id.lv);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        lv.setOnRefreshListener(this);
        adapter = new DefaultAdapter<CommentBean>(comments) {
            @Override
            protected BaseHolder getHolder() {
                return new CommentHolder();
            }
        };
        lv.setAdapter(adapter);
        lv.hideFooter();
        lv.setFooterDividersEnabled(false);
    }

    @Override
    protected void initData() {
        try {
            DoctorBean doctorBean = (DoctorBean) getIntent().getSerializableExtra("doctor");
            tv_doctor.setText(doctorBean.doctor_name);
            doctor_id = doctorBean.doctor_id;
            pageCount = 1;
            itemCount = 0;
            comments.clear();
            requestComment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得此医生的评论
     */
    private void requestComment() {
        params.clear();
        params.put("doctor_id", doctor_id);
        params.put("pageindex", pageCount);
        params.put("pagesize", DEFAULT_NUMBER_PER_PAGE);
        if (lv.isLoadingMore()) {
            if (pageCount * DEFAULT_NUMBER_PER_PAGE == itemCount) {
                pageCount++;
                params.put("pageindex", pageCount);
            } else {
                MyToast.show(getString(R.string.no_more_data));
                return;
            }
        } else if (lv.isRefreshing()) {
            params.put("pageindex", 1);
        }
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    MyToast.show(getString(R.string.connect_error));
                    return;
                }
                if (lv.isRefreshing()) {
                    parseListResult(comments, CommentBean.class, s);
                } else {
                    ArrayList<CommentBean> tempList = new ArrayList<>();
                    parseListResult(tempList, CommentBean.class, s);
                    comments.addAll(tempList);
                }
                itemCount = comments.size();
                if (itemCount % DEFAULT_NUMBER_PER_PAGE == 0)
                    pageCount = itemCount / DEFAULT_NUMBER_PER_PAGE;
                else
                    pageCount = itemCount / DEFAULT_NUMBER_PER_PAGE + 1;
                adapter.notifyDataSetChanged();
                if (itemCount == 0) {
                    MyToast.show(getString(R.string.no_more_data));
                }
                if (lv.isRefreshing()) {
                    lv.refreshCompleted(true);
                } else if (lv.isLoadingMore()) {
                    lv.hideFooter();
                }
            }
        }.execute("get_doctor_Comment", params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onPullToRefresh() {
        requestComment();
    }

    @Override
    public void onLoadMore() {
        requestComment();
    }
}
