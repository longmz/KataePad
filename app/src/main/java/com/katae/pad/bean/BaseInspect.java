package com.katae.pad.bean;

import android.content.Context;

import com.katae.pad.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BaseInspect {
    private Context mContext;

    public final int mRecNum;
    public final String mBizCode;
    public final String mTaskId;
    public String mInspectId;
    public final Date mInspectDate;
    public String mInspectResult;
    public String mInconformityDesc;
    public String mPictures;
    public String mComment;

    // 补充属性，用户归纳不同调查类型
    public String mGroupId;
    public String mGroupName;
    public String mInspectNo;
    public String mInspectName;
    public String mInspectDesc;
    public String mValueType;
    public String mValueHint;
    public boolean mShowButton;
    public String mStatus;

    public List<InspectItem> mItems;

    public BaseInspect(int recNum, String bizCode, String taskId, String inspectId,
                       Date inspectDate) {
        this.mRecNum = recNum;
        this.mBizCode = bizCode;
        this.mTaskId = taskId;
        this.mInspectId = inspectId;
        this.mInspectDate = inspectDate;
        this.mStatus = "New";
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public String getInspectDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        return dateFormat.format(this.mInspectDate);
    }

    public String getStatusText() {
        if(this.mContext != null) {
            switch (mStatus) {
                case "New":
                    return this.mContext.getString(R.string.text_status_new);
                case "Checked":
                    return this.mContext.getString(R.string.text_status_checked);
                default:
                    return mStatus;
            }
        } else {
            return mStatus;
        }
    }
}