package com.yimeng.hyzc.adapter;

import com.yimeng.hyzc.bean.MedicineBean;
import com.yimeng.hyzc.bean.PrescriptionBean;
import com.yimeng.hyzc.holder.BaseHolder;
import com.yimeng.hyzc.holder.MedicineHolder;

import java.util.ArrayList;

/**
 * 药方药品适配器
 */
public class MedicineAdapter extends DefaultAdapter<MedicineBean> {

    /**
     * 是否传递adapter，用于条目内操作
     */
    private boolean shareAdapter;

    public MedicineAdapter(ArrayList<MedicineBean> data) {
        this(data, true);
    }

    public MedicineAdapter(ArrayList<MedicineBean> data, boolean shareAdapter) {
        super(data);
        this.shareAdapter = shareAdapter;
    }



    @Override
    protected BaseHolder getHolder() {
        if (shareAdapter) {
            return new MedicineHolder(data, this);
        } else {
            return new MedicineHolder(data, null);
        }
    }
}
