package com.katae.pad.bean;

public class InspectGroup {
    public final String mTaskId;
    public final String mGroupId;
    public final String mGroupName;

    public InspectGroup(String taskId, String groupId, String groupName) {
        this.mTaskId = taskId;
        this.mGroupId = groupId;
        this.mGroupName = groupName;
    }
}