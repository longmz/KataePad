package com.katae.pad.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longmz on 2018/05/19.
 * Pick Task Execution
 */

public class InspectContent {

    /**
     * An array of INSPECTS
     */
    public static final List<BaseInspect> INSPECT_LIST = new ArrayList<>();
    public static final List<InspectGroup> GROUP_LIST = new ArrayList<>();

    /**
     * A map of INSPECTS, by ID.
     */
    public static final Map<String, BaseInspect> INSPECT_MAP = new HashMap<>();

    public static void initList(List<BaseInspect> list) {
        clearList();
        if(list != null) {
            InspectGroup ig;
            List<String> groupIds = new ArrayList<>();
            for (BaseInspect item : list) {
                INSPECT_LIST.add(item);
                INSPECT_MAP.put(String.valueOf(item.mRecNum), item);

                if (item.mGroupId != null && !item.mGroupId.isEmpty() && !groupIds.contains(item.mGroupId)) {
                    groupIds.add(item.mGroupId);
                    ig = new InspectGroup(item.mTaskId, item.mGroupId, item.mGroupName);
                    GROUP_LIST.add(ig);
                }
            }
        }
    }

    public static void clearList() {
        INSPECT_LIST.clear();
        INSPECT_MAP.clear();
        GROUP_LIST.clear();
    }
}