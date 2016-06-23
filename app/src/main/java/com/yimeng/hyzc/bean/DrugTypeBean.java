package com.yimeng.hyzc.bean;

import com.yimeng.hyzc.utils.PinYinUtils;

import java.io.Serializable;

/**
 * Created by 依萌 on 2016/6/17.
 */
public class DrugTypeBean implements Comparable<DrugTypeBean>, Serializable {
    public String name;
    public String icon;
    public String TypeCode;

    @Override
    public int compareTo(DrugTypeBean another) {
        return getPinYin().compareTo(another.getPinYin());
    }

    @Override
    public String toString() {
        return "DrugTypeBean{" +
                "name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", TypeCode='" + TypeCode + '\'' +
                '}';
    }

    private String getPinYin() {
        return PinYinUtils.getPinYin(name);
    }
}
