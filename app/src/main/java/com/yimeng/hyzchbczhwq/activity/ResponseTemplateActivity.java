package com.yimeng.hyzchbczhwq.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.DefaultAdapter;
import com.yimeng.hyzchbczhwq.bean.ModuleTemplateBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 医生回应模板界面
 */
public class ResponseTemplateActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private ArrayList<ModuleTemplateBean> templates = new ArrayList<>();

    HashMap<String, Object> hashMap = new HashMap<>();

    private ImageView iv_back;
    public static final String EXTRA_TEMPLATE = "template";
    private DefaultAdapter<ModuleTemplateBean> adapter;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_response_module;
    }


    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.lv);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        adapter = new DefaultAdapter<ModuleTemplateBean>(templates) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ModuleTemplateBean templateBean = templates.get(position);
                if (convertView == null) {
                    convertView = new TextView(context);
                    convertView.setPadding(0, DensityUtil.dip2px(3), 0, DensityUtil.dip2px(3));
                    convertView.setMinimumHeight(DensityUtil.dip2px(12));
                    ((TextView) convertView).setTextColor(getResources().getColor(R.color.black_item_content));
                    ((TextView) convertView).setTextSize(DensityUtil.dip2px(8));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setResult(100, new Intent().putExtra(EXTRA_TEMPLATE, templateBean.template_text));
                            finish();
                        }
                    });
                }
                ((TextView) convertView).setText(templateBean.template_text);
                return convertView;
            }

            @Override
            protected BaseHolder getHolder() {
                return null;
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        hashMap.clear();
        hashMap.put("type", 0);
        hashMap.put("departments_id", getIntent().getStringExtra("departments_id"));
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    MyToast.show(getString(R.string.connect_error));
                    return;
                }
                parseListResult(templates, ModuleTemplateBean.class, s);
                adapter.notifyDataSetChanged();
                if (templates.size() == 0) {
                    MyToast.show(getString(R.string.no_more_data));
                }
            }
        }.execute("Get_Tmp_Content", hashMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
