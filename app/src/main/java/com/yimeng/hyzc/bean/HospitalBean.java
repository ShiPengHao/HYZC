package com.yimeng.hyzc.bean;

import java.io.Serializable;

/**
 * Created by 依萌 on 2016/6/28.
 */
public class HospitalBean implements Serializable {
    public String province;
    public String city;
    public String area;
    public String hospital_name;
    public String doctor_adress;
    public int hospital_id;

    @Override
    public String toString() {
        return hospital_name;
    }
}
