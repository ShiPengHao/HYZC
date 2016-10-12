package com.yimeng.hyzchbczhwq.bean;

import java.io.Serializable;

/**
 * 满意度评价
 */
public class CommentBean implements Serializable{
    /**
     * comment_content : 咯
     * comment_point : 5
     * comment_time : /Date(1473238091277)/
     * is_anonymity : 0
     * user_name : 张三
     */

    public String comment_content;
    public int comment_point;
    public String comment_time;
    public int is_anonymity;
    public String patient_name;
}
