package com.yimeng.hyzchbczhwq.bean;

import java.io.Serializable;

/**
 * 处方详情，继承药品信息
 */
public class PrescriptionBean implements Serializable {
    public String prescription_id;//id
    public String explaination;// 用法
    public String doctor_name;// 医生姓名
    public String remark;// 医嘱
    public String patient_name;// 病人名称
    public String patient_phone;// 病人电话
    public String recipe_time;// 取药时间
    public String sig_time;// 开方时间
    public String medicines_unit;// 单位
    public String medicines_name;// 药品名称
    public String medicines_quantity;// 药品名称
    public int recipe_flag;// 取药标志（病人取药否 0：未取 1已取 2取药中 -1未受理）
    public String pharmacy_name;
    public String pharmacy_adress;
}
