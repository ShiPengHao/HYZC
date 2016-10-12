package com.yimeng.hyzchbczhwq.bean;

import java.io.Serializable;

/**
 * 预约信息
 */
public class AppointmentBean implements Serializable {
    /*
    "appointment_id":22,
    "user_id":12,
    "disease_description":"aaa",
    "select_doctor_id":2,
    "doctor_Responses":"",
    "doctor_Responses_time":null,
    "doctor_dispose":0,
    "doctor_Way":"",
    "IsPrescribe":0,
    "registration_time":"\/Date(1467734400000)\/",
    "add_time":"\/Date(1467776262920)\/",
    "user_name":"user001",
    "patient_sex":1,
    "patient_age":"\/Date(-99993600000)\/",
    "patient_phone":"13526669999",
    "doctor_name":"张三"}]
     */
    public String add_time;
    public String doctor_name;
    public int appointment_id;
    public String disease_description;
    public String select_doctor_id;
    public String patient_id;
    public int doctor_dispose;
    public String doctor_Responses;
    public String doctor_Responses_time;
    public String doctor_Way;
    public int IsPrescribe;
    public int is_comment;
    public String patient_name;
    public String patient_sex;
    public String patient_age;
    public String patient_phone;
    public String registration_time;
    public String prescription_id;
}
