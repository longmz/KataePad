package com.katae.pad.bean;

public class InspectItem {

    public final String mInspectId;
    public final String mItemId;
    public final String mItemNo;
    public final String mItemName;
    public final String mItemUnit;
    public String mInspectResult;
    public String mInconformityDesc;
    public String mPictures;
    public String mComment;

    public String mValueType;
    public String mValueHint;

    public InspectItem(String inspectId, String itemId, String itemNo, String itemName, String itemUnit) {
        this.mInspectId = inspectId;
        this.mItemId = itemId;
        this.mItemNo = itemNo;
        this.mItemName = itemName;
        this.mItemUnit = itemUnit;
    }
}