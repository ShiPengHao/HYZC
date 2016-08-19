package com.yimeng.hyzchbczhwq.bean;

/**
 * 药店bean
 */
public class PharmacyBean {
    /**
     * pharmacy_id : 1
     * pharmacy_user : lisi
     * pharmacy_pwd : F4204C66F1E452D7
     * pharmacy_avatar : /upload/201608/15/201608150914140258.png
     * pharmacy_name : 张仲景大药房
     * pharmacy_Corporate : 李四
     * pharmacy_Organization_code : /upload/201608/15/201608150855173754.jpg
     * License : /upload/201608/15/201608150855230161.jpg
     * pharmacy_Businesspermit : /upload/201608/15/201608150855230161.jpg
     * contacts : 李四
     * phone : 13512345678
     * pharmacy_flag : 医保,城镇,农合
     * Provinces : 130000
     * cities : 130900
     * counties : 130928
     * pharmacy_adress : 吱吱吱
     * pharmacy_WeChat : 
     * pharmacy_Audit : 1
     * pharmacy_OnOff : 1
     * remark : 
     * add_time : /Date(1471222548000)/
     */

    public String pharmacy_id;
    public String pharmacy_user;
    public String pharmacy_pwd;
    public String pharmacy_avatar;
    public String pharmacy_name;
    public String pharmacy_adress;
    public String pharmacy_Corporate;
    public String pharmacy_Organization_code;
    public String License;
    public String pharmacy_Businesspermit;
    public String contacts;
    public String phone;
    public String pharmacy_flag;
    public String Provinces;
    public String cities;
    public String counties;
    public String pharmacy_WeChat;
    public int pharmacy_Audit;
    public int pharmacy_OnOff;
    public String remark;
    public String add_time;

    @Override
    public String toString() {
        return pharmacy_name + "(" + pharmacy_adress + ")";
    }
}
