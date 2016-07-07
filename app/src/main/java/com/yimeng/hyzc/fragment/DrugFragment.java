package com.yimeng.hyzc.fragment;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.adapter.DrugTypeAdapter;
import com.yimeng.hyzc.bean.DrugTypeBean;
import com.yimeng.hyzc.db.DrugTypeDAO;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyLog;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.PinYinUtils;
import com.yimeng.hyzc.utils.ThreadUtils;
import com.yimeng.hyzc.utils.UiUtils;
import com.yimeng.hyzc.view.ClearEditText;
import com.yimeng.hyzc.view.QuickIndexBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 药品类型列表的fragment
 */
public class DrugFragment extends Fragment implements AdapterView.OnItemClickListener, TextWatcher, QuickIndexBar.OnLetterChangeListener {

    private ListView listView;
    private QuickIndexBar quickIndexBar;

    private DrugTypeAdapter adapter;
    private ClearEditText clearEditText;

    private List<DrugTypeBean> datas = new ArrayList<>();
    private boolean isFlushing = false;
    private LinearLayout loading;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = UiUtils.inflate(R.layout.fragment_drug);
        initView(view);
        setListener();
        initData();
        return view;
    }

    private void initView(View view) {
        clearEditText = (ClearEditText) view.findViewById(R.id.cet);
        listView = (ListView) view.findViewById(R.id.lv);
        quickIndexBar = (QuickIndexBar) view.findViewById(R.id.quickindexbar);
        loading = (LinearLayout) view.findViewById(R.id.ll_loading);
    }

    /**
     * 设置适配器和监听
     */
    private void setListener() {
        adapter = new DrugTypeAdapter(datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        clearEditText.addTextChangedListener(this);
        quickIndexBar.setOnLetterChangeListener(this);

        getActivity().getContentResolver().registerContentObserver(DrugTypeDAO.DRUG_TYPE_URI, true, new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                if (!isFlushing) {
                    flushData();
                }
            }
        });
    }

    /**
     * 初始化数据源
     */
    private void initData() {
        flushData();
        OkHttpUtils.get().url(MyConstant.URL_DRUGTYPE).build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int i) throws Exception {
                String s = response.body().string();
                if (TextUtils.isEmpty(s)) {
                    return null;
                }
                JSONObject object = new JSONObject(s);
                if (object.optInt("total") > 0) {
                    parseJson(object.optString("rows"));
                }
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Object o, int i) {

            }
        });

    }


    /**
     * 从本地数据库读取数据后刷新页面//TODO 请求本地数据库更新界面优化
     */
    private void flushData() {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public synchronized void run() {
                synchronized (DrugFragment.this) {
                    isFlushing = true;
                    Cursor cursor = DrugTypeDAO.getInstance().getAllCursor();
                    datas.clear();
                    while (cursor.moveToNext()) {
                        DrugTypeBean bean = new DrugTypeBean();
                        bean.name = cursor.getString(DrugTypeDAO.ID_NAME);
                        bean.icon = cursor.getString(DrugTypeDAO.ID_ICON);
                        bean.TypeCode = cursor.getString(DrugTypeDAO.ID_CODE);
                        datas.add(bean);
                    }
                    Collections.sort(datas);
                }
                ThreadUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (datas.size() > 0) {
                            loading.setVisibility(View.GONE);
                        } else {
                            loading.setVisibility(View.VISIBLE);
                        }
                        isFlushing = false;
                    }
                });
            }
        });
    }

    /**
     * 解析json
     *
     * @param json json数据
     */
    private void parseJson(String json) {
        if (json == null) {
            return;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DrugTypeBean>>() {
        }.getType();
        ArrayList<DrugTypeBean> beans = gson.fromJson(json, type);
//        SystemClock.sleep(3000);
        if (!datas.equals(beans)) {
            for (int i = 0; i < beans.size(); i++) {
                DrugTypeDAO.getInstance().update(beans.get(i));
            }
        }
    }

    /**
     * 让listview滚动到指定的位置
     *
     * @param s 名称拼音的起始字符
     */
    private void scrollListTo(CharSequence s) {
        String pinYin;
        for (int i = 0; i < datas.size(); i++) {
            pinYin = PinYinUtils.getPinYin(datas.get(i).name);
            if (pinYin.startsWith(String.valueOf(s).toUpperCase())) {
                listView.setSelection(i + listView.getHeaderViewsCount());
                break;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        scrollListTo(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onLetterChange(String letter) {
        scrollListTo(letter);
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DrugTypeBean bean = datas.get(position - listView.getHeaderViewsCount());
        MyToast.show(bean.toString());
    }
}
