package com.yankteam.yank.app;

/*
 * Singleton appinfo class
 */
public class AppInfo {
    public String api_key;
    public Double my_lat;
    public Double my_lng;

    public static AppInfo appInfo = null;

    public static AppInfo getInstance(){
        if (appInfo == null)
            appInfo = new AppInfo();

        return appInfo;
    }
}
