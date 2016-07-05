package com.yimeng.hyzc.utils;

/**
 * Created by 依萌 on 2016/6/20.
 */
public class MyConstant {
    /**
     * 账号、登陆有关的本地存储sp文件名称
     */
    public static final String PREFS_ACCOUNT = "PREFS_ACCOUNT";
    /**
     * 首次运行的键
     */
    public static final String KEY_ACCOUNT_FIRSTRUNNING = "KEY_ACCOUNT_FIRSTRUNNING";
    /**
     * 自动更新的键
     */
    public static final String KEY_ACCOUNT_AUTOUPDATE = "KEY_ACCOUNT_AUTOUPDATE";
    /**
     * 自动登陆的键
     */
    public static final String KEY_ACCOUNT_AUTOLOGIN = "KEY_ACCOUNT_AUTOLOGIN";
    /**
     * 上次登录成功的用户名的键
     */
    public static final String KEY_ACCOUNT_LAST_USERNAME = "KEY_ACCOUNT_LAST_USERNAME";
    /**
     * 上次登录成功的用户的密码的键
     */
    public static final String KEY_ACCOUNT_LAST_PASSWORD = "KEY_ACCOUNT_LAST_PASSWORD";
    /**
     * 上次登录成功的用户名的是否记住密码的键
     */
    public static final String KEY_ACCOUNT_LAST_REMEMBER = "KEY_ACCOUNT_LAST_REMEMBER";
    /**
     * 上次登录成功的用户的id的键
     */
    public static final String KEY_ACCOUNT_LAST_ID = "KEY_ACCOUNT_LAST_ID";
    /**
     * 上次登录成功的用户的类型的键
     */
    public static final String KEY_ACCOUNT_LAST_TYPE = "KEY_ACCOUNT_LAST_TYPE";

    /**
     * 服务器ip
     */
    public static final String URL_BASE = "http://192.168.0.168";
    /**
     * 登录接口
     */
    public static final String URL_LOGIN = URL_BASE + "/login/loginauth";
    /**
     * 药品类型接口
     */
    public static final String URL_DRUGTYPE = URL_BASE + "/cpw/cpw_drugtype/getlist";//http://192.168.0.168/CPW/CPW_drug/getlist

    /**
     * 注册接口
     */
    public static final String URL_REGISTER = URL_BASE + "";
    /**
     * webservice命名空间
     */
    public static final String NAMESPACE = "http://192.168.0.108:888/";
    /**
     * webserviceUrl
     */
    public static final String WEB_SERVICE_URL = NAMESPACE + "/API/ymOR_WebService.asmx";

}
