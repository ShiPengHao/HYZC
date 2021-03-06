package com.yimeng.hyzchbczhwq.adapter;

import com.yimeng.hyzchbczhwq.bean.PrescriptionBean;
import com.yimeng.hyzchbczhwq.holder.BaseHolder;
import com.yimeng.hyzchbczhwq.holder.PrescriptionMedicineHolder;

import java.util.ArrayList;

/**
 * 药方药品适配器
 */
public class PrescriptionMedicineAdapter extends DefaultAdapter<PrescriptionBean> {

    /**
     * 是否传递adapter，用于条目内操作
     */
    private boolean shareAdapter;

    public PrescriptionMedicineAdapter(ArrayList<PrescriptionBean> data) {
        this(data, true);
    }

    public PrescriptionMedicineAdapter(ArrayList<PrescriptionBean> data, boolean shareAdapter) {
        super(data);
        this.shareAdapter = shareAdapter;
    }



    @Override
    protected BaseHolder getHolder() {
        if (shareAdapter) {
            return new PrescriptionMedicineHolder(data, this);
        } else {
            return new PrescriptionMedicineHolder(data, null);
        }
    }
}
