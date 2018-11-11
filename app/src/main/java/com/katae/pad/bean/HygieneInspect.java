package com.katae.pad.bean;

import java.util.Date;

public class HygieneInspect extends BaseInspect {

    public final String mWorkShopId;
    public final String mWorkShopNo;
    public final String mWorkShopName;
    public final String mWorkTeamId;
    public final String mWorkTeamNo;
    public final String mWorkTeamName;

    public final String mEmpId;
    public final String mEmpNo;
    public final String mEmpName;
    public final String mBookerId;
    public final Date mCreatedDate;
    public final String mCreatorId;
    public final String mCreatorName;
    public Date mRevisedDate;
    public String mRevisorId;
    public String mRevisorName;

    public HygieneInspect(int recNum, String bizCode, String taskId, String inspectId,
                          Date inspectDate, String workShopId, String workShopNo, String workShopName,
                          String workTeamId, String workTeamNo, String workTeamName,
                          String empId, String empNo, String empName, String bookerId,
                          Date createDate, String creatorId, String creatorName) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mWorkShopId = workShopId;
        this.mWorkShopNo = workShopNo;
        this.mWorkShopName = workShopName;
        this.mWorkTeamId = workTeamId;
        this.mWorkTeamNo = workTeamNo;
        this.mWorkTeamName = workTeamName;

        this.mEmpId = empId;
        this.mEmpNo = empNo;
        this.mEmpName = empName;
        this.mBookerId = bookerId;
        this.mCreatedDate = createDate;
        this.mCreatorId = creatorId;
        this.mCreatorName = creatorName;
    }
}