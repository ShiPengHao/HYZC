package com.yimeng.hyzc.bean;

import com.yimeng.hyzc.utils.PinYinUtils;

import java.io.Serializable;

/**
 * 药品类型数据bean
 */
public class DrugTypeBean implements Comparable<DrugTypeBean>, Serializable {
    public String CnName;
    public String IconUrl;
    public String TypeCode;
    public String EnName;
    public String ParentCode;
    public String IconClass;
    public int Position;

    @Override
    public int compareTo(DrugTypeBean another) {
        return getPinYin().compareTo(another.getPinYin());
    }

    @Override
    public String toString() {
        return CnName;
    }

    private String getPinYin() {
        return PinYinUtils.getPinYin(CnName);
    }
}
