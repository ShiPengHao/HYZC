package com.yimeng.hyzc.bean;

import java.io.Serializable;

/**
 * 医生
 */
public class DoctorBean implements Serializable{
    public int doctor_id;//id
    public int doctor_UCL;// 预约上限，-1不限
    public int Is_Order;// 是否接受预约0不接受1接受
    public String doctor_user;//用户名
    public String doctor_name;//姓名
    public String doctor_avatar;//头像相对路径
    public String doctor_qualification;//医生资格证相对路径
    public String E_signature;//电子签名相对路径
    public String doctor_sex;//性别
    public String doctor_age;//年龄
    public String doctor_phone;//电话
    public String doctor_WeChat;//电话
    public String doctor_email;//邮箱
    public String remark;//简介
    public String doctor_Audit;//审核状态（0未受理 1已通过 -1未通过）
    public String add_time;//注册时间
}
