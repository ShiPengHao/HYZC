package com.yimeng.hyzchbczhwq.bean;

import java.io.Serializable;

/**
 * 病人
 */
public class PatientBean implements Serializable {
    public int patient_id;//id
    public String patient_user;//用户名
    public String patient_name;//姓名
    public String patient_avatar;//头像相对路径
    public String patient_qualification;//医生资格证相对路径
    public String patient_sex;//性别
    public String patient_age;//年龄
    public String patient_phone;//电话
    public String patient_identification;//身份证号
    public String patieny_WeChat;//电话
    public String patient_email;//邮箱
    public String add_time;//注册时间
}