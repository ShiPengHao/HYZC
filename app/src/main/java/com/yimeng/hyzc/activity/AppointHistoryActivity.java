package com.yimeng.hyzc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.AppointmentAdapter;
import com.yimeng.hyzc.bean.AppointmentBean;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.WebServiceUtils;
import com.yimeng.hyzc.view.PullDownToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约历史
 */
public class AppointHistoryActivity extends BaseActivity implements View.OnClickListener, PullDownToRefreshListView.OnRefreshListener, AdapterView.OnItemClickListener {

    private List<AppointmentBean> data = new ArrayList<>();
    private PullDownToRefreshListView listView;
    private AppointmentAdapter appointmentAdapter;
    private String userId;
    private HashMap<String, Object> params = new HashMap<>();
    private int itemsCount;
    private String type;
    private ImageView iv_back;
    public static final int REQUEST_CODE_FOR_APPOINT_DETAIL = 100;
    private static final int ITEM_NUMBER_PER_PAGE = 20;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_appoint_history;
    }

    @Override
    protected void initView() {
        listView = (PullDownToRefreshListView) findViewById(R.id.lv);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        appointmentAdapter = new AppointmentAdapter(data);
        listView.setAdapter(appointmentAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.hideFooter();
    }

    @Override
    protected void initData() {
        userId = context.getSharedPreferences(MyConstant.PREFS_ACCOUNT, Context.MODE_PRIVATE).getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
        type = context.getSharedPreferences(MyConstant.PREFS_ACCOUNT, Context.MODE_PRIVATE).getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
        data.clear();
        itemsCount = 0;
        requestAppointmentList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(type)) {
            MyToast.show("账号异常，请重新登陆再试");
            return;
        }
    }

    /**
     * 请求预约列表
     */
    private void requestAppointmentList() {
        params.clear();
        params.put("orderby", "add_time desc");// 默认按时间倒序
        String where = String.format("%s_id=%s", type, userId);
        if (type.equalsIgnoreCase("doctor")) {
            where = "select_" + where;
            params.put("orderby", "doctor_dispose asc, add_time desc");// 医生按处理标志升序，时间倒序
        }
        params.put("where", where);
        if (listView.isRefreshing()) {
            params.put("startIndex", 1);
            params.put("endIndex", Math.max(ITEM_NUMBER_PER_PAGE, itemsCount));
        } else {
            params.put("startIndex", itemsCount + 1);
            params.put("endIndex", itemsCount + ITEM_NUMBER_PER_PAGE);
        }
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Load_AppointmentData",
                            (Map<String, Object>) params[0]);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result == null) {
                    MyToast.show(getString(R.string.connet_error));
                    if (listView.isRefreshing()) {
                        listView.refreshCompleted(false);
                    } else if (listView.isLoadingMore()) {
                        listView.hideFooter();
                    }
                    return;
                }
                parseAppointList(result);
            }
        }.execute(params);
    }

    /**
     * 解析预约列表
     *
     * @param result
     */
    private void parseAppointList(String result) {
        try {
            JSONObject object = new JSONObject(result);
            ArrayList<AppointmentBean> tempData = new Gson().fromJson(object.optString("data"), new TypeToken<ArrayList<AppointmentBean>>() {
            }.getType());
            if (tempData.size() == 0) {
                MyToast.show(getString(R.string.no_more_data));
            }
            if (listView.isRefreshing()) {
                data.clear();
            }
            data.addAll(tempData);
            itemsCount = data.size();
            appointmentAdapter.notifyDataSetChanged();
            if (listView.isRefreshing()) {
                listView.refreshCompleted(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyToast.show(getString(R.string.connet_error));
            if (listView.isRefreshing()) {
                listView.refreshCompleted(false);
            }
        } finally {
            if (listView.isLoadingMore()) {
                listView.hideFooter();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        goToDetail(data.get(position - listView.getHeaderViewsCount()).appointment_id);
    }

    /**
     * 跳转到预约详情界面
     *
     * @param id 预约单id
     */
    private void goToDetail(int id) {
        startActivityForResult(new Intent(this, AppointDetailActivity.class).putExtra("id", id).putExtra("type", type)
                , REQUEST_CODE_FOR_APPOINT_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data){
            return;
        }
        switch (requestCode){
            case REQUEST_CODE_FOR_APPOINT_DETAIL:
                MyToast.show("列表数据已更新，请刷新查看");
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

    @Override
    public void onPullToRefresh() {
        requestAppointmentList();
    }

    @Override
    public void onLoadMore() {
        if (itemsCount >= ITEM_NUMBER_PER_PAGE) {
            requestAppointmentList();
        }
    }
}
