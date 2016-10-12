package com.yimeng.hyzchbczhwq.bean;

import java.io.Serializable;

/**
 * 病人bean
 */

public class PatientBean implements Serializable {
    /*
    patient_id				int		病人id
    user_id					int		用户id
    patient_name				varcahr	病人真实姓名
    patient_sex				char		性别
    patient_age				int	    年龄
    patient_identification		varchar	身份证号
    patient_phone				varchar	电话
     */
    public String patient_id;
    public String user_id;
    public String patient_name;
    public String patient_sex;
    public String patient_age;
    public String patient_identification;
    public String patient_phone;
}
