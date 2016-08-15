package com.yimeng.hyzc.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.BookingActivity;
import com.yimeng.hyzc.activity.WebViewActivity;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.view.AutoRollViewPager;
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
    private LinearLayout ll_booking;
    private LinearLayout ll_chat;
    private LinearLayout ll_health;
    private final String url = "http://www.hyzczg.com/plugins/advert/advert_js.ashx?id=1";

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    protected void initView(View view) {
        viewPager = (AutoRollViewPager) view.findViewById(R.id.vp);
        ll_booking = (LinearLayout) view.findViewById(R.id.ll_booking);
        ll_chat = (LinearLayout) view.findViewById(R.id.ll_chat);
        ll_health = (LinearLayout) view.findViewById(R.id.ll_health);
    }

    @Override
    protected void setListener() {
        ll_health.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        ll_booking.setOnClickListener(this);
        viewPager.setOnItemClickListener(this);
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
                        }
                    }
                    viewPager.setData(imgUrls);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
