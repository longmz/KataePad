package com.katae.pad.bean;

import java.util.Date;

public class PickInspect extends BaseInspect {

    public final String mStoreId;
    public final String mStoreNo;
    public final String mStoreMapNo;
    public final String mStoreName;
    public final String mRouteId;
    public final String mRouteNo;
    public final String mRouteName;
    public final String mPickerId;
    public final String mPickerNo;
    public final String mPickerName;
    public final int mPickQty;
    public final int mCartonQty;

    public PickInspect(int recNum, String bizCode, String taskId, String inspectId,
                       Date inspectDate, String storeId, String storeNo, String storeMapNo,
                       String storeName, String routeId, String routeNo, String routeName,
                       String pickerId, String pickerNo, String pickerName, int pickQty,
                       int cartonQty) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mStoreId = storeId;
        this.mStoreNo = storeNo;
        this.mStoreMapNo = storeMapNo;
        this.mStoreName = storeName;
        this.mRouteId = routeId;
        this.mRouteNo = routeNo;
        this.mRouteName = routeName;
        this.mPickerId = pickerId;
        this.mPickerNo = pickerNo;
        this.mPickerName = pickerName;
        this.mPickQty = pickQty;
        this.mCartonQty = cartonQty;
    }
}