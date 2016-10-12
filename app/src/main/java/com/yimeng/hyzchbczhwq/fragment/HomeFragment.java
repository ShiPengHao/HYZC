package com.yimeng.hyzchbczhwq.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.activity.AddressChoiceActivity;
import com.yimeng.hyzchbczhwq.activity.BaseActivity;
import com.yimeng.hyzchbczhwq.activity.DepartmentChoiceActivity;
import com.yimeng.hyzchbczhwq.activity.DoctorListActivity;
import com.yimeng.hyzchbczhwq.activity.WebViewActivity;
import com.yimeng.hyzchbczhwq.adapter.DefaultAdapter;
import com.yimeng.hyzchbczhwq.bean.DecorateImgBean;
import com.yimeng.hyzchbczhwq.bean.HospitalBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.holder.NewsHolder;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.JsonUtils;
import com.yimeng.hyzchbczhwq.utils.LocationUtils;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.MyToast;
import com.yimeng.hyzchbczhwq.utils.UiUtils;
import com.yimeng.hyzchbczhwq.view.CycleViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 病人首页fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, CycleViewPager.OnItemClickListener, LocationUtils.UpdateLocationListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private CycleViewPager viewPager;
    /**
     * 轮播图对应条目集合
     */
    private ArrayList<DecorateImgBean> bannerImgList = new ArrayList<>();
    /**
     * 资讯对应条目集合
     */
    private ArrayList<DecorateImgBean> newsImgList = new ArrayList<>();
    private LinearLayout ll_booking;
    private LinearLayout ll_chat;
    private LinearLayout ll_health;
    public static final int REQUEST_CODE_FOR_CITY = 200;
    private LinearLayout ll_points;
    private TextView tv_img_title;
    private PagerAdapter pagerAdapter;
    private ArrayList<HospitalBean> hospital = new ArrayList<>();
    private static final String IMG_TYPE_LBT = "LBT";
    private static final String IMG_TYPE_NEWS = "NEWS";

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
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DefaultAdapter<DecorateImgBean> listAdapter;

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
        listView = (ListView)view.findViewById(R.id.lv);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
    }

    @Override
    protected void setListener() {
        ll_health.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        ll_booking.setOnClickListener(this);
        ll_location.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        LocationUtils.setUpdateLocationListener(this);
        viewPager.setOnItemClickListener(this);
        listView.setOnItemClickListener(this);
        listAdapter = new DefaultAdapter<DecorateImgBean>(newsImgList) {
            @Override
            protected BaseHolder getHolder() {
                return new NewsHolder();
            }
        };
        listView.setAdapter(listAdapter);
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return bannerImgList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = (ImageView) UiUtils.inflate(R.layout.layout_imageview);
                Picasso.with(getContext())
                        .load(MyConstant.NAMESPACE + bannerImgList.get(position).decorate_img)
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
                tv_img_title.setText(bannerImgList.get(position).decorate_name);
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
        requestImg(IMG_TYPE_LBT);
        requestImg(IMG_TYPE_NEWS);
    }

    /**
     * 请求轮播图或者资讯图片
     *
     * @param imgType 轮播图或者资讯图片
     */
    private void requestImg(final String imgType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("key", imgType);
        new BaseActivity.SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (null == s) {
                    return;
                }
                try {
                    ArrayList<DecorateImgBean> tempList = new ArrayList<>();
                    JsonUtils.parseListResult(tempList, DecorateImgBean.class, s);
                    if (IMG_TYPE_LBT.equalsIgnoreCase(imgType)) {
                        bannerImgList.clear();
                        bannerImgList.addAll(tempList);
                        if (bannerImgList.size() > 0) {
                            tv_img_title.setText(bannerImgList.get(0).decorate_name);
                            initDots();
                            viewPager.setAdapter(pagerAdapter);
                        }
                    } else if (IMG_TYPE_NEWS.equalsIgnoreCase(imgType)) {
                        newsImgList.clear();
                        newsImgList.addAll(tempList);
                        listAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("get_APP_Decorate", hashMap);
    }


    /**
     * 初始化页码指示器
     */
    private void initDots() {
        ll_points.removeAllViews();
        for (int i = 0; i < bannerImgList.size(); i++) {
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
    /**
     * 轮播图条目被点击回调
     */
    public void onItemClick(int index) {
        String url = bannerImgList.get(index).decorate_value;
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

    @Override
    public void onRefresh() {
        requestImg(IMG_TYPE_NEWS);
    }

    @Override
    /**
     * 新闻列表ListView条目被点击回调
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = newsImgList.get(position).decorate_value;
        if (!TextUtils.isEmpty(url) && url.matches(URL_PATTERN)) {
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", url));
        }
    }
}
