package com.yimeng.hyzc.utils;

/**
 * Created by 依萌 on 2016/6/20.
 */
public class MyConstant {
    /**
     * 账号、登陆有关的本地存储sp文件名称
     */
    public static final String SPREFS_ACCOUNT = "SPREFS_ACCOUNT";
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
     * 上次登录成功的用户名的密码的键
     */
    public static final String KEY_ACCOUNT_LAST_PASSWORD = "KEY_ACCOUNT_LAST_PASSWORD";
    /**
     * 上次登录成功的用户名的是否记住密码的键
     */
    public static final String KEY_ACCOUNT_LAST_REMEMBER = "KEY_ACCOUNT_LAST_REMEMBER";

    /**
     * 服务器ip
     */
    public static final String URL_BASE = "http://192.168.0.168/";
    /**
     * 登录接口
     */
    public static final String URL_LOGIN = URL_BASE + "login/loginauth";
    /**
     * 登录接口
     */
    public static final String URL_DRUGTYPE = URL_BASE + "cpw/cpw_drugtype/getlist";
}
