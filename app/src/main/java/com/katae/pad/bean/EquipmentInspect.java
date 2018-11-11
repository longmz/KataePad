package com.katae.pad.bean;

import java.util.Date;

public class EquipmentInspect extends BaseInspect {

    public final String mDeptId;
    public final String mDeptNo;
    public final String mDeptName;
    public final String mWorkShopId;
    public final String mWorkShopNo;
    public final String mWorkShopName;

    public final String mEquipmentId;
    public final String mEquipmentNo;
    public final String mEquipmentName;
    public final String mBarcode;
    public final String mEquipmentState;

    public EquipmentInspect(int recNum, String bizCode, String taskId, String inspectId,
                            Date inspectDate, String deptId, String deptNo, String deptName,
                            String workShopId, String workShopNo, String workShopName,
                            String equipmentId, String equipmentNo, String equipmentName,
                            String barcode, String equipmentState) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mDeptId = deptId;
        this.mDeptNo = deptNo;
        this.mDeptName = deptName;
        this.mWorkShopId = workShopId;
        this.mWorkShopNo = workShopNo;
        this.mWorkShopName = workShopName;

        this.mEquipmentId = equipmentId;
        this.mEquipmentNo = equipmentNo;
        this.mEquipmentName = equipmentName;
        this.mBarcode = barcode;
        this.mEquipmentState = equipmentState;
    }
}