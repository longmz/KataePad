package com.katae.pad.bean;

import java.util.Date;

public class AssanationInspect extends BaseInspect {

    public final String mWorkShopId;
    public final String mWorkShopNo;
    public final String mWorkShopName;
    public final String mWorkTeamId;
    public final String mWorkTeamNo;
    public final String mWorkTeamName;

    public AssanationInspect(int recNum, String bizCode, String taskId, String inspectId,
        Date inspectDate, String workShopId, String workShopNo, String workShopName,
        String workTeamId, String workTeamNo, String workTeamName) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mWorkShopId = workShopId;
        this.mWorkShopNo = workShopNo;
        this.mWorkShopName = workShopName;
        this.mWorkTeamId = workTeamId;
        this.mWorkTeamNo = workTeamNo;
        this.mWorkTeamName = workTeamName;
    }
}