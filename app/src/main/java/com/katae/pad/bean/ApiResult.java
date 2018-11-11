package com.katae.pad.bean;

public class ApiResult<T> {
    public int mErrorCode;
    public String mErrorMessage;
    public int mTotalCount;
    public T mResult;

    public ApiResult() {
        this.mErrorCode = -1;
        this.mErrorMessage = "ApiResult Initial Message";
        this.mTotalCount = 0;
    }
}