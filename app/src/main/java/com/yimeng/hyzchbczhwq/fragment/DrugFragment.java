package com.yimeng.hyzchbczhwq.fragment;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.DrugTypeAdapter;
import com.yimeng.hyzchbczhwq.bean.DrugTypeBean;
import com.yimeng.hyzchbczhwq.db.DrugTypeDAO;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.PinYinUtils;
import com.yimeng.hyzchbczhwq.utils.ThreadUtils;
import com.yimeng.hyzchbczhwq.utils.WebServiceUtils;
import com.yimeng.hyzchbczhwq.view.ClearEditText;
import com.yimeng.hyzchbczhwq.view.QuickIndexBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 药品类型列表的fragment
 */
public class DrugFragment extends BaseFragment implements AdapterView.OnItemClickListener, TextWatcher, QuickIndexBar.OnLetterChangeListener {

    private ListView listView;
    private QuickIndexBar quickIndexBar;

    private DrugTypeAdapter adapter;
    private ClearEditText clearEditText;

    private List<DrugTypeBean> data = new ArrayList<>();
    private boolean isFlushing = false;
    private LinearLayout ll_loading;
    private Handler handler;
    private static final int WHAT_FLUSH_DATA = 1;
    private HashMap<String,Object> paramsMap = new HashMap<>();

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_drug;
    }

    protected void initView(View view) {
        clearEditText = (ClearEditText) view.findViewById(R.id.cet);
        listView = (ListView) view.findViewById(R.id.lv);
        quickIndexBar = (QuickIndexBar) view.findViewById(R.id.quickindexbar);
        ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
    }

    /**
     * 设置适配器和监听
     */
    protected void setListener() {
        adapter = new DrugTypeAdapter(data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        clearEditText.addTextChangedListener(this);
        quickIndexBar.setOnLetterChangeListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_FLUSH_DATA:
                        if (!isFlushing) {
                            ll_loading.setVisibility(View.VISIBLE);
                            flushData();
                        }
                        break;
                }
            }
        };
        getActivity().getContentResolver().registerContentObserver(DrugTypeDAO.DRUG_TYPE_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                handler.removeMessages(WHAT_FLUSH_DATA);
                handler.sendEmptyMessageDelayed(WHAT_FLUSH_DATA, 2000);
            }
        });
    }

    /**
     * 初始化数据源
     */
    protected void initData() {
        flushData();
        requestDrugType();
    }

    /**
     * 从本地数据库读取数据后刷新页面
     */
    private void flushData() {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public synchronized void run() {

                isFlushing = true;
                Cursor cursor = DrugTypeDAO.getInstance().getAllCursor();
                data.clear();
                while (cursor.moveToNext()) {
                    DrugTypeBean bean = new DrugTypeBean();
                    bean.CnName = cursor.getString(DrugTypeDAO.ID_NAME);
                    bean.IconUrl = cursor.getString(DrugTypeDAO.ID_ICON);
                    bean.TypeCode = cursor.getString(DrugTypeDAO.ID_CODE);
                    data.add(bean);
                }
                Collections.sort(data);

                ThreadUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (data.size() > 0) {
                            ll_loading.setVisibility(View.GONE);
                        } else {
                            ll_loading.setVisibility(View.VISIBLE);
                        }
                        isFlushing = false;
                    }
                });
            }
        });
    }

    /**
     * 请求药品分类
     */
    private void requestDrugType() {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String s = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                        null);
                if (TextUtils.isEmpty(s)) {
                    return null;
                }
                try {
                    JSONObject object = new JSONObject(s);
                    parseDrugTypeJson(object.optString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute("Load_Classify");
    }


    /**
     * 请求药品分类
     */
    private void requestDrugByType(String typeCode) {
        paramsMap.clear();
        paramsMap.put("TypeCode",typeCode);
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String s = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                        paramsMap);
                if (TextUtils.isEmpty(s)) {
                    return null;
                }
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optInt("total") > 0) {
                        parseDrugJson(object.optString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute("Load_Medicine");
    }

    /**
     * 解析药品json数据
     *
     * @param json 药品json数据
     */
    private void parseDrugJson(String json) {
        if (json == null) {
            return;
        }
        ArrayList<DrugTypeBean> beans = new Gson().fromJson(json, new TypeToken<ArrayList<DrugTypeBean>>() {
        }.getType());
        for (int i = 0; i < beans.size(); i++) {
            DrugTypeDAO.getInstance().update(beans.get(i));
        }
    }

    /**
     * 解析药品类型json数据
     *
     * @param json 药品类型json数据
     */
    private void parseDrugTypeJson(String json) {
        if (json == null) {
            return;
        }
        ArrayList<DrugTypeBean> beans = new Gson().fromJson(json, new TypeToken<ArrayList<DrugTypeBean>>() {
        }.getType());
        for (int i = 0; i < beans.size(); i++) {
            DrugTypeDAO.getInstance().update(beans.get(i));
        }
    }

    /**
     * 让listview滚动到指定的位置
     *
     * @param s 名称拼音的起始字符
     */
    private void scrollListTo(CharSequence s) {
        String pinYin;
        for (int i = 0; i < data.size(); i++) {
            pinYin = PinYinUtils.getPinYin(data.get(i).CnName);
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
        DrugTypeBean bean = data.get(position - listView.getHeaderViewsCount());
        MyToast.show(bean.toString());
//        requestDrugByType(bean.TypeCode);
    }
}
