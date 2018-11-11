package com.katae.pad.bean;

import java.util.Date;

public class DutyInspect extends BaseInspect {

    public final String mWorkShopId;
    public final String mWorkShopNo;
    public final String mWorkShopName;
    public final String mWorkTeamId;
    public final String mWorkTeamNo;
    public final String mWorkTeamName;

    public final String mEmpId;
    public final String mEmpNo;
    public final String mEmpName;
    public final String mClauseId;
    public final String mClauseNo;
    public final String mClauseName;

    public DutyInspect(int recNum, String bizCode, String taskId, String inspectId,
                       Date inspectDate, String workShopId, String workShopNo, String workShopName,
                       String workTeamId, String workTeamNo, String workTeamName,
                       String empId, String empNo, String empName,
                       String clauseId, String clauseNo, String clauseName) {
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
        this.mClauseId = clauseId;
        this.mClauseNo = clauseNo;
        this.mClauseName = clauseName;
    }
}