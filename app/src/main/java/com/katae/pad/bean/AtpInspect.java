package com.katae.pad.bean;

import java.util.Date;

public class AtpInspect extends BaseInspect {

    public final String mDeptId;
    public final String mDeptNo;
    public final String mDeptName;
    public final String mWorkShopId;
    public final String mWorkShopNo;
    public final String mWorkShopName;

    public AtpInspect(int recNum, String bizCode, String taskId, String inspectId,
                      Date inspectDate, String deptId, String deptNo, String deptName,
                      String workShopId, String workShopNo, String workShopName) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mDeptId = deptId;
        this.mDeptNo = deptNo;
        this.mDeptName = deptName;
        this.mWorkShopId = workShopId;
        this.mWorkShopNo = workShopNo;
        this.mWorkShopName = workShopName;
    }
}