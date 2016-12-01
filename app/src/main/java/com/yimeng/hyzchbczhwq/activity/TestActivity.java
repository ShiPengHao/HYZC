package com.yimeng.hyzchbczhwq.activity;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.utils.Cheeses;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.UiUtils;

import java.util.HashMap;
import java.util.Random;


/**
 * 测试
 */

public class TestActivity extends BaseActivity {

    private ListView listView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void setListener() {
        listView.setAdapter(new BaseAdapter() {
            private int[] res = new int[]{R.drawable.banner_mask1, R.drawable.banner_mask2, R.drawable.banner_mask3};
            private int[] picNums = new int[]{
                    new Random().nextInt(5) + 3,
                    new Random().nextInt(5) + 3,
                    new Random().nextInt(5) + 3,
                    new Random().nextInt(5) + 3,
                    new Random().nextInt(5) + 3,
                    new Random().nextInt(5) + 3,
                    new Random().nextInt(5) + 3,
                    new Random().nextInt(5) + 3
            };

            @Override
            public int getCount() {
                return 22;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public int getItemViewType(int position) {
                return position % 3 == 0 ? 1 : 0;
            }

            @Override
            public int getViewTypeCount() {
                return super.getViewTypeCount() + 1;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                switch (getItemViewType(position)) {
                    case 0:
                        if (convertView == null)
                            convertView = UiUtils.inflate(android.R.layout.simple_list_item_1);
                        ((TextView) convertView).setText(Cheeses.NAMES[position]);
                        ((TextView) convertView).setTextColor(Color.BLACK);
                        break;
                    case 1:
                        if (convertView == null) {
                            convertView = UiUtils.inflate(R.layout.layout_scroll_item);
                            convertView.setTag(new ViewHolder(convertView));
                        }
                        ViewHolder holder = (ViewHolder) convertView.getTag();
                        holder.bindView(position / 3);
                        break;
                }
                return convertView;
            }

            class ItemViewHolder implements View.OnClickListener {
                ImageView imageView;
                TextView textView;
                View view;

                ItemViewHolder() {
                    view = UiUtils.inflate(R.layout.item_doctor);
                    imageView = (ImageView) view.findViewById(R.id.item_icon);
                    textView = (TextView) view.findViewById(R.id.item_name);
                    imageView.setOnClickListener(this);
                    textView.setOnClickListener(this);
                }

                @Override
                public void onClick(View v) {
                    MyToast.show(v.getClass().toString() + v.hashCode());
                }
            }

            class ViewHolder implements View.OnClickListener {

                private final LinearLayout ll;
                private final HorizontalScrollView sl;
                private HashMap<Integer, ItemViewHolder> itemHolders = new HashMap<>();
                private final ImageView imageView;
                private final TextView textView;


                public ViewHolder(View view) {
                    ll = (LinearLayout) view.findViewById(R.id.ll);
                    sl = (HorizontalScrollView) view.findViewById(R.id.sl);
                    textView = (TextView) view.findViewById(R.id.tv);
                    imageView = (ImageView) view.findViewById(R.id.iv);
                    imageView.setOnClickListener(this);
                    textView.setOnClickListener(this);
                }

                private void bindView(int picNumIndex) {
                    ll.removeAllViews();
                    ItemViewHolder itemViewHolder;
                    for (int i = 0; i < picNums[picNumIndex]; i++) {
                        itemViewHolder = itemHolders.get(i);
                        if (itemViewHolder == null) {
                            itemViewHolder = new ItemViewHolder();
                            itemHolders.put(i, itemViewHolder);
                        }
                        itemViewHolder.imageView.setImageResource(res[i % 3]);
                        itemViewHolder.textView.setText("num:" + i);
                        ll.addView(itemViewHolder.view);
                    }
                    sl.scrollTo(0, 0);
                }

                @Override
                public void onClick(View v) {
                    MyToast.show(v.getClass().toString() + v.hashCode());
                }

            }
        });
    }

    @Override
    protected void initData() {

    }
}
