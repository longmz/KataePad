package com.katae.pad.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longmz on 2018/05/19.
 * Pick Task Execution
 */

public class TaskContent {

    /**
     * An array of TASKS
     */
    public static final List<Task> TODO_TASK_LIST = new ArrayList<>();
    public static final List<Task> COMP_TASK_LIST = new ArrayList<>();

    /**
     * A map of TASKS, by ID.
     */
    public static final Map<String, Task> TODO_TASK_MAP = new HashMap<>();
    public static final Map<String, Task> COMP_TASK_MAP = new HashMap<>();

    public static void initTodoList(List<Task> list) {
        TODO_TASK_LIST.clear();
        TODO_TASK_MAP.clear();
        for (Task item : list) {
            TODO_TASK_LIST.add(item);
            TODO_TASK_MAP.put(item.mTaskId, item);
        }
    }

    public static void initCompList(List<Task> list) {
        COMP_TASK_LIST.clear();
        COMP_TASK_MAP.clear();
        for (Task item : list) {
            COMP_TASK_LIST.add(item);
            COMP_TASK_MAP.put(item.mTaskId, item);
        }
    }

    public static void clearTodoList() {
        TODO_TASK_LIST.clear();
        TODO_TASK_MAP.clear();
    }

    public static void clearCompList() {
        COMP_TASK_LIST.clear();
        COMP_TASK_MAP.clear();
    }
}