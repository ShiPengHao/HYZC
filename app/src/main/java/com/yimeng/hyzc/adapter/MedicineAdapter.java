package com.yimeng.hyzc.adapter;

import android.widget.ListView;

import com.yimeng.hyzc.bean.MedicineBean;
import com.yimeng.hyzc.holder.BaseHolder;
import com.yimeng.hyzc.holder.MedicineHolder;

import java.util.List;

/**
 * 药方药品适配器
 */
public class MedicineAdapter extends DefaultAdapter<MedicineBean> {


    public MedicineAdapter(List<MedicineBean> data) {
        super(data);
    }

    @Override
    protected BaseHolder getHolder() {
        return new MedicineHolder(data, this);
    }
}
