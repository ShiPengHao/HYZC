package com.yimeng.hyzc.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzc.R;
import com.yimeng.hyzc.bean.MedicineBean;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.UiUtils;

/**
 * 药方药品
 */
public class MedicineHolder extends BaseHolder<MedicineBean> {

    private Context context;

    private TextView tv_medicine_name;
    private TextView tv_medicine_unit;
    private TextView tv_medicine_number;

    @Override
    protected View initView() {
        View view = UiUtils.inflate(R.layout.item_medicine);
        tv_medicine_name = (TextView) view.findViewById(R.id.tv_medicine_name);
        tv_medicine_unit = (TextView) view.findViewById(R.id.tv_medicine_unit);
        tv_medicine_number = (TextView) view.findViewById(R.id.tv_medicine_number);
        return view;
    }

    @Override
    public void bindData(MedicineBean data) {
        if (null == context) {
            context = MyApp.getAppContext();
        }
        tv_medicine_name.setText(String.format("%s:%s", context.getString(R.string.medicine_name), data.CnName.replace("\r", "").replace("\n", "")));
        tv_medicine_unit.setText(String.format("%s:%s", context.getString(R.string.medicine_unit), data.Unit.replace("\r", "").replace("\n", "")));
        tv_medicine_number.setText(String.format("%s:%s", context.getString(R.string.medicine_number), data.medicines_quantity));
    }
}
