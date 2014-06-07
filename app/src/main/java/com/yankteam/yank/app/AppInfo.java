package com.yankteam.yank.app;

import java.io.File;

/*
 * Singleton appinfo class
 */
public class AppInfo {
    public String api_key;

    public Boolean locationSet = false;
    public Double my_lat;
    public Double my_lng;
    public File myImageFile;

    public static AppInfo appInfo = null;

    public static AppInfo getInstance(){
        if (appInfo == null)
            appInfo = new AppInfo();

        return appInfo;
    }
}
