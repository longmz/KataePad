package com.katae.pad.bean;

import android.content.Context;
import android.content.Intent;

import com.katae.pad.activity.InspectExpandActivity;
import com.katae.pad.activity.InspectRecyclerActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {

    public final String mTaskId;
    public final String mTaskTypeId;
    public final String mTaskTypeNo;
    public final String mTaskTypeName;
    public final Date mInspectDate;
    public String mDeptIds;
    public String mDeptNos;
    public String mDeptNames;
    public String mWorkShopIds;
    public String mWorkShopNos;
    public String mWorkShopName;
    public String mWorkTeamIds;
    public String mWorkTeamNos;
    public String mWorkTeamNames;
    public String mStoreId;
    public String mStoreNo;
    public String mStoreName;
    public final String mBookerId;
    public final int mStatus;
    public String mComment;

    public String mAppId;
    public String mTaskIcon;

    public Task(String taskId, String taskTypeId, String taskTypeNo, String taskTypeName,
                Date inspectDate, String bookerId, int status) {
        this.mTaskId = taskId;
        this.mTaskTypeId = taskTypeId;
        this.mTaskTypeNo = taskTypeNo;
        this.mTaskTypeName = taskTypeName;
        this.mInspectDate = inspectDate;
        this.mBookerId = bookerId;
        this.mStatus = status;
    }

    public String getInspectDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        return dateFormat.format(this.mInspectDate);
    }

    public Intent getInspectIntent(Context context) {
        switch (mTaskTypeNo.trim()) {
            case "101":
            case "106":
            case "107":
            case "108":
            case "110":
            case "111":
            case "112":
            case "113":
            case "114":
            case "115":
                return new Intent(context, InspectRecyclerActivity.class);
            case "102":
            case "103":
            case "104":
            case "105":
            case "109":
                return new Intent(context, InspectExpandActivity.class);
            default:
                return new Intent(context, InspectRecyclerActivity.class);
        }
    }
}