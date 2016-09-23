package com.yimeng.hyzchbczhwq.activity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.ModuleClassifyBean;
import com.yimeng.hyzchbczhwq.bean.ModuleTemplateBean;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 医生回应模板界面
 */
public class ResponseTemplateActivity extends BaseActivity implements View.OnClickListener {
    private ExpandableListView expend_lv;
    /**
     * 一级分类
     */
    private ArrayList<ModuleClassifyBean> classifyBeanArrayList = new ArrayList<>();
    /**
     * 对应一级分类的2级条目
     */
    private ArrayList<ArrayList<ModuleTemplateBean>> items = new ArrayList<>();


    HashMap<String, Object> hashMap = new HashMap<>();

    private ImageView iv_back;
    public static final String EXTRA_TEMPLATE = "template";


    private class MyExpandableListAdapter implements ExpandableListAdapter {

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return classifyBeanArrayList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return items.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        private class GroupViewHolder {
            public TextView title;
            public ImageView iv;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder;
            if (convertView == null) {
                holder = new GroupViewHolder();
                convertView = UiUtils.inflate(R.layout.item_group);
                holder.title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            String groupTitle = classifyBeanArrayList.get(groupPosition).classify_name;
            holder.title.setText(groupTitle);
            if (isExpanded) {
                holder.iv.setImageResource(R.mipmap.d_arrow);
            } else {
                holder.iv.setImageResource(R.mipmap.r_arrow);
            }
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ModuleTemplateBean templateBean = items.get(groupPosition).get(childPosition);
            if (convertView == null) {
                convertView = new TextView(context);
                ((TextView) convertView).setTextColor(getResources().getColor(R.color.black_item_content));
                ((TextView) convertView).setTextSize(DensityUtil.dip2px(6));
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
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_response_module;
    }

    @Override
    protected void initView() {
        expend_lv = (ExpandableListView) findViewById(R.id.expend_lv);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        hashMap.clear();
        hashMap.put("type", 0);
        new SoapAsyncTask() {

            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    MyToast.show(getString(R.string.connect_error));
                    return;
                }
                parseListResult(classifyBeanArrayList, ModuleClassifyBean.class, s);
                if (classifyBeanArrayList.size() > 0) {
                    requestTemp(0);
                } else {
                    MyToast.show(getString(R.string.no_more_data));
                }
            }

            private void requestTemp(final int index) {
                if (index >= classifyBeanArrayList.size()) {
                    ExpandableListAdapter adapter = new MyExpandableListAdapter();
                    expend_lv.setAdapter(adapter);
                    if (classifyBeanArrayList.size() > 0)
                        expend_lv.expandGroup(0);
                    return;
                }
                hashMap.clear();
                hashMap.put("classify_id", classifyBeanArrayList.get(index).classify_id);
                new SoapAsyncTask() {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s == null) {
                            requestTemp(index + 1);
                            return;
                        }
                        ArrayList<ModuleTemplateBean> tempList = new ArrayList<>();
                        parseListResult(tempList, ModuleTemplateBean.class, s);
                        items.add(tempList);
                        requestTemp(index + 1);
                    }
                }.execute("Get_Tmp_Content", hashMap);
            }
        }.execute("Get_Tmp_Classify", hashMap);
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
