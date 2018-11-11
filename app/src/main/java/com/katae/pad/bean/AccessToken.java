package com.katae.pad.bean;

import java.util.Date;
import java.util.List;

public class AccessToken {
    public final String mAccessToken;
    public final String mTokenType;
    public final long mExpiresIn;
    public final String mUserId;
    public final String mName;
    public final String mEmpId;

    public Date mIssued;  //签发时间
    public Date mExpires; //到期时间
    public List<AppInfo> mApps; // App列表

    public AccessToken(String accessToken, String tokenType, long expiresIn, String userId,
                       String name, String empId) {
        this.mAccessToken = accessToken;
        this.mTokenType = tokenType;
        this.mExpiresIn = expiresIn;
        this.mUserId = userId;
        this.mName = name;
        this.mEmpId = empId;
    }
}