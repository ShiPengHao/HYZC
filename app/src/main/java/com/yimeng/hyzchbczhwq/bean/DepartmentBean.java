package com.yimeng.hyzchbczhwq.bean;

import java.io.Serializable;

/**
 * Created by 依萌 on 2016/6/28.
 */
public class DepartmentBean implements Serializable {
    public int departments_id;
    public String departments_name;
    public int parentid;
    public int hospital_id;

    @Override
    public String toString() {
        return departments_name;
    }
}
