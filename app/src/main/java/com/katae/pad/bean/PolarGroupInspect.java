package com.katae.pad.bean;

import java.util.Date;

public class PolarGroupInspect extends BaseInspect {

    public final String mWorkShopId;
    public final String mWorkShopNo;
    public final String mWorkShopName;
    public final String mInspectArea;

    public PolarGroupInspect(int recNum, String bizCode, String taskId, String inspectId,
                             Date inspectDate, String workShopId, String workShopNo,
                             String workShopName, String inspectArea) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mWorkShopId = workShopId;
        this.mWorkShopNo = workShopNo;
        this.mWorkShopName = workShopName;
        this.mInspectArea = inspectArea;
    }
}