package com.yimeng.hyzc.utils;

import android.widget.Toast;

/**
 * 静态吐司
 * Created by 依萌 on 2016/6/13.
 */
public class MyToast {
    private static Toast toast;

    public  static void show(final String content){
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null){
                    toast = Toast.makeText(MyApp.getAppContext(),content,Toast.LENGTH_SHORT);
                }else{
                    toast.setText(content);
                }
                toast.show();
            }
        });

    }
}
