package com.yimeng.hyzchbczhwq.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.DefaultAdapter;
import com.yimeng.hyzchbczhwq.bean.ModuleTemplateBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyToast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 病情描述模板界面
 */
public class DiseaseTemplateActivity extends BaseActivity implements View.OnClickListener {

    private ListView listView;
    private ArrayList<ModuleTemplateBean> templates = new ArrayList<>();
    /**
     * 选中的疾病条目信息
     */
    private ArrayList<String> checkedItems = new ArrayList<>();

    HashMap<String, Object> hashMap = new HashMap<>();

    private ImageView iv_back;
    private Button bt_submit;
    public static final String EXTRA_CHECKED_ITEMS = "checkedItems";
    private DefaultAdapter<ModuleTemplateBean> adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_disease_module;
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.lv);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        adapter = new DefaultAdapter<ModuleTemplateBean>(templates) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ModuleTemplateBean templateBean = templates.get(position);
                final String checkedItem = templateBean.template_name + "：" + templateBean.template_text;
                if (convertView == null) {
                    convertView = new CheckBox(context);
                    convertView.setPadding(0, DensityUtil.dip2px(3), 0, DensityUtil.dip2px(3));
                    convertView.setMinimumHeight(DensityUtil.dip2px(12));
                    ((CheckBox) convertView).setTextColor(getResources().getColor(R.color.black_item_content));
                    ((CheckBox) convertView).setTextSize(DensityUtil.dip2px(8));
                    ((CheckBox) convertView).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked)
                                checkedItems.add(checkedItem);
                            else checkedItems.remove(checkedItem);
                        }
                    });
                }
                ((CheckBox) convertView).setText(templateBean.template_text);
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
        hashMap.put("type", 1);
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
            case R.id.bt_submit:
                setResult(100, new Intent().putExtra(EXTRA_CHECKED_ITEMS, checkedItems));
                finish();
                break;
        }
    }
}
