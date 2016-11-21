package com.yimeng.hyzchbczhwq.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;
import com.squareup.picasso.Picasso;
import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.DefaultAdapter;
import com.yimeng.hyzchbczhwq.adapter.DoctorAdapter;
import com.yimeng.hyzchbczhwq.bean.DecorateImgBean;
import com.yimeng.hyzchbczhwq.bean.DoctorBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.holder.NewsHolder;
import com.yimeng.hyzchbczhwq.huanxin.ChatActivity;
import com.yimeng.hyzchbczhwq.utils.DensityUtil;
import com.yimeng.hyzchbczhwq.utils.JsonUtils;
import com.yimeng.hyzchbczhwq.utils.MyConstant;
import com.yimeng.hyzchbczhwq.utils.UiUtils;
import com.yimeng.hyzchbczhwq.view.CycleViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 科室activity
 */
public class DepartmentActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, CycleViewPager.OnItemClickListener {
    public static final String EXTRA_CHAT_OR_BOOKING = "EXTRA_CHAT_OR_BOOKING";
    public static final int EXTRA_CHAT = 1;
    public static final int EXTRA_BOOKING = 2;

    private int chatOrBooking = EXTRA_BOOKING;

    private ArrayList<DoctorBean> doctor = new ArrayList<>();
    private ArrayList<DoctorBean> doctorAll = new ArrayList<>();

    private DoctorAdapter doctorAdapter;

    private ImageView iv_back;
    private ListView listView;
    private TextView tv_tip;
    private TextView tv_title;
    private String departments_id;
    private TextView tv_department;
    private TextView tv_schedule;
    private AlertDialog scheduleDialog;

    private String[] days = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    private CycleViewPager viewPager;
    /**
     * 轮播图对应条目集合
     */
    private ArrayList<DecorateImgBean> bannerImgList = new ArrayList<>();
    private PagerAdapter pagerAdapter;
    private static final String IMG_TYPE_LBT = "LBT";
    private static final String IMG_TYPE_NEWS = "WZ";
    private LinearLayout ll_points;
    private TextView tv_img_title;
    private ImageView iv_mask;

    /**
     * 资讯对应条目集合
     */
    private ArrayList<DecorateImgBean> newsImgList = new ArrayList<>();
    private DefaultAdapter<DecorateImgBean> listAdapter;
    private ListView lv_news;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_department;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        listView = (ListView) findViewById(R.id.lv);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_schedule = (TextView) findViewById(R.id.tv_schedule);
        tv_department = (TextView) findViewById(R.id.tv_department);
        tv_tip.setVisibility(View.GONE);

        viewPager = (CycleViewPager) findViewById(R.id.vp);
        ll_points = (LinearLayout) findViewById(R.id.ll_points);
        tv_img_title = (TextView) findViewById(R.id.tv_img_title);
        iv_mask = (ImageView) findViewById(R.id.iv_mask);

