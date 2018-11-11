package com.katae.pad.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longmz on 2018/05/19.
 * Pick Task Execution
 */

public class LoginContent {

    /**
     * An array of APPS
     */
    public static final List<AppInfo> APP_LIST = new ArrayList<>();

    /**
     * A map of APPS, by ID.
     */
    public static final Map<String, AppInfo> APP_MAP = new HashMap<>();

    public static String Account;
    public static String UserId;
    public static String UserName;
    public static String Token;

    public static void initList(List<AppInfo> list) {
        APP_LIST.clear();
        APP_MAP.clear();
        for (AppInfo item : list) {
            APP_LIST.add(item);
            APP_MAP.put(item.mAppId, item);
        }
    }

    public static void clearList() {
        APP_LIST.clear();
        APP_MAP.clear();
    }
}