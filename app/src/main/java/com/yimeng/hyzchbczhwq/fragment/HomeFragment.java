package com.yimeng.hyzchbczhwq.fragment;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.DoctorListActivity;
import com.yimeng.hyzchbczhwq.activity.WebViewActivity;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.UiUtils;
import com.yimeng.hyzchbczhwq.view.CycleViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 病人首页fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, CycleViewPager.OnItemClickListener {

    private CycleViewPager viewPager;
    /**
     * 轮播图图片url集合
     */
    private ArrayList<String> imgUrls = new ArrayList<>();
    /**
     * 轮播图图片对应链接url集合
     */
    private ArrayList<String> backUrls = new ArrayList<>();
    /**
     * 轮播图图片标题集合
     */
    private ArrayList<String> imgTitles = new ArrayList<>();
    private LinearLayout ll_booking;
    private LinearLayout ll_chat;
    private LinearLayout ll_health;
    private final String url = "http://www.hyzczg.com/plugins/advert/advert_js.ashx?id=1";
    private LinearLayout ll_points;
    private TextView tv_img_title;
    private PagerAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    protected void initView(View view) {
        viewPager = (CycleViewPager) view.findViewById(R.id.vp);
        ll_booking = (LinearLayout) view.findViewById(R.id.ll_booking);
        ll_chat = (LinearLayout) view.findViewById(R.id.ll_chat);
        ll_health = (LinearLayout) view.findViewById(R.id.ll_health);
        ll_points = (LinearLayout) view.findViewById(R.id.ll_points);
        tv_img_title = (TextView) view.findViewById(R.id.tv_img_title);
    }

    @Override
    protected void setListener() {
        ll_health.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        ll_booking.setOnClickListener(this);
        viewPager.setOnItemClickListener(this);
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return imgUrls.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = (ImageView) UiUtils.inflate(R.layout.layout_imageview);
                Picasso.with(getContext())
                        .load(imgUrls.get(position))
                        .resize(viewPager.getWidth(), viewPager.getHeight())
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(imageView);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tv_img_title.setText(imgTitles.get(position));
                for (int i = 0; i < ll_points.getChildCount(); i++) {
                    if (i == position) {
                        ll_points.getChildAt(i).setEnabled(false);
                    } else {
                        ll_points.getChildAt(i).setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.startRoll();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.stopRoll();
    }

    @Override
    protected void initData() {
        imgUrls.clear();
        backUrls.clear();
        imgTitles.clear();
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {

            }

            @Override
            public void onResponse(String s, int code) {
                if (null == s) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONObject(s).optJSONArray("advert");
                    JSONObject obj;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        obj = jsonArray.optJSONObject(i);
                        if (obj != null) {
                            imgUrls.add(obj.optString("file_path"));
                            backUrls.add(obj.optString("url"));
                            imgTitles.add(obj.optString("title"));
                        }
                    }
                    if (imgUrls.size() > 0) {
                        tv_img_title.setText(imgTitles.get(0));
                        initDots();
                        viewPager.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 初始化页码指示器
     */
    private void initDots() {
        ll_points.removeAllViews();
        for (int i = 0; i < imgUrls.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.selector_dot);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                imageView.setEnabled(false);
            } else {
                layoutParams.leftMargin = DensityUtil.dip2px(15);
            }
            ll_points.addView(imageView, layoutParams);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_chat:
                startActivity(new Intent(getActivity(), DoctorListActivity.class)
                        .putExtra(DoctorListActivity.EXTRA_CHAT_OR_BOOKING, DoctorListActivity.EXTRA_CHAT));// 在线咨询
                break;
            case R.id.ll_booking:
                startActivity(new Intent(getActivity(), DoctorListActivity.class)
                        .putExtra(DoctorListActivity.EXTRA_CHAT_OR_BOOKING, DoctorListActivity.EXTRA_BOOKING));// 预约挂号
                break;
            case R.id.ll_health:
                startActivity(new Intent(getActivity(), WebViewActivity.class));// 健康教育
                break;
        }
    }

    @Override
    public void onItemClick(int index) {
        String url = backUrls.get(index);
        String pattern = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-zA-Z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]\\." // 二级域名
                + "[a-zA-Z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|"
                + "(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        if (!TextUtils.isEmpty(url) && url.matches(pattern)) {
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", url));
        }
    }
}
