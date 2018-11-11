package com.katae.pad.bean;

import java.util.Date;

public class PesticideResidueInspect extends BaseInspect {

    public final String mWorkShopId;
    public final String mWorkShopNo;
    public final String mWorkShopName;
    public final String mWorkTeamId;
    public final String mWorkTeamNo;
    public final String mWorkTeamName;

    public final String mProdItemId;
    public final String mProdItemNo;
    public final String mProdItemName;
    public final String mSupplierId;
    public final String mSupplierNo;
    public final String mSupplierName;
    public final String mSupplierShortName;

    public PesticideResidueInspect(int recNum, String bizCode, String taskId, String inspectId,
                                   Date inspectDate, String workShopId, String workShopNo,
                                   String workShopName, String workTeamId, String workTeamNo,
                                   String workTeamName, String prodItemId, String prodItemNo,
                                   String prodItemName, String supplierId, String supplierNo,
                                   String supplierName, String supplierShortname) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mWorkShopId = workShopId;
        this.mWorkShopNo = workShopNo;
        this.mWorkShopName = workShopName;
        this.mWorkTeamId = workTeamId;
        this.mWorkTeamNo = workTeamNo;
        this.mWorkTeamName = workTeamName;

        this.mProdItemId = prodItemId;
        this.mProdItemNo = prodItemNo;
        this.mProdItemName = prodItemName;
        this.mSupplierId = supplierId;
        this.mSupplierNo = supplierNo;
        this.mSupplierName = supplierName;
        this.mSupplierShortName = supplierShortname;
    }
}