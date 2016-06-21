package com.yimeng.hyzc.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.DrugTypeBean;
import com.yimeng.hyzc.db.DrugTypeDAO;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyLog;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.ThreadUtils;
import com.yimeng.hyzc.utils.UiUtils;
import com.yimeng.hyzc.view.PullDownToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 依萌 on 2016/6/21.
 */
public class DrugFragment extends Fragment implements AdapterView.OnItemClickListener {

    private PullDownToRefreshListView listView;

    private int itemCount = 10;
    private int itemHeightCount = 0;
    private DrugTypeCursorAdapter adapter;
    private int pageCount;
    private int itemIndex;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.getCursor().moveToPosition(position - listView.getHeaderViewsCount())) {
            String name = adapter.getCursor().getString(DrugTypeDAO.ID_NAME);
            MyToast.show(name);
        }

    }

    private class DrugTypeCursorAdapter extends CursorAdapter implements View.OnLayoutChangeListener {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            itemIndex++;
            itemHeightCount += v.getHeight();
            if (pageCount == 0 && itemIndex == itemCount) {
                if (itemHeightCount < listView.getHeight()
                        && getCursor() != null
                        && getCursor().getCount() >= itemCount) {
                    itemCount++;
                    itemIndex = 0;
                    itemHeightCount = 0;
                    getLimitCursor();
                } else {
                    pageCount = itemCount;
                }
            }
        }

        public DrugTypeCursorAdapter(Cursor c) {
            super(MyApp.getAppContext(), c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }

        @Override
        protected void onContentChanged() {
//            if (getCursor() == null || getCursor().getCount() == 0 || pageCount != 0) {
                getLimitCursor();
//            }
            MyLog.i("onContentChanged", "onContentChanged");
        }

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = UiUtils.inflate(R.layout.item_drug_type);
            view.removeOnLayoutChangeListener(this);
            if (pageCount == 0) {
                view.addOnLayoutChangeListener(this);
            }
            ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            String string = cursor.getString(DrugTypeDAO.ID_ICON);
            if (!TextUtils.isEmpty(string)) {
                Picasso.with(getContext()).load(MyConstant.URL_BASE + string).into(holder.iv);
            } else {
                holder.iv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            }
            holder.tv_drug.setText(cursor.getString(DrugTypeDAO.ID_NAME));
        }

        public class ViewHolder {
            public View rootView;
            public ImageView iv;
            public TextView tv_drug;

            public ViewHolder(View rootView) {
                this.rootView = rootView;
                this.iv = (ImageView) rootView.findViewById(R.id.iv);
                this.tv_drug = (TextView) rootView.findViewById(R.id.tv_drug);
            }

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = UiUtils.inflate(R.layout.fragment_drug);
        initView(view);
        setListener();
        initData();
        return view;
    }

    /**
     * 设置适配器和监听
     */
    private void setListener() {
        adapter = new DrugTypeCursorAdapter(null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        getLimitCursor();
    }

    private void getLimitCursor() {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = DrugTypeDAO.getInstance().getCursorByLimit(itemCount);
                ThreadUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.swapCursor(cursor);
                        if (listView.isRefreshing) {
                            listView.refreshCompleted(true);
                        } else if (listView.isLoadingMore) {
                            listView.hideFooter();
                        }
                    }
                });
            }
        });
    }

    /**
     * 初始化数据源
     */
    private void initData() {
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

    private void parseJson(String datas) {
        if (datas == null) {
            return;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DrugTypeBean>>() {
        }.getType();
        ArrayList<DrugTypeBean> beans = gson.fromJson(datas, type);
        for (int i = 0; i < beans.size(); i++) {
            DrugTypeDAO.getInstance().update(beans.get(i));
        }
        getLimitCursor();
    }

    private void initView(View view) {
        listView = (PullDownToRefreshListView) view.findViewById(R.id.lv);
        listView.setOnRefreshListener(new PullDownToRefreshListView.OnRefreshListener() {
            @Override
            public void onPullToRefresh() {
                getLimitCursor();
            }

            @Override
            public void onLoadMore() {
                itemCount += pageCount;
                getLimitCursor();
            }
        });
    }
}
