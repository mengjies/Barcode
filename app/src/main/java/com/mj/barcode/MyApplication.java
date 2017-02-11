package com.mj.barcode;

import android.app.Application;
import android.content.Context;

/**
 * Created by MengJie on 2017/1/17.
 * 获取全局Context
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /**
     * 获取全局Context
     * @return
     */
    public static Context getContext() {
        return context;
    }
}
