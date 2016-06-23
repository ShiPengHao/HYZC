package com.yimeng.hyzc.bean;

import java.io.Serializable;

/**
 * Created by 依萌 on 2016/6/23.
 */
public class AddressBean implements Serializable{
    public String code;
    public String name;
    public String provincecode;
    public String citycode;

    @Override
    public String toString() {
        return name;
    }
}
