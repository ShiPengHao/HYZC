package com.yimeng.hyzc.utils;

public class DensityUtil {
    /**
     * 手机的分辨率
     */
    public static final float DENSITY = MyApp.getAppContext().getResources().getDisplayMetrics().density;

    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(float dpValue) {
        return (int) (dpValue * DENSITY + 0.5f);
    }  

    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(float pxValue) {
        return (int) (pxValue / DENSITY + 0.5f);
    }  
} 