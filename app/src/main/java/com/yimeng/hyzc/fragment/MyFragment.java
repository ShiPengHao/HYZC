package com.yimeng.hyzc.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.activity.AccountInfoActivity;
import com.yimeng.hyzc.activity.BookingActivity;
import com.yimeng.hyzc.adapter.AppointmentAdapter;
import com.yimeng.hyzc.bean.AppointmentBean;
import com.yimeng.hyzc.utils.BitmapUtils;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.MyConstant;
import com.yimeng.hyzc.utils.MyToast;
import com.yimeng.hyzc.utils.UiUtils;
import com.yimeng.hyzc.utils.WebServiceUtils;
import com.yimeng.hyzc.view.PullDownToRefreshListView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的对应fragment
 */
public class MyFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, PullDownToRefreshListView.OnRefreshListener {

    private RelativeLayout rl_appointment_history;
    private RelativeLayout rl_booking;
    private ImageView iv_appoint_arrow;
    private PullDownToRefreshListView listView;
    private boolean listViewToogle;
    private RelativeLayout rl_account_info;


    private List<AppointmentBean> data = new ArrayList<>();
    private AppointmentAdapter appointmentAdapter;
    private int itemsCount;
    private final int itemsPerPage = 20;
    private TextView tv_title;
    private PopupWindow popupWindow;
    private View popView;
    private TextView tv_description;
    private TextView tv_patient_name;
    private TextView tv_patient_sex;
    private TextView tv_patient_age;
    private TextView tv_patient_phone;
    private TextView tv_response;
    private TextView tv_response_time;
    private TextView tv_appointmentTime;
    private TextView tv_appointmentId;
    private TextView tv_doctor;
    private TextView tv_appointStatus;
    private TextView tv_response_way;
    private String patientId;
    private HashMap<String, Object> params = new HashMap<>();
    private TextView tv_appointment_add_time;
    private ImageView iv_back;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view) {
        rl_account_info = (RelativeLayout) view.findViewById(R.id.rl_account_info);
        rl_appointment_history = (RelativeLayout) view.findViewById(R.id.rl_appointment_history);
        rl_booking = (RelativeLayout) view.findViewById(R.id.rl_booking);

        listView = (PullDownToRefreshListView) view.findViewById(R.id.lv);
        iv_appoint_arrow = (ImageView) view.findViewById(R.id.iv_appoint_arrow);

        tv_title = (TextView) view.findViewById(R.id.tv_title);
    }

    @Override
    protected void setListener() {
        listViewToogle = true;
        switchAppointmentListDisplay();
        rl_account_info.setOnClickListener(this);
        rl_appointment_history.setOnClickListener(this);
        rl_booking.setOnClickListener(this);

        appointmentAdapter = new AppointmentAdapter(data);
        listView.setAdapter(appointmentAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        patientId = context.getSharedPreferences(MyConstant.PREFS_ACCOUNT, Context.MODE_PRIVATE).getString(MyConstant.KEY_ACCOUNT_LAST_ID, "");
        data.clear();
        itemsCount = 0;
        requestPersonalInfo();
        requestAppointmentList();
    }

    /**
     * 请求个人信息
     */
    private void requestPersonalInfo() {
    }

    /**
     * 请求预约列表
     */
    private void requestAppointmentList() {

        if (TextUtils.isEmpty(patientId)) {
            return;
        }
        params.clear();
        params.put("where", "patient_id=" + patientId);
        params.put("orderby", "add_time desc");
        if (listView.isRefreshing()) {
            params.put("startIndex", 1);
            params.put("endIndex", itemsCount);
        } else {
            params.put("startIndex", itemsCount + 1);
            params.put("endIndex", itemsCount + itemsPerPage);
        }
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Load_AppointmentData",
                            (Map<String, Object>) params[0]);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result == null) {
                    MyToast.show(getString(R.string.connet_error));
                    if (listView.isRefreshing()) {
                        listView.refreshCompleted(false);
                    } else if (listView.isLoadingMore()) {
                        listView.hideFooter();
                    }
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    ArrayList<AppointmentBean> tempData = new Gson().fromJson(object.optString("data"), new TypeToken<ArrayList<AppointmentBean>>() {
                    }.getType());
                    if (tempData.size() == 0) {
                        MyToast.show(getString(R.string.no_more_data));
                    }
                    if (listView.isRefreshing()) {
                        data.clear();
                    }
                    data.addAll(tempData);
                    itemsCount = data.size();
                    appointmentAdapter.notifyDataSetChanged();
                    if (listView.isRefreshing()) {
                        listView.refreshCompleted(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listView.isRefreshing()) {
                        listView.refreshCompleted(false);
                    }
                } finally {
                    if (listView.isLoadingMore()) {
                        listView.hideFooter();
                    }
                }
            }
        }.execute(params);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_appointment_history:
                switchAppointmentListDisplay();
                break;
            case R.id.rl_account_info:
                startActivity(new Intent(getActivity(), AccountInfoActivity.class));
                break;
            case R.id.iv_back:
                dismissPopWindow();
                break;
            case R.id.rl_booking:
                startActivity(new Intent(getActivity(), BookingActivity.class));
                break;
        }
    }


    /**
     * 切换预约历史的展示与否
     */
    private void switchAppointmentListDisplay() {
        if (listViewToogle) {
            listView.setVisibility(View.VISIBLE);
            iv_appoint_arrow.setImageBitmap(BitmapUtils.getResImg(getActivity(),R.mipmap.next));
        } else {
            listView.setVisibility(View.INVISIBLE);
            iv_appoint_arrow.setImageBitmap(BitmapUtils.getResImg(getActivity(),R.mipmap.arrow_down));
        }
        listViewToogle = !listViewToogle;
        
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        requestAppointmentDetail(position - listView.getHeaderViewsCount());
    }

    /**
     * 请求预约详情
     */
    private void requestAppointmentDetail(int position) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("appointment_id", data.get(position).appointment_id);
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null) {
                    return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, "Load_patient_detail",
                            (Map<String, Object>) params[0]);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result == null) {
                    MyToast.show(getString(R.string.connet_error));
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("total") == 1) {
                        ArrayList<AppointmentBean> tempData = new Gson().fromJson(object.optString("data"),
                                new TypeToken<ArrayList<AppointmentBean>>() {
                                }.getType());
                        if (tempData.size() > 0) {
                            showAppointmentDetailPopWindow(tempData.get(0));
                        } else {
                            MyToast.show(context.getString(R.string.connet_error));
                        }
                    } else {
                        MyToast.show(context.getString(R.string.connet_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(params);
    }

    /**
     * 显示预约详情的PopWindow
     *
     * @param bean 预约详情数据
     */
    private void showAppointmentDetailPopWindow(AppointmentBean bean) {
        initPopView();
        bindPopView(bean);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(DensityUtil.SCREEN_WIDTH, DensityUtil.SCREEN_HEIGHT);
            popupWindow.setContentView(popView);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } else if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        int[] location = new int[2];
        tv_title.getLocationInWindow(location);
        popupWindow.showAtLocation(listView, Gravity.NO_GRAVITY, location[0], location[1]);
    }

    /**
     * 初始化PopWindow
     */
    private void initPopView() {
        if (popView == null) {
            popView = UiUtils.inflate(R.layout.popwindow_appointment_detail);
            tv_description = (TextView) popView.findViewById(R.id.tv_description);
            tv_patient_name = (TextView) popView.findViewById(R.id.tv_name);
            tv_patient_sex = (TextView) popView.findViewById(R.id.tv_sex);
            tv_patient_age = (TextView) popView.findViewById(R.id.tv_age);
            tv_patient_phone = (TextView) popView.findViewById(R.id.tv_phone);
            tv_response = (TextView) popView.findViewById(R.id.tv_response);
            tv_response_time = (TextView) popView.findViewById(R.id.tv_response_time);
            tv_appointmentTime = (TextView) popView.findViewById(R.id.tv_appointmentTime);
            tv_appointmentId = (TextView) popView.findViewById(R.id.tv_appointmentId);
            tv_doctor = (TextView) popView.findViewById(R.id.tv_doctor);
            tv_appointStatus = (TextView) popView.findViewById(R.id.tv_appointStatus);
            tv_response_way = (TextView) popView.findViewById(R.id.tv_response_way);
            tv_appointment_add_time = (TextView) popView.findViewById(R.id.tv_appointment_add_time);
            iv_back = (ImageView) popView.findViewById(R.id.iv_back);
            iv_back.setOnClickListener(this);
        }
    }

    /**
     * 为医生详情界面绑定数据
     */
    private void bindPopView(AppointmentBean bean) {
        tv_appointmentId.setText(String.format("%s：%s", getString(R.string.appointment_id), bean.appointment_id));
        tv_description.setText(String.format("%s：%s", getString(R.string.disease_description), bean.disease_description == null ? "" : bean.disease_description));
        tv_doctor.setText(String.format("%s：%s", getString(R.string.doctor), bean.doctor_name == null ? "" : bean.doctor_name));
        tv_response.setText(String.format("%s：%s", getString(R.string.doctor_response), bean.doctor_Responses == null ? "" : bean.doctor_Responses));
        try {
            String date = bean.registration_time.substring(bean.registration_time.indexOf("(") + 1, bean.registration_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(date)));
            tv_appointmentTime.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.appointment_time),
                    date));
        } catch (Exception e) {
            tv_appointmentTime.setText(MyApp.getAppContext().getString(R.string.appointment_time));
        }
        try {
            String date = bean.add_time.substring(bean.add_time.indexOf("(") + 1, bean.add_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date)));
            tv_appointment_add_time.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.appointment_add_time),
                    date));
        } catch (Exception e) {
            tv_appointment_add_time.setText(MyApp.getAppContext().getString(R.string.appointment_add_time));
        }
        try {
            String date = bean.doctor_Responses_time.substring(bean.doctor_Responses_time.indexOf("(") + 1, bean.doctor_Responses_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date)));
            tv_response_time.setText(String.format("%s：%s", MyApp.getAppContext().getString(R.string.doctor_response_time),
                    date));
        } catch (Exception e) {
            tv_response_time.setText(MyApp.getAppContext().getString(R.string.doctor_response_time));
        }
        try {
            String birth = bean.patient_age.substring(bean.patient_age.indexOf("(") + 1, bean.patient_age.indexOf(")"));
            tv_patient_age.setText(String.format("%s：%s", getString(R.string.age),
                    new Date().getYear() - new Date(Long.parseLong(birth)).getYear()));
        } catch (Exception e) {
            tv_patient_age.setText(getString(R.string.age));
        }
        tv_patient_name.setText(String.format("%s：%s", getString(R.string.username), bean.patient_name == null ? "" : bean.patient_name));
        tv_response_way.setText(String.format("%s：%s", getString(R.string.doctor_response_way), bean.doctor_Way == null ? "" : bean.doctor_Way));
        tv_patient_phone.setText(String.format("%s：%s", getString(R.string.phone), bean.patient_phone == null ? "" : bean.patient_phone));
        tv_patient_sex.setText(String.format("%s：%s", getString(R.string.sex),
                bean.patient_sex == 1 ? getString(R.string.male) : getString(R.string.female)));
        if (bean.doctor_dispose == 0) {
            tv_appointStatus.setText(String.format("%s：%s", getString(R.string.appointment_status), getString(R.string.no_response)));
            tv_appointStatus.setTextColor(Color.RED);
        } else {
            tv_appointStatus.setText(String.format("%s：%s", getString(R.string.appointment_status), getString(R.string.has_response)));
            tv_appointStatus.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }


    @Override
    public void onDestroy() {
        dismissPopWindow();
        super.onDestroy();
    }

    /**
     * 让PopWindow消失，防止内存泄漏
     */
    private void dismissPopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }


    @Override
    public void onPullToRefresh() {
        requestAppointmentList();
    }

    @Override
    public void onLoadMore() {
        requestAppointmentList();
    }
}
