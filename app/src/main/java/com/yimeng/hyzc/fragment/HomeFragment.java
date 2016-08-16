package com.yimeng.hyzc.fragment;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.BookingActivity;
import com.yimeng.hyzc.activity.WebViewActivity;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.view.AutoRollViewPager;
import com.yimeng.hyzc.view.LazyViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 首页fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, AutoRollViewPager.OnItemClickListener {

    private AutoRollViewPager viewPager;

    private ArrayList<String> imgUrls = new ArrayList<>();
    private ArrayList<String> backUrls = new ArrayList<>();
    private ArrayList<String> imgTitles = new ArrayList<>();
    private LinearLayout ll_booking;
    private LinearLayout ll_chat;
    private LinearLayout ll_health;
    private final String url = "http://www.hyzczg.com/plugins/advert/advert_js.ashx?id=1";
    private LinearLayout ll_points;
    private TextView tv_img_title;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    protected void initView(View view) {
        viewPager = (AutoRollViewPager) view.findViewById(R.id.vp);
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
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
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
                    tv_img_title.setText(imgTitles.get(0));
                    initDots();
                    viewPager.setData(imgUrls);
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
                MyToast.show(String.format("%s%s", getString(R.string.chat_online), getString(R.string.fun_undo)));// 在线咨询
                break;
            case R.id.ll_booking:
                startActivity(new Intent(getActivity(), BookingActivity.class));// 预约挂号
                break;
            case R.id.ll_health:
                startActivity(new Intent(getActivity(), WebViewActivity.class));// 健康教育
                break;
        }
    }

    @Override
    public void onItemClick(int index) {
        String url = backUrls.get(index);
        String pattern =  "^((https|http|ftp|rtsp|mms)?://)"
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
