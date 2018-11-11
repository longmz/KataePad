package com.katae.pad.bean;
import android.content.Context;
import android.content.Intent;

import com.katae.pad.R;
import com.katae.pad.activity.MainTaskActivity;

public class AppInfo {
    public final String mAppId;
    public final String mAppName;
    public final String mAppIcon;
    public final String mAppType;
    public final String mSerialNo;

    public AppInfo(String appId, String appName, String appIcon, String appType, String serialNo) {
        this.mAppId = appId;
        this.mAppName = appName;
        this.mAppIcon = appIcon;
        this.mAppType = appType;
        this.mSerialNo = serialNo;
    }

    public int getImageDrawableId() {
        switch (mAppId.toLowerCase().trim()) {
            case "a01":
                return R.drawable.ptw;
            case "a02":
                return R.drawable.ptw1;
            case "a03":
                return R.drawable.pte;
            case "a04":
                return R.drawable.ptedc;
            default:
                return R.drawable.coming;
        }
    }

    public Intent getAppIntent(Context context) {
        switch (mAppId.toLowerCase().trim()) {
            case "a01":
                return new Intent(context, MainTaskActivity.class);
            case "a02":
                return new Intent(context, MainTaskActivity.class);
            case "a03":
                return new Intent(context, MainTaskActivity.class);
            case "a04":
                return new Intent(context, MainTaskActivity.class);
            default:
                return null;
        }
    }
}