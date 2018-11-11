package com.katae.pad.bean;

public class TaskGroup {
    public final String mCheckId;
    public final String mTaskGroupId;
    public final String mTaskGroupName;

    public TaskGroup(String checkId, String groupId, String groupName) {
        this.mCheckId = checkId;
        this.mTaskGroupId = groupId;
        this.mTaskGroupName = groupName;
    }
}