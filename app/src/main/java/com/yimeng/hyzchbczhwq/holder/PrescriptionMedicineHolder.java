package com.yimeng.hyzchbczhwq.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.adapter.PrescriptionMedicineAdapter;
import com.yimeng.hyzchbczhwq.bean.PrescriptionBean;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.UiUtils;

import java.util.List;

/**
 * 药方药品
 */
public class PrescriptionMedicineHolder extends BaseHolder<PrescriptionBean> {

    private Context context;

    private TextView tv_medicine_name;
    private TextView tv_medicine_unit;
    private TextView tv_medicine_number;
    private List<PrescriptionBean> beans;
    private PrescriptionMedicineAdapter adapter;
    private TextView tv_medicine_usage;

    public PrescriptionMedicineHolder(List<PrescriptionBean> data, PrescriptionMedicineAdapter adapter){
        super();
        this.beans = data;
        this.adapter = adapter;
    }

    @Override
    protected View initView() {
        View view = UiUtils.inflate(R.layout.item_prescription_medicine);
        tv_medicine_name = (TextView) view.findViewById(R.id.tv_medicine_name);
        tv_medicine_unit = (TextView) view.findViewById(R.id.tv_medicine_unit);
        tv_medicine_number = (TextView) view.findViewById(R.id.tv_medicine_number);
        tv_medicine_usage = (TextView) view.findViewById(R.id.tv_medicine_usage);

        return view;
    }

    @Override
    public void bindData(final PrescriptionBean data) {
        if (null == context) {
            context = MyApp.getAppContext();
        }
        tv_medicine_name.setText(String.format("%s:%s", context.getString(R.string.medicine_name), data.medicines_name.replace("\r", "").replace("\n", "")));
        tv_medicine_unit.setText(String.format("%s:%s", context.getString(R.string.medicine_unit), data.medicines_unit.replace("\r", "").replace("\n", "")));
        tv_medicine_number.setText(String.format("%s:%s", context.getString(R.string.medicine_number), data.medicines_quantity));
        tv_medicine_usage.setText(String.format("%s:%s", context.getString(R.string.medicine_usage), data.explaination.replace("\r","").replace("\n","")));
    }
}
