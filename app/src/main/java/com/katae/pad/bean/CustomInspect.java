package com.katae.pad.bean;

import java.util.Date;

public class CustomInspect extends BaseInspect {

    public final String mItemName;

    public CustomInspect(int recNum, String bizCode, String taskId, String inspectId,
                         Date inspectDate, String itemName) {
        super(recNum, bizCode, taskId, inspectId, inspectDate);

        this.mItemName = itemName;
    }
}