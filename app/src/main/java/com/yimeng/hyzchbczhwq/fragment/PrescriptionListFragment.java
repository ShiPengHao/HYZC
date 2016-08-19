package com.yimeng.hyzchbczhwq.fragment;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.BaseActivity;
import com.yimeng.hyzchbczhwq.activity.PrescribeDetailActivity;
import com.yimeng.hyzchbczhwq.adapter.DefaultAdapter;
import com.yimeng.hyzchbczhwq.bean.PrescriptionBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.holder.PrescriptionHolder;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.view.PullDownToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 药方列表界面
 */
public class PrescriptionListFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullDownToRefreshListView.OnRefreshListener {

    private PullDownToRefreshListView listView;
    private BaseAdapter adapter;
    private ArrayList<PrescriptionBean> data = new ArrayList<>();
    private String pharmacy_id;
    private int recipe_flag;
    private HashMap<String, Object> params = new HashMap<>();
    private static final int ITEM_NUMBER_PER_PAGE = 20;
    private int itemsCount;
    private static final int REQUEST_CODE_FOR_PRESCRIPTION_DETAIL = 100;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_prescription_list;
    }

    @Override
    protected void initView(View view) {
        listView = (PullDownToRefreshListView) view.findViewById(R.id.lv);
    }

    @Override
    protected void setListener() {
        adapter = new DefaultAdapter<PrescriptionBean>(data) {
            @Override
            protected BaseHolder getHolder() {
                return new PrescriptionHolder();
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.hideFooter();
    }

    @Override
    protected void initData() {
        try {
            pharmacy_id = context.getSharedPreferences(MyConstant.PREFS_ACCOUNT, Context.MODE_PRIVATE).getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
            recipe_flag = getArguments().getInt("recipe_flag");
            data.clear();
            itemsCount = 0;
            requestPrescriptionList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求药方列表
     */
    private void requestPrescriptionList() {
        params.clear();
        params.put("pharmacy_id", pharmacy_id);
        params.put("recipe_flag", recipe_flag);
        if (listView.isRefreshing()) {
            params.put("startIndex", 1);
            params.put("endIndex", Math.max(ITEM_NUMBER_PER_PAGE, itemsCount));
        } else {
            params.put("startIndex", itemsCount + 1);
            params.put("endIndex", itemsCount + ITEM_NUMBER_PER_PAGE);
        }

        new BaseActivity.SoapAsyncTask() {
            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    MyToast.show(getString(R.string.connect_error));
                    if (listView.isRefreshing()) {
                        listView.refreshCompleted(false);
                    } else if (listView.isLoadingMore()) {
                        listView.hideFooter();
                    }
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    ArrayList<PrescriptionBean> tempData = new Gson().fromJson(object.optString("data"), new TypeToken<ArrayList<PrescriptionBean>>() {
                    }.getType());
                    if (listView.isRefreshing()) {
                        data.clear();
                    }
                    data.addAll(tempData);
                    itemsCount = data.size();
                    adapter.notifyDataSetChanged();
                    if (listView.isRefreshing()) {
                        listView.refreshCompleted(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MyToast.show(getString(R.string.connect_error));
                    if (listView.isRefreshing()) {
                        listView.refreshCompleted(false);
                    }
                } finally {
                    if (listView.isLoadingMore()) {
                        listView.hideFooter();
                    }
                }
            }
        }.execute("Load_Shop_Prescription_List", params);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String prescription_id = data.get(position - listView.getHeaderViewsCount()).prescription_id;
        startActivityForResult(new Intent(context, PrescribeDetailActivity.class)
                        .putExtra("prescription_id", prescription_id)
                        .putExtra("isPharmacy", true)
                , REQUEST_CODE_FOR_PRESCRIPTION_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_FOR_PRESCRIPTION_DETAIL:
                MyToast.show("列表数据已更新，请刷新查看");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPullToRefresh() {
        requestPrescriptionList();
    }

    @Override
    public void onLoadMore() {
        if (itemsCount >= ITEM_NUMBER_PER_PAGE) {
            requestPrescriptionList();
        }
    }
}
