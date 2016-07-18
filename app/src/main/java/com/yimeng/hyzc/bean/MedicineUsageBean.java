package com.yimeng.hyzc.bean;

import java.io.Serializable;

/**
 * 药品用法
 */
public class MedicineUsageBean implements Serializable{
    public String usage_id;
    public String usage_code;
    public String explaination;

    @Override
    public String toString() {
        return usage_code + ":" + explaination.replace("\n", "").replace("\r", "");
    }
}
