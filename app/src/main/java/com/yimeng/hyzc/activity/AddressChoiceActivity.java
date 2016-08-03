package com.yimeng.hyzc.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.AddressBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 选择地址的activity
 */
public class AddressChoiceActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView iv_back;
    private ListView lv_province;
    private ListView lv_city;
    private ListView lv_area;
    private View view_divider_city;

    private ArrayList<AddressBean> province = new ArrayList<>();
    private ArrayList<AddressBean> city = new ArrayList<>();
    private ArrayList<AddressBean> area = new ArrayList<>();
    private ArrayAdapter<AddressBean> provinceAdapter;
    private ArrayAdapter<AddressBean> cityAdapter;
    private ArrayAdapter<AddressBean> areaAdapter;
    HashMap<String, Object> params = new HashMap<>();
    private int cityPosition;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_address_choice;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv_province = (ListView) findViewById(R.id.lv_province);
        lv_city = (ListView) findViewById(R.id.lv_city);
        lv_area = (ListView) findViewById(R.id.lv_area);
        view_divider_city = findViewById(R.id.view_divider_city);
        view_divider_city.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        provinceAdapter = new ArrayAdapter<>(this, R.layout.item_text1, province);
        lv_province.setAdapter(provinceAdapter);
        lv_province.setOnItemClickListener(this);
        cityAdapter = new ArrayAdapter<>(this, R.layout.item_text1, city);
        lv_city.setAdapter(cityAdapter);
        lv_city.setOnItemClickListener(this);
        areaAdapter = new ArrayAdapter<>(this, R.layout.item_text1, area);
        lv_area.setAdapter(areaAdapter);
        lv_area.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        requestProvince("GetProvince");
    }

    /**
     * 获得省
     *
     * @param params 方法名+参数
     */
    private void requestProvince(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (null == s)
                    return;
                parseListResult(province, AddressBean.class, s);
                provinceAdapter.notifyDataSetChanged();
            }
        }.execute(params);
    }

    /**
     * 获得市
     *
     * @param params 方法名+参数
     */
    private void requestCity(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (null == s)
                    return;
                parseListResult(city, AddressBean.class, s);
                cityAdapter.notifyDataSetChanged();
                if (city.size() > 0) {
                    view_divider_city.setVisibility(View.VISIBLE);
                }
            }
        }.execute(params);
    }

    /**
     * 获得区
     *
     * @param params 方法名+参数
     */
    private void requestArea(Object... params) {
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (null == s)
                    return;
                parseListResult(area, AddressBean.class, s);
                areaAdapter.notifyDataSetChanged();
            }
        }.execute(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_province:// 点击某个省，获得市的信息，同时清空区的信息
                params.clear();
                params.put("provincecode", province.get(position).code);
                requestCity("GetCity", params);
                view_divider_city.setVisibility(View.INVISIBLE);
                if (area.size() > 0) {
                    area.clear();
                    areaAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.lv_city:// 点击市，获得县信息
                cityPosition = position;
                params.clear();
                params.put("citycode", city.get(position).code);
                requestArea("GetArea", params);
                break;
            case R.id.lv_area:// 点击县，获得省市区编号，并将其返回给上个activity
                setResult(101, new Intent()
                        .putExtra("address", area.get(position))
                        .putExtra("name", city.get(cityPosition).name + area.get(position).name));
                finish();
                break;
        }
    }
}