        lv_news = (ListView) findViewById(R.id.lv_news);
    }

    @Override
    protected void setListener() {
        doctorAdapter = new DoctorAdapter(doctor);
        listView.setAdapter(doctorAdapter);
        listView.setOnItemClickListener(this);
        iv_back.setOnClickListener(this);
        tv_schedule.setOnClickListener(this);

        pagerAdapter = new PagerAdapter() {

            private int[] bannerPlaceHolder = new int[]{R.drawable.banner_mask1, R.drawable.banner_mask2, R.drawable.banner_mask3};

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
                Picasso.with(DepartmentActivity.this)
                        .load(MyConstant.NAMESPACE + bannerImgList.get(position).decorate_img)
                        .resize(viewPager.getWidth(), viewPager.getHeight())
                        .placeholder(bannerPlaceHolder[position % 3])
                        .error(bannerPlaceHolder[position % 3])
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
                if (null == bannerImgList || bannerImgList.size() <= 1)
                    return;
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

        viewPager.setOnItemClickListener(this);
        lv_news.setOnItemClickListener(this);
        listAdapter = new DefaultAdapter<DecorateImgBean>(newsImgList) {
            @Override
            protected BaseHolder getHolder() {
                return new NewsHolder();
            }
        };
        lv_news.setAdapter(listAdapter);
    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;
        try {
            Intent intent = getIntent();
            chatOrBooking = intent.getIntExtra(EXTRA_CHAT_OR_BOOKING, EXTRA_BOOKING);
            String departments_name = intent.getStringExtra("departments_name");
            String hospital_name = intent.getStringExtra("hospital_name");
            if (!TextUtils.isEmpty(departments_name) && !TextUtils.isEmpty(hospital_name))
                tv_department.setText(String.format("%s%s", hospital_name, departments_name));
            tv_department.setFocusable(true);
            tv_department.setFocusableInTouchMode(true);
            departments_id = intent.getStringExtra("departments_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (chatOrBooking) {
            case EXTRA_CHAT:
                tv_title.setText(getString(R.string.chat_online));
                break;
            case EXTRA_BOOKING:
                tv_title.setText(getString(R.string.booking));
                break;
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("departments_id", departments_id);
        hashMap.put("week", dayOfWeek);
        requestDoctor("Load_Doctor", hashMap);
        requestAllDoctor();
        requestImg(IMG_TYPE_LBT);
        requestImg(IMG_TYPE_NEWS);
    }

    /**
     * 根据科室请求医生信息
     */
    public void requestAllDoctor() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("departments_id", departments_id);
        hashMap.put("week", 0);
        new SoapAsyncTask() {
            protected void onPostExecute(String result) {
                if (result != null) {
                    parseListResult(doctorAll, DoctorBean.class, result);
                }
            }
        }.execute("Load_Doctor", hashMap);
    }

    /**
     * 根据科室请求医生信息
     *
     * @param params 方法名+参数列表
     */
    public void requestDoctor(Object... params) {
        new SoapAsyncTask() {
            protected void onPostExecute(String result) {
                if (result != null) {
                    parseListResult(doctor, DoctorBean.class, result);
                    Collections.sort(doctor);
                    doctorAdapter.notifyDataSetChanged();
                    if (doctor.size() > 0) {
                        listView.setVisibility(View.VISIBLE);
                        tv_tip.setVisibility(View.GONE);
                    } else {
                        listView.setVisibility(View.GONE);
                        tv_tip.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute(params);
    }

    /**
     * 请求轮播图或者资讯图片
     *
     * @param imgType 轮播图或者资讯图片
     */
    private void requestImg(final String imgType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("key", imgType);
        hashMap.put("departments_id", departments_id);
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
                            iv_mask.setVisibility(View.GONE);
                        } else {
                            iv_mask.setVisibility(View.VISIBLE);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv:
                DoctorBean doctorBean = doctor.get(position);
                switch (chatOrBooking) {
                    case EXTRA_BOOKING:
                        startActivity(new Intent(this, DoctorDetailActivity.class).putExtra("doctor", doctorBean));
                        break;
                    case EXTRA_CHAT:
                        startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, "doctor_" + doctorBean.doctor_id));
                        break;
                }
                break;
            case R.id.lv_news:
                String url = newsImgList.get(position).decorate_value;
                if (!TextUtils.isEmpty(url) && url.matches(MyConstant.URL_PATTERN)) {
                    startActivity(new Intent(this, WebViewActivity.class).putExtra("url", url));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_schedule:
                showScheduleWindow();
                break;
        }
    }


    /**
     * 显示科室排班表窗口
     */
    private void showScheduleWindow() {
        if (scheduleDialog == null) {
            scheduleDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.doctor_duty_list)
                    .setView(initScheduleView(), DensityUtil.dip2px(10), DensityUtil.dip2px(10), DensityUtil.dip2px(10), DensityUtil.dip2px(10))
                    .create();
        }
        scheduleDialog.show();
    }

    /**
     * 创建排班表视图
     *
     * @return 返回一个view用来构建视图
     */
    private View initScheduleView() {
        final GridView gridView = (GridView) UiUtils.inflate(R.layout.layout_schedule);
        BaseAdapter gridViewAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return (doctorAll.size() + 1) * 8;
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
            public View getView(final int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = UiUtils.inflate(R.layout.item_text_schedule);
                    TextView textView = (TextView) convertView.findViewById(R.id.tv);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int doctorIndex = position / 8 - 1;
                            if (doctorIndex >= 0) {
                                dismissDialog();
                                startActivity(new Intent(DepartmentActivity.this, DoctorDetailActivity.class).putExtra("doctor", doctorAll.get(doctorIndex)));
                            }
                        }
                    });
                    convertView.setTag(textView);
                }
                TextView textView = (TextView) convertView.getTag();
                if (position == 0)
                    textView.setText("");
                else if (position < 8)
                    textView.setText(days[position - 1]);
                else if (position % 8 == 0)
                    textView.setText(doctorAll.get(position / 8 - 1).doctor_name);
                else if (doctorAll.get(position / 8 - 1).Is_Order == 1 || doctorAll.get(position / 8 - 1).week.contains(String.valueOf(position % 8))) {
                    textView.setText("班");
                } else
                    textView.setText("");

                return convertView;
            }
        };
        gridView.setAdapter(gridViewAdapter);
        return gridView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pagerAdapter.getCount() > 1)
            viewPager.startRoll();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.stopRoll();
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    private void dismissDialog() {
        if (scheduleDialog != null && scheduleDialog.isShowing())
            scheduleDialog.dismiss();
    }

    /**
     * 初始化页码指示器
     */
    private void initDots() {
        ll_points.removeAllViews();
        if (null == bannerImgList || bannerImgList.size() < 2)
            return;
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

    @Override
    /**
     * 轮播图条目被点击回调
     */
    public void onItemClick(int index) {
        if (index < bannerImgList.size() && index >= 0) {
            String url = bannerImgList.get(index).decorate_value;
            if (!TextUtils.isEmpty(url) && url.matches(MyConstant.URL_PATTERN)) {
                startActivity(new Intent(this, WebViewActivity.class).putExtra("url", url));
            }
        }
    }

}
