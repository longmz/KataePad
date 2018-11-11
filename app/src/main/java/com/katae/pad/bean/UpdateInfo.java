package com.katae.pad.bean;

public class UpdateInfo {
    public final int mVersionCode;
    public final String mVersionName;
    public final String mApkSize;
    public final String mApkDescription;
    public final String mApkUrl;

    public UpdateInfo(int versionCode, String versionName, String apkSize, String apkDesc,
                      String apkUrl) {
        this.mVersionCode = versionCode;
        this.mVersionName = versionName;
        this.mApkSize = apkSize;
        this.mApkDescription = apkDesc;
        this.mApkUrl = apkUrl;
    }
}