package com.yimeng.hyzchbczhwq.bean;

import java.io.Serializable;

/**
 * 病人
 */
public class UserBean implements Serializable {
    public int user_id;//id
    public String user_name;//姓名
    public String user_avatar;//头像相对路径
    public String patient_phone;//电话
    public String user_WeChat;//电话
    public String user_email;//邮箱
    public String add_time;//注册时间
    public String user_ICode;//邀请码
}