package com.yimeng.hyzchbczhwq.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
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
import com.yimeng.hyzchbczhwq.activity.AddressChoiceActivity;
import com.yimeng.hyzchbczhwq.activity.BaseActivity;
import com.yimeng.hyzchbczhwq.activity.DepartmentChoiceActivity;
import com.yimeng.hyzchbczhwq.activity.DoctorListActivity;
import com.yimeng.hyzchbczhwq.activity.WebViewActivity;
import com.yimeng.hyzchbczhwq.bean.HospitalBean;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.JsonUtils;
import com.yimeng.hyzchbczhwq.utils.LocationUtils;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.UiUtils;
import com.yimeng.hyzchbczhwq.view.CycleViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;



/**
 * 病人首页fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, CycleViewPager.OnItemClickListener, LocationUtils.UpdateLocationListener {

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
    private final String BANNER_URL = "http://www.hyzczg.com/plugins/advert/advert_js.ashx?id=1";
    public static final int REQUEST_CODE_FOR_CITY = 200;
    private LinearLayout ll_points;
    private TextView tv_img_title;
    private PagerAdapter adapter;
    private ArrayList<HospitalBean> hospital = new ArrayList<>();

    private final String URL_PATTERN =
            "^((https|http|ftp|rtsp|mms)?://)"//ftp的user@
                    // IP形式的URL- 199.194.52.184
                    // 允许IP和DOMAIN（域名）
                    // 域名- www.
                    // 二级域名
                    // first level domain- .com or .museum
                    // 端口- :80
                    + "?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?" //ftp的user@
                    + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                    + "|" // 允许IP和DOMAIN（域名）
                    + "([0-9a-zA-Z_!~*'()-]+\\.)*" // 域名- www.
                    + "([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]\\." // 二级域名
                    + "[a-zA-Z]{2,6})" // first level domain- .com or .museum
                    + "(:[0-9]{1,4})?" // 端口- :80
                    + "((/?)|"
                    + "(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";
    private TextView tv_location;
    private LinearLayout ll_location;
    private String locationCity;
    private ImageView iv_location;
    private String cityName;

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
        ll_location = (LinearLayout) view.findViewById(R.id.ll_location);
        tv_img_title = (TextView) view.findViewById(R.id.tv_img_title);
        tv_location = (TextView) view.findViewById(R.id.tv_location);
        iv_location = (ImageView) view.findViewById(R.id.iv_location);
    }

    @Override
    protected void setListener() {
        ll_health.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        ll_booking.setOnClickListener(this);
        ll_location.setOnClickListener(this);
        LocationUtils.setUpdateLocationListener(this);
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
        if (locationCity != null) {
            tv_location.setText(locationCity);
        }
        imgUrls.clear();
        backUrls.clear();
        imgTitles.clear();
        OkHttpUtils.get().url(BANNER_URL).build().execute(new StringCallback() {
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

    /**
     * 验证城市名字
     *
     * @return 非空
     */
    private boolean checkCityNameAndHospital() {
        if (TextUtils.isEmpty(cityName)) {
            MyToast.show("请选择您所在的城市");
            ObjectAnimator.ofFloat(ll_location, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return false;
        }
        if (hospital == null || hospital.size() == 0) {
            ObjectAnimator.ofFloat(ll_location, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            MyToast.show("该地区没有互联网医院，欢迎您联系\"华医之春\"合作");
            return false;
        }
        return true;
    }

    /**
     * 获得本市医院
     */
    private void requestHospital(String cityName) {
        if (TextUtils.isEmpty(cityName))
            return;
        HashMap<String, Object> params = new HashMap<>();
        params.put("cityname", cityName);
        new BaseActivity.SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (null == s)
                    return;
                try {
                    JsonUtils.parseListResult(hospital, HospitalBean.class, s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("get_City_Hospital", params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_chat:
                if (checkCityNameAndHospital())
                    startActivity(new Intent(getActivity(), DepartmentChoiceActivity.class)
                            .putExtra("hospital", hospital)
                            .putExtra(DoctorListActivity.EXTRA_CHAT_OR_BOOKING, DoctorListActivity.EXTRA_CHAT));// 在线咨询
                break;
            case R.id.ll_booking:
                if (checkCityNameAndHospital())
                    startActivity(new Intent(getActivity(), DepartmentChoiceActivity.class)
                            .putExtra("hospital", hospital)
                            .putExtra(DoctorListActivity.EXTRA_CHAT_OR_BOOKING, DoctorListActivity.EXTRA_BOOKING));// 预约挂号
                break;
            case R.id.ll_health:
                startActivity(new Intent(getActivity(), WebViewActivity.class));// 健康教育
                break;
            case R.id.ll_location:
                startActivityForResult(new Intent(getActivity(),
                        AddressChoiceActivity.class).putExtra(MyConstant.REQUEST_CODE, REQUEST_CODE_FOR_CITY), REQUEST_CODE_FOR_CITY);// 选择市
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_FOR_CITY:
                String city = data.getStringExtra("city");
                if (city == null) return;
                tv_location.setText(city);
                cityName = city;
                requestHospital(city);
                if (!city.equalsIgnoreCase(locationCity))
                    iv_location.setVisibility(View.GONE);
                else
                    iv_location.setVisibility(View.VISIBLE);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(int index) {
        String url = backUrls.get(index);
        if (!TextUtils.isEmpty(url) && url.matches(URL_PATTERN)) {
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", url));
        }
    }

    @Override
    public void updateWithNewLocation(Location location) {
        new AsyncTask<Location, Void, Void>() {
            @Override
            protected Void doInBackground(Location... params) {
                if (null == params || null == params[0]) {
                    return null;
                }
                Location location = params[0];
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                List<Address> addList;
                Geocoder ge = new Geocoder(MyApp.getAppContext());
                try {
                    addList = ge.getFromLocation(lat, lng, 1);
                    if (addList != null && addList.size() > 0) {
                        Address ad = addList.get(0);
                        cityName = ad.getLocality();
                        hospital.clear();
                        requestHospital(cityName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!TextUtils.isEmpty(cityName) && !cityName.equalsIgnoreCase(locationCity)) {
                    locationCity = cityName;
                    tv_location.setText(cityName);
                }
            }
        }.execute(location);
    }
}
