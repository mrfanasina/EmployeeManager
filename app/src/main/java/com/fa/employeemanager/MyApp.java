package com.fa.employeemanager;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.Context;
import com.fa.employeemanager.api.ApiConfig;

public class MyApp extends Application {
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);

        String ip = prefs.getString("server_ip", "10.0.2.2");
        String port = prefs.getString("server_port", "3000");
        String api = prefs.getString("api_read", "employes");

        ApiConfig.init(ip, port, api);
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}