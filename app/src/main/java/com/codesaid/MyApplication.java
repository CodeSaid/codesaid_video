package com.codesaid;

import android.app.Application;

import com.codesaid.lib_network.ApiService;

/**
 * Created By codesaid
 * On :2020-05-08 01:23
 * Package Name: com.codesaid
 * desc:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://123.56.232.18:8080/serverdemo", null);
    }
}
