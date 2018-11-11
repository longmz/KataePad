package com.katae.pad.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.session.MediaSession;
import android.util.Base64;

import com.katae.pad.bean.AccessToken;
import com.katae.pad.bean.ApiResult;
import com.katae.pad.bean.AppInfo;
import com.katae.pad.bean.BaseInspect;
import com.katae.pad.bean.InspectGroup;
import com.katae.pad.bean.InspectItem;
import com.katae.pad.bean.Task;
import com.katae.pad.bean.UpdateInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

/**
 * Created by longmingzan on 2018/05/19.
 */

public class NetWebApi {

    // WEB API 地址
    private static final String KATAE_BASE_URL = "http://jacob9292.6655.la/KataeServ/";
    private static final String KATAE_TEST_URL = "http://longwf.imwork.net/api/";

    // User：根据账号密码获取Token
    public static ApiResult<AccessToken> getAccessToken(String userName, String password, String imei){

        ApiResult<AccessToken> result = new ApiResult<>();

        AppInfo app;
        List<AppInfo> appList;

        try {
            //password = DefaultEncryptor.Encrypt3DES(password, "jacob929@qq.com");
            //password = password.replace("\n", "");

            String authString = "Pad_HuaWei:" + imei;
            String paramString = "grant_type=password&username=" + userName + "&password=" + password;
            String resultString = postBase("Token", paramString, "", authString);
            JSONObject object = new JSONObject(resultString);

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

            if(!object.isNull("Apps")) {
                JSONArray itemArrary = object.getJSONArray("Apps");
                appList = new ArrayList<>();
                for (int i = 0; i < itemArrary.length(); i++) {
                    app = new AppInfo(
                            itemArrary.getJSONObject(i).getString("AppId"),
                            itemArrary.getJSONObject(i).getString("AppName"),
                            itemArrary.getJSONObject(i).getString("AppIcon"),
                            itemArrary.getJSONObject(i).getString("AppType"),
                            itemArrary.getJSONObject(i).getString("SerialNo"));
                    appList.add(app);
                }
            } else {
                appList = null;
            }

            AccessToken token = new AccessToken(
                    object.getString("access_token"),
                    object.getString("token_type"),
                    object.getLong("expires_in"),
                    object.getString("userId"),
                    object.getString("name"),
                    object.getString("empId"));

            token.mIssued = dateFormat.parse(object.getString(".issued"));
            token.mExpires = dateFormat.parse(object.getString(".expires"));
            token.mApps = appList;

            result.mErrorCode = 0;
            result.mErrorMessage = "";
            result.mTotalCount = 1;
            result.mResult = token;
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        if(result.mErrorCode != 0) {
            result.mErrorMessage = translateMessage(result.mErrorMessage);
        }

        return result;
    }

    public static ApiResult<AccessToken> getLocalAccessToken(String userName, String password, String imei, Context context){

        ApiResult<AccessToken> result = new ApiResult<>();

        AppInfo app;
        List<AppInfo> appList;

        // 读取本地SQLite数据库
        SqliteDBHelper dbHelper = new SqliteDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 获取用户信息
        Cursor cursor = db.query(SqliteDBHelper.TABLE_USR, null,
                "account = ? and password = ?", new String[]{ userName, password },
                null, null, null, null);

        // 不断移动光标获取值
        if (cursor.moveToNext()) {
            AccessToken token = new AccessToken(
                    "",
                    "",
                    0,
                    cursor.getString(0),
                    cursor.getString(4),
                    cursor.getString(3));

            result.mErrorCode = 0;
            result.mErrorMessage = "";
            result.mTotalCount = 1;
            result.mResult = token;
        } else {
            result.mErrorCode = -1;
            result.mErrorMessage = "用户名或密码错误！";
        }

        if(result.mErrorCode == 0) {
            appList = new ArrayList<>();

            // 获取APP信息
            cursor = db.query(SqliteDBHelper.TABLE_APP, null,
                    "account = ?", new String[]{ userName },
                    null, null, null, null);

            while (cursor.moveToNext()) {
                app = new AppInfo(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));
                appList.add(app);
            }

            result.mResult.mApps = appList;
        }

        // 关闭光标
        cursor.close();

        return result;
    }

    public static void saveLocalAccessToken(AccessToken token, String account, String password, Context context) {
        if(token != null) {
            SqliteDBHelper dbHelper = new SqliteDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete(SqliteDBHelper.TABLE_USR, "account = ?", new String[]{ account });
            db.delete(SqliteDBHelper.TABLE_APP, "account = ?", new String[]{ account });

            ContentValues values;

            values = new ContentValues();
            values.put("account", account);
            values.put("password", password);
            values.put("imei", "");
            values.put("emp_id", token.mEmpId);
            values.put("user_name", token.mName);
            db.insert(SqliteDBHelper.TABLE_USR, null, values);

            if(token.mApps != null) {
                for (AppInfo item : token.mApps) {
                    values = new ContentValues();
                    values.put("account", account);
                    values.put("app_id", item.mAppId);
                    values.put("app_name", item.mAppName);
                    values.put("app_icon", item.mAppIcon);
                    values.put("app_type", item.mAppType);
                    values.put("serial_no", item.mSerialNo);
                    db.insert(SqliteDBHelper.TABLE_APP, null, values);
                }
            }
        }
    }

    // User：根据账号密码获取Token
    public static ApiResult<UpdateInfo> getUpdateInfo(){

        ApiResult<UpdateInfo> result = new ApiResult<>();

        try {
            String paramString = "";
            String resultString = getBase("api/ApkUpdate/", paramString, "", "");
            JSONObject object = new JSONObject(resultString);

            result.mErrorCode = object.getInt("ErrorCode");
            result.mErrorMessage = object.getString("ErrorMessage");
            result.mTotalCount = object.getInt("TotalCount");

            if(result.mErrorCode == 0) {
                if(!object.isNull("Result")) {
                    JSONObject jsonObject = object.getJSONObject("Result");
                    result.mResult = new UpdateInfo(
                            jsonObject.getInt("VersionCode"),
                            jsonObject.getString("VersionName"),
                            jsonObject.getString("ApkSize"),
                            jsonObject.getString("ApkDescription"),
                            jsonObject.getString("ApkUrl"));
                } else {
                    result.mErrorCode = -1;
                    result.mErrorMessage = "没有获取到更新数据！";
                }
            }
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        if(result.mErrorCode != 0) {
            result.mErrorMessage = translateMessage(result.mErrorMessage);
        }

        return result;
    }

    // TASK：获取未完成的任务列表
    public static ApiResult<List<Task>> getUnfinishedTasks(String userId){

        ApiResult<List<Task>> result = new ApiResult<>();

        Task head;
        List<Task> list;

        try {
            String paramString = "";
            String resultString = getBase("api/Task/UnfinishedTasks/" + userId, paramString, "", "");
            JSONObject object = new JSONObject(resultString);

            result.mErrorCode = object.getInt("ErrorCode");
            result.mErrorMessage = object.getString("ErrorMessage");
            result.mTotalCount = object.getInt("TotalCount");

            if(result.mErrorCode == 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
                list = new ArrayList<>();

                if(!object.isNull("Result")) {
                    JSONArray listArrary = object.getJSONArray("Result");
                    for (int i = 0; i < listArrary.length(); i++) {

                        head = new Task(
                                listArrary.getJSONObject(i).getString("TaskId"),
                                listArrary.getJSONObject(i).getString("TaskTypeId"),
                                listArrary.getJSONObject(i).getString("TaskTypeNo"),
                                listArrary.getJSONObject(i).getString("TaskTypeName"),
                                dateFormat.parse(listArrary.getJSONObject(i).getString("InspectDate")),
                                listArrary.getJSONObject(i).getString("BookerId"),
                                listArrary.getJSONObject(i).getInt("Status"));

                        list.add(head);
                    }
                }

                result.mResult = list;
            }
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        if(result.mErrorCode != 0) {
            result.mErrorMessage = translateMessage(result.mErrorMessage);
        }
        return  result;
    }

    public static ApiResult<List<Task>> getLocalUnfinishedTasks(String userId, String appId, Context context){

        ApiResult<List<Task>> result = new ApiResult<>();

        Task head;
        List<Task> list;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);

            // 读取本地SQLite数据库
            SqliteDBHelper dbHelper = new SqliteDBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // 获取用户信息
            Cursor cursor = db.query(SqliteDBHelper.TABLE_TASK0, null,
                    "account = ? and app_id = ?", new String[]{ userId, appId },
                    null, null, null, null);

            // 不断移动光标获取值
            list = new ArrayList<>();
            while (cursor.moveToNext()) {
                head = new Task(
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        dateFormat.parse(cursor.getString(6)),
                        cursor.getString(7),
                        cursor.getInt(8));

                list.add(head);
            }

            // 关闭光标
            cursor.close();

            result.mErrorCode = 0;
            result.mErrorMessage = "";
            result.mTotalCount = 1;
            result.mResult = list;
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        return result;
    }

    public static void saveLocalUnfinishedTasks(List<Task> tasks, String account, String appId, Context context) {
        if(tasks != null) {
            SqliteDBHelper dbHelper = new SqliteDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete(SqliteDBHelper.TABLE_TASK0, "account = ? and app_id = ?", new String[]{ account, appId });

            ContentValues values;

            for (Task item : tasks) {
                values = new ContentValues();
                values.put("account", account);
                values.put("app_id", appId);
                values.put("task_id", item.mTaskId);
                values.put("type_id", item.mTaskTypeId);
                values.put("type_no", item.mTaskTypeNo);
                values.put("type_name", item.mTaskTypeName);
                values.put("inspect_date", item.getInspectDate());
                values.put("booker_id", item.mBookerId);
                values.put("status", item.mStatus);
                values.put("task_num", 0);
                db.insert(SqliteDBHelper.TABLE_TASK0, null, values);
            }
        }
    }

    // TASK：获取已完成的任务列表
    public static ApiResult<List<Task>> getFinishedTasks(String userId, String startDate, String endDate){

        ApiResult<List<Task>> result = new ApiResult<>();

        Task head;
        List<Task> list;

        try {
            String paramString = "startDate=" + startDate + "&endDate=" + endDate;
            String resultString = getBase("api/Task/FinishedTasks/" + userId, paramString, "", "");
            JSONObject object = new JSONObject(resultString);

            result.mErrorCode = object.getInt("ErrorCode");
            result.mErrorMessage = object.getString("ErrorMessage");
            result.mTotalCount = object.getInt("TotalCount");

            if(result.mErrorCode == 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.SIMPLIFIED_CHINESE);

                list = new ArrayList<>();

                if(!object.isNull("Result")) {
                    JSONArray listArrary = object.getJSONArray("Result");
                    for (int i = 0; i < listArrary.length(); i++) {

                        head = new Task(
                                listArrary.getJSONObject(i).getString("TaskId"),
                                listArrary.getJSONObject(i).getString("TaskTypeId"),
                                listArrary.getJSONObject(i).getString("TaskTypeNo"),
                                listArrary.getJSONObject(i).getString("TaskTypeName"),
                                dateFormat.parse(listArrary.getJSONObject(i).getString("InspectDate")),
                                listArrary.getJSONObject(i).getString("BookerId"),
                                listArrary.getJSONObject(i).getInt("Status"));

                        list.add(head);
                    }
                }

                result.mResult = list;
            }
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        if(result.mErrorCode != 0) {
            result.mErrorMessage = translateMessage(result.mErrorMessage);
        }
        return  result;
    }

    // INSPECT：获取任务接口
    public static ApiResult<List<BaseInspect>> getInspectList(String taskType, String taskId){

        ApiResult<List<BaseInspect>> result = new ApiResult<>();

        BaseInspect head;
        List<BaseInspect> list;

        InspectItem item;
        List<InspectItem> itemList;

        try {
            String uri = getUriByTaskType(taskType);
            if(uri.length() == 0) {
                result.mErrorCode = -1;
                result.mErrorMessage = "非预期的TaskTypeNo：" + taskType;
                return result;
            } else {
                uri += "TaskDetais/" + taskId;
            }

            String paramString = "taskId=" + taskId;
            String resultString = getBase(uri, paramString, "", "");
            JSONObject object = new JSONObject(resultString);

            result.mErrorCode = object.getInt("ErrorCode");
            result.mErrorMessage = object.getString("ErrorMessage");
            result.mTotalCount = object.getInt("TotalCount");

            if(result.mErrorCode == 0) {
                int i, j;
                JSONArray itemArrary;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.SIMPLIFIED_CHINESE);

                list = new ArrayList<>();

                if(!object.isNull("Result")) {
                    JSONArray listArrary = object.getJSONArray("Result");
                    for (i = 0; i < listArrary.length(); i++) {
                        head = new BaseInspect(
                                listArrary.getJSONObject(i).getInt("RecNum"),
                                listArrary.getJSONObject(i).getString("BizCode"),
                                listArrary.getJSONObject(i).getString("TaskId"),
                                listArrary.getJSONObject(i).getString("InspectId"),
                                dateFormat.parse(listArrary.getJSONObject(i).getString("InspectDate")));

                        getInspectExtraData(taskType, head, listArrary.getJSONObject(i));

                        if (!listArrary.getJSONObject(i).isNull("InspectItems")) {
                            if (!listArrary.getJSONObject(i).isNull("InspectGroupId")) {
                                head.mInspectId = listArrary.getJSONObject(i).getString("InspectGroupId");
                            }

                            itemList = new ArrayList<>();
                            itemArrary = listArrary.getJSONObject(i).getJSONArray("InspectItems");
                            for (j = 0; j < itemArrary.length(); j++) {
                                item = new InspectItem(
                                        itemArrary.getJSONObject(j).getString("InspectId"),
                                        itemArrary.getJSONObject(j).getString("ItemId"),
                                        itemArrary.getJSONObject(j).getString("ItemNo"),
                                        itemArrary.getJSONObject(j).getString("ItemName"),
                                        itemArrary.getJSONObject(j).getString("ItemUnit"));
                                itemList.add(item);
                            }
                            head.mItems = itemList;
                        } else {
                            head.mItems = new ArrayList<>();
                        }

                        list.add(head);
                    }
                }

                result.mResult = list;
            }
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        if(result.mErrorCode != 0) {
            result.mErrorMessage = translateMessage(result.mErrorMessage);
        }
        return  result;
    }

    public static ApiResult<List<BaseInspect>> getLocalInspectList(String taskId, Context context){

        ApiResult<List<BaseInspect>> result = new ApiResult<>();

        BaseInspect head;
        List<BaseInspect> list;

        InspectItem item;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);

            // 读取本地SQLite数据库
            SqliteDBHelper dbHelper = new SqliteDBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // 获取用户信息
            Cursor cursor = db.query(SqliteDBHelper.TABLE_INSPECT, null,
                    "task_id = ?", new String[]{ taskId },
                    null, null, null, null);

            Cursor cursorItem;

            // 不断移动光标获取值
            list = new ArrayList<>();
            while (cursor.moveToNext()) {
                head = new BaseInspect(
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        dateFormat.parse(cursor.getString(6)));

                head.mInspectResult = cursor.getString(7);
                head.mInconformityDesc = cursor.getString(8);
                head.mComment = cursor.getString(9);
                head.mPictures = cursor.getString(10);
                head.mGroupId = cursor.getString(11);
                head.mGroupName = cursor.getString(12);
                head.mInspectNo = cursor.getString(13);
                head.mInspectName = cursor.getString(14);
                head.mInspectDesc = cursor.getString(15);
                head.mValueType = cursor.getString(16);
                if(cursor.getString(17).equals("1")) {
                    head.mShowButton = true;
                } else {
                    head.mShowButton = false;
                }
                head.mValueHint = cursor.getString(18);

                head.mItems = new ArrayList<>();

                cursorItem = db.query(SqliteDBHelper.TABLE_ITEM, null,
                        "inspect_group_id = ?", new String[]{ head.mInspectId },
                        null, null, null, null);
                while (cursorItem.moveToNext()) {
                    item = new InspectItem(
                            cursorItem.getString(1),
                            cursorItem.getString(2),
                            cursorItem.getString(3),
                            cursorItem.getString(4),
                            cursorItem.getString(5));

                    item.mInspectResult = cursorItem.getString(6);
                    item.mInconformityDesc = cursorItem.getString(7);
                    item.mComment = cursorItem.getString(8);
                    item.mPictures = cursorItem.getString(9);

                    head.mItems.add(item);
                }
                cursorItem.close();

                list.add(head);
            }

            // 关闭光标
            cursor.close();

            result.mErrorCode = 0;
            result.mErrorMessage = "";
            result.mTotalCount = 1;
            result.mResult = list;
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        return result;
    }

    public static void saveLocalInspectList(List<BaseInspect> inspects, String account, String appId, String taskId, Context context) {
        if(inspects != null) {
            SqliteDBHelper dbHelper = new SqliteDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //db.delete(SqliteDBHelper.TABLE_INSPECT, "account = ? and app_id = ?", new String[]{ account, appId });

            List<String> inspectIds = new ArrayList<>();
            Cursor cursor = db.query(SqliteDBHelper.TABLE_INSPECT, new String[]{ "inspect_id" },
                    "account = ? and app_id = ? and task_id = ?",
                    new String[]{ account, appId, taskId },
                    null, null, null, null);
            while (cursor.moveToNext()) {
                inspectIds.add(cursor.getString(0));
            }
            cursor.close();

            ContentValues values;
            for (BaseInspect item : inspects) {
                if(!inspectIds.contains(item.mInspectId)) {
                    values = new ContentValues();
                    values.put("account", account);
                    values.put("app_id", appId);
                    values.put("rec_num", item.mRecNum);
                    values.put("biz_code", item.mBizCode);
                    values.put("task_id", item.mTaskId);
                    values.put("inspect_id", item.mInspectId);
                    values.put("inspect_date", item.getInspectDate());
                    values.put("inspect_result", item.mInspectResult == null ? "" : item.mInspectResult);
                    values.put("inconformity_desc", item.mInconformityDesc == null ? "" : item.mInconformityDesc);
                    values.put("comment", item.mComment == null ? "" : item.mComment);
                    values.put("pic", item.mPictures == null ? "" : item.mPictures);
                    values.put("group_id", item.mGroupId == null ? "" : item.mGroupId);
                    values.put("group_name", item.mGroupName == null ? "" : item.mGroupName);
                    values.put("inspect_no", item.mInspectNo == null ? "" : item.mInspectNo);
                    values.put("inspect_name", item.mInspectName == null ? "" : item.mInspectName);
                    values.put("inspect_desc", item.mInspectDesc == null ? "" : item.mInspectDesc);
                    values.put("value_type", item.mValueType);
                    values.put("show_button", item.mShowButton);
                    values.put("value_hint", item.mValueHint);
                    db.insert(SqliteDBHelper.TABLE_INSPECT, null, values);

                    //db.delete(SqliteDBHelper.TABLE_ITEM, "inspect_id = ? ", new String[]{item.mInspectId});

                    if (item.mItems != null) {
                        for (InspectItem it : item.mItems) {
                            values = new ContentValues();
                            values.put("inspect_group_id", item.mInspectId);
                            values.put("inspect_id", it.mInspectId);
                            values.put("item_id", it.mItemId);
                            values.put("item_no", it.mItemNo);
                            values.put("item_name", it.mItemName);
                            values.put("item_unit", it.mItemUnit == null ? "" : it.mItemUnit);
                            values.put("inspect_result", it.mInspectResult == null ? "" : it.mInspectResult);
                            values.put("inconformity_desc", item.mInconformityDesc == null ? "" : item.mInconformityDesc);
                            values.put("comment", it.mComment == null ? "" : it.mComment);
                            values.put("pic", it.mPictures == null ? "" : it.mPictures);
                            db.insert(SqliteDBHelper.TABLE_ITEM, null, values);
                        }
                    }
                }
            }
        }
    }

    public static ApiResult<Integer> updateLocalInspectList(List<BaseInspect> inspects, Context context) {
        ApiResult<Integer> result = new ApiResult<>();
        int updateNum = 0;

        if(inspects != null) {
            SqliteDBHelper dbHelper = new SqliteDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values;
            for (BaseInspect item : inspects) {
                values = new ContentValues();
                values.put("inspect_result", item.mInspectResult == null ? "" : item.mInspectResult);
                values.put("inconformity_desc", item.mInconformityDesc == null ? "" : item.mInconformityDesc);
                values.put("comment", item.mComment == null ? "" : item.mComment);
                values.put("pic", item.mPictures == null ? "" : item.mPictures);
                updateNum += db.update(SqliteDBHelper.TABLE_INSPECT, values, "inspect_id = ?", new String[]{ item.mInspectId });

                if (item.mItems != null) {
                    for (InspectItem it : item.mItems) {
                        values = new ContentValues();
                        values.put("inspect_result", it.mInspectResult == null ? "" : it.mInspectResult);
                        values.put("inconformity_desc", it.mInconformityDesc == null ? "" : it.mInconformityDesc);
                        values.put("comment", it.mComment == null ? "" : it.mComment);
                        values.put("pic", it.mPictures == null ? "" : it.mPictures);
                        db.update(SqliteDBHelper.TABLE_ITEM, values, "inspect_id = ? and item_id = ?", new String[]{ it.mInspectId, it.mItemId });
                    }
                }
            }

            if(updateNum == inspects.size()) {
                result.mErrorCode = 0;
                result.mResult = updateNum;
            } else {
                result.mErrorCode = -1;
                result.mErrorMessage = String.format("共有 %1$d 条数据，保存了 %2$d 条！", inspects.size(), updateNum);
            }
        } else {
            result.mErrorCode = -1;
            result.mErrorMessage = "没有需要保存的数据！";
        }

        return result;
    }

    public static ApiResult<Integer> deleteLocalInspectList(List<BaseInspect> inspects, Context context) {
        ApiResult<Integer> result = new ApiResult<>();
        int deleteNum = 0;

        if(inspects != null) {
            SqliteDBHelper dbHelper = new SqliteDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (BaseInspect item : inspects) {
                deleteNum += db.delete(SqliteDBHelper.TABLE_INSPECT, "inspect_id = ?", new String[]{ item.mInspectId });
                if(item.mItems != null) {
                    for (InspectItem it : item.mItems) {
                        db.delete(SqliteDBHelper.TABLE_ITEM, "inspect_id = ?", new String[]{ it.mInspectId });
                    }
                }
            }

            if(deleteNum == inspects.size()) {
                result.mErrorCode = 0;
                result.mResult = deleteNum;
            } else {
                result.mErrorCode = -1;
                result.mErrorMessage = String.format("共有 %1$d 条数据，删除了 %2$d 条！", inspects.size(), deleteNum);
            }
        } else {
            result.mErrorCode = -1;
            result.mErrorMessage = "没有需要删除的数据！";
        }

        return result;
    }

    // INSPECT：保存任务
    public static ApiResult<Integer> saveInspect(String taskType, List<BaseInspect> inspectList, String groupId){

        ApiResult<Integer> result = new ApiResult<>();

        try {
            String uri = getUriByTaskType(taskType);
            if(uri.length() == 0) {
                result.mErrorCode = -1;
                result.mErrorMessage = "非预期的TaskTypeNo：" + taskType;
                return result;
            } else {
                uri += "SaveTask/";
            }

            JSONArray inspectArrary, itemArrary;
            JSONObject inspectObject, itemObject;

            inspectArrary = new JSONArray();
            for (BaseInspect inspect : inspectList) {
                if(groupId.isEmpty() || inspect.mGroupId.equals(groupId)) {
                    itemArrary = new JSONArray();
                    for (InspectItem item : inspect.mItems) {
                        itemObject = new JSONObject();
                        itemObject.put("ItemId", item.mItemId);
                        itemObject.put("ItemNo", item.mItemNo);
                        itemObject.put("ItemName", item.mItemName);
                        itemObject.put("ItemUnit", item.mItemUnit);
                        itemObject.put("InspectResult", item.mInspectResult);
                        itemObject.put("InconformityDesc", item.mInconformityDesc);
                        itemObject.put("Pictures", item.mPictures);
                        itemObject.put("Comment", item.mComment);

                        itemArrary.put(itemObject);
                    }

                    inspectObject = new JSONObject();
                    inspectObject.put("RecNum", inspect.mRecNum);
                    inspectObject.put("BizCode", inspect.mBizCode);
                    inspectObject.put("TaskId", inspect.mTaskId);
                    inspectObject.put("InspectId", inspect.mInspectId);
                    inspectObject.put("InspectDate", inspect.getInspectDate());
                    inspectObject.put("InspectResult", inspect.mInspectResult);
                    inspectObject.put("InconformityDesc", inspect.mInconformityDesc);
                    inspectObject.put("Pictures", inspect.mPictures);
                    inspectObject.put("Comment", inspect.mComment);
                    inspectObject.put("InspectItems", itemArrary);

                    inspectArrary.put(inspectObject);
                }
            }

            String paramsString = inspectArrary.toString();
            String resultString = postBase(uri, paramsString, "", "");
            JSONObject object = new JSONObject(resultString);

            result.mErrorCode = object.getInt("ErrorCode");
            result.mErrorMessage = object.getString("ErrorMessage");
            result.mTotalCount = object.getInt("TotalCount");
            result.mResult = object.getInt("Result");
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        if(result.mErrorCode != 0) {
            result.mErrorMessage = translateMessage(result.mErrorMessage);
        }
        return result;
    }

    // INSPECT：提交任务
    public static ApiResult<Integer> commitInspect(String taskType, List<BaseInspect> inspectList, String groupId){

        ApiResult<Integer> result = new ApiResult<>();

        try {
            String uri = getUriByTaskType(taskType);
            if(uri.length() == 0) {
                result.mErrorCode = -1;
                result.mErrorMessage = "非预期的TaskTypeNo：" + taskType;
                return result;
            } else {
                uri += "CommitTask/";
            }

            JSONArray inspectArrary, itemArrary;
            JSONObject inspectObject, itemObject;

            inspectArrary = new JSONArray();
            for (BaseInspect inspect : inspectList) {
                if(groupId.isEmpty() || inspect.mGroupId.equals(groupId)) {
                    itemArrary = new JSONArray();
                    for (InspectItem item : inspect.mItems) {
                        itemObject = new JSONObject();
                        itemObject.put("InspectId", item.mInspectId);
                        itemObject.put("ItemId", item.mItemId);
                        itemObject.put("ItemNo", item.mItemNo);
                        itemObject.put("ItemName", item.mItemName);
                        itemObject.put("ItemUnit", item.mItemUnit);
                        itemObject.put("InspectResult", item.mInspectResult);
                        itemObject.put("InconformityDesc", item.mInconformityDesc);
                        itemObject.put("Comment", item.mComment);

                        if (item.mPictures != null && item.mPictures.length() > 0) {
                            itemObject.put("Pictures", ImageUtils.bitmapToString(item.mPictures, false));
                        } else {
                            itemObject.put("Pictures", item.mPictures);
                        }

                        itemArrary.put(itemObject);
                    }

                    inspectObject = new JSONObject();
                    inspectObject.put("RecNum", inspect.mRecNum);
                    inspectObject.put("BizCode", inspect.mBizCode);
                    inspectObject.put("TaskId", inspect.mTaskId);
                    inspectObject.put("InspectId", inspect.mInspectId);
                    inspectObject.put("InspectDate", inspect.getInspectDate());
                    inspectObject.put("InspectResult", inspect.mInspectResult);
                    inspectObject.put("InconformityDesc", inspect.mInconformityDesc);
                    inspectObject.put("Comment", inspect.mComment);
                    inspectObject.put("InspectItems", itemArrary);

                    if (inspect.mPictures != null && inspect.mPictures.length() > 0) {
                        inspectObject.put("Pictures", ImageUtils.bitmapToString(inspect.mPictures, false));
                    } else {
                        inspectObject.put("Pictures", inspect.mPictures);
                    }

                    inspectArrary.put(inspectObject);
                }
            }

            String paramsString = inspectArrary.toString();
            String resultString = postBase(uri, paramsString, "", "");
            JSONObject object = new JSONObject(resultString);

            result.mErrorCode = object.getInt("ErrorCode");
            result.mErrorMessage = object.getString("ErrorMessage");
            result.mTotalCount = object.getInt("TotalCount");
            result.mResult = object.getInt("Result");
        } catch (Exception e) {
            result.mErrorMessage = e.getMessage();
        }

        if(result.mErrorCode != 0) {
            result.mErrorMessage = translateMessage(result.mErrorMessage);
        }
        return result;
    }

    private static String getUriByTaskType(String taskType) {
        switch (taskType) {
            case "101": // 岗位责任审核
                return "api/DutyInspect/";
            case "102": // 成品质量检查
                return "api/ProductQualityInspect/";
            case "103": // 成品重量检查
                return "api/ProductWeightInspect/";
            case "104": // 半成品重量检查
                return "api/ProductWeightInspect/";
            case "105": // 个人卫生检查
                return "api/HygieneInspect/";
            case "106": // 环境卫生检查
                return "api/AssanationInspect/";
            case "107": // 设备与工器具检查
                return "api/EquipmentInspect/";
            case "108": // 阳性检查
                return "api/PositiveInspect/";
            case "109": // 原材料重量检测
                return "api/ProductWeightInspect/";
            case "110": // 农药残留检查
                return "api/PesticideResidueInspect/";
            case "111": // 极性组分
                return "api/PolarGroupInspect/";
            case "112": // ATP检测
                return "api/AtpInspect/";
            case "113": // 周转箱清洗抽查 【缺失】":
                return "api/TurnoverBoxInspect/";
            case "114": // 配货抽查表
                return "api/PickInspect/";
            case "115": // 自定义任务
                return "api/CustomInspect/";
            default:
                return "";
        }
    }

    private static void getInspectExtraData(String taskType, BaseInspect head, JSONObject object) {
        try {
            switch (taskType) {
                case "101": // 岗位责任审核 DutyInspect
                    head.mGroupId = object.getString("WorkTeamId");
                    head.mGroupName = object.getString("WorkTeamName");
                    head.mInspectNo = "";
                    head.mInspectName = object.getString("WorkShopClauseNo");
                    head.mInspectDesc = object.getString("ClauseName");
                    head.mValueType = "Check";
                    head.mShowButton = true;
                    head.mValueHint = "";
                    break;
                case "102": // 成品质量检查 ProductQualityInspect
                    head.mGroupId = object.getString("CustomerId");
                    head.mGroupName = object.getString("CustomerShortName");
                    head.mInspectNo = object.getString("ProdItemNo");
                    head.mInspectName = object.getString("ProdItemName");
                    head.mInspectDesc = "";
                    head.mValueType = "EditText";
                    head.mShowButton = true;
                    head.mValueHint = "";
                    break;
                case "103": // 成品重量检查 ProductWeightInspect
                    head.mGroupId = object.getString("CustomerId");
                    head.mGroupName = object.getString("CustomerShortName");
                    head.mInspectNo = object.getString("ProdItemNo");
                    head.mInspectName = object.getString("ProdItemName");
                    head.mInspectDesc = "";
                    head.mValueType = "EditText";
                    head.mShowButton = false;
                    head.mValueHint = "";
                    break;
                case "104": // 半成品重量检查 ProductWeightInspect
                    head.mGroupId = object.getString("WorkShopId");
                    head.mGroupName = object.getString("WorkShopName");
                    head.mInspectNo = object.getString("ProdItemNo");
                    head.mInspectName = object.getString("ProdItemName");
                    head.mInspectDesc = "";
                    head.mValueType = "EditText";
                    head.mShowButton = false;
                    head.mValueHint = "";
                    break;
                case "109": // 原材料重量检测 ProductWeightInspect
                    head.mGroupId = object.getString("SupplierId");
                    head.mGroupName = object.getString("SupplierShortName");
                    head.mInspectNo = object.getString("ProdItemNo");
                    head.mInspectName = object.getString("ProdItemName");
                    head.mInspectDesc = "";
                    head.mValueType = "EditText";
                    head.mShowButton = false;
                    head.mValueHint = "";
                    break;
                case "105": // 个人卫生 HygieneInspect
                    head.mGroupId = object.getString("WorkTeamId");
                    head.mGroupName = object.getString("WorkTeamName");
                    head.mInspectNo = object.getString("EmpNo");
                    head.mInspectName = object.getString("EmpName");
                    head.mInspectDesc = "";
                    head.mValueType = "Check";
                    head.mShowButton = true;
                    head.mValueHint = "";
                    break;
                case "106": // 环境卫生检查 AssanationInspect
                    head.mGroupId = object.getString("WorkTeamId");
                    head.mGroupName = object.getString("WorkTeamName");
                    head.mInspectNo = object.getString("ItemNo");
                    head.mInspectName = object.getString("ItemName");
                    head.mInspectDesc = "";
                    head.mValueType = "Check";
                    head.mShowButton = true;
                    head.mValueHint = "";
                    break;
                case "107": // 设备检查 EquipmentInspect
                    head.mGroupId = object.getString("WorkShopId");
                    head.mGroupName = object.getString("WorkShopName");
                    head.mInspectNo = object.getString("EquipmentNo");
                    head.mInspectName = object.getString("EquipmentName");
                    head.mInspectDesc = object.getString("Barcode");
                    head.mValueType = "Check";
                    head.mShowButton = true;
                    head.mValueHint = "";
                    break;
                case "108": // 阳性检查 PositiveInspect
                    head.mGroupId = object.getString("WorkShopId");
                    head.mGroupName = object.getString("WorkShopName");
                    head.mInspectNo = object.getString("ItemNo");
                    head.mInspectName = object.getString("ItemName");
                    head.mInspectDesc = "";
                    head.mValueType = "Check";
                    head.mShowButton = true;
                    head.mValueHint = "";
                    break;
                case "110": // 农药残留检查 PesticideResidueInspect
                    head.mGroupId = object.getString("WorkShopId");
                    head.mGroupName = object.getString("WorkShopName");
                    head.mInspectNo = object.getString("ProdItemNo");
                    head.mInspectName = object.getString("ProdItemName");
                    head.mInspectDesc = object.getString("SupplierShortName");
                    head.mValueType = "Select"; //弱阳性，阳性，阴性
                    head.mShowButton = false;
                    head.mValueHint = "";
                    break;
                case "111": // 极性组分 PolarGroupInspect
                    head.mGroupId = object.getString("WorkShopId");
                    head.mGroupName = object.getString("WorkShopName");
                    head.mInspectNo = "";
                    head.mInspectName = object.getString("InspectArea");
                    head.mInspectDesc = "";
                    head.mValueType = "EditText"; //文本框
                    head.mShowButton = false;
                    head.mValueHint = "";
                    break;
                case "112": // ATP检测 AtpInspect
                    head.mGroupId = object.getString("WorkShopId");
                    head.mGroupName = object.getString("WorkShopName");
                    head.mInspectNo = object.getString("ItemNo");
                    head.mInspectName = object.getString("ItemName");
                    head.mInspectDesc = "";
                    head.mValueType = "EditText"; //文本框
                    head.mShowButton = false;
                    head.mValueHint = "";
                    break;
                case "113": // 周转箱清洗抽查 TurnoverBoxInspect
                    //head.mGroupId = object.getString("WorkShopId");
                    //head.mGroupName = object.getString("WorkShopName");
                    //head.mInspectNo = object.getString("ItemNo");
                    //head.mInspectName = object.getString("ProdItemName");
                    //head.mInspectDesc = object.getString("ProdItemNo");
                    //head.mValueType = "Expand";
                    //head.mShowButton = true;
                    //head.mValueHint = "";
                    break;
                case "114": // 配货抽查表 PickInspect
                    head.mGroupId = object.getString("RouteId");
                    head.mGroupName = object.getString("RouteName");
                    head.mInspectNo = object.getString("StoreNo");
                    head.mInspectName = object.getString("StoreName");
                    head.mInspectDesc = object.getString("PickerName");
                    head.mValueType = "Check";
                    head.mShowButton = false;
                    head.mValueHint = "";
                    break;
                case "115": // 自定义任务 CustomInspect
                    head.mGroupId = "";
                    head.mGroupName = "";
                    head.mInspectNo = "";
                    head.mInspectName = object.getString("ItemContent");
                    head.mInspectDesc = "";
                    head.mValueType = "EditText"; //文本框
                    head.mShowButton = true;
                    head.mValueHint = "";
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    private static String translateMessage(String message) {
        if(message != null) {
            if (message.indexOf("resolve host") > -1) {
                return "联网失败，请检查网络是否正常！";
            } else if (message.indexOf("authentication chalenges") > -1) {
                return "登录超时，请重新登录！";
            } else if(message.indexOf("on a null object reference") > -1) {
                return "联网失败，请检查网络是否正常！";
            }
        } else {
            return "联网失败，请检查网络是否常，稍后重试！";
        }
        return message;
    }


    // GET BASE
    private static String getBase(String uri, String params, String token, String auth){

        String resultString       = null;
        HttpURLConnection conn    = null;
        InputStream inputStream   = null;
        ByteArrayOutputStream bos = null;

        try {
            String srcUrl = KATAE_BASE_URL + uri;
            if(!params.equals("")) {
                srcUrl = srcUrl + "?" + params;
            }
            URL url = new URL(srcUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json,text/html;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Cache-Control", "max-age=0");
            conn.setRequestProperty("token", token);

            byte[] authEncBytes = Base64.encode(auth.getBytes(), Base64.DEFAULT);
            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (null != conn.getHeaderField("Content-Encoding")) {
                    inputStream = new GZIPInputStream(conn.getInputStream());
                } else {
                    inputStream = conn.getInputStream();
                }

                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[10240];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();

                byte[] resultByte = bos.toByteArray();
                resultString = new String(resultByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return  resultString;
    }

    // POST BASE
    private static String postBase(String uri, String params, String token, String auth){

        String resultString       = null;
        HttpURLConnection conn    = null;
        InputStream inputStream   = null;
        ByteArrayOutputStream bos = null;

        try {
            String srcUrl = KATAE_BASE_URL + uri;
            URL url = new URL(srcUrl );

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("token", token);

            byte[] authEncBytes = Base64.encode(auth.getBytes(), Base64.DEFAULT);
            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();

            //传入参数
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(params.getBytes());

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (null != conn.getHeaderField("Content-Encoding")) {
                    inputStream = new GZIPInputStream(conn.getInputStream());
                } else {
                    inputStream = conn.getInputStream();
                }

                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[10240];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();

                byte[] resultByte = bos.toByteArray();
                resultString = new String(resultByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultString;
    }

    private static String postTest(String uri, String params, String token, String auth){

        String resultString       = null;
        HttpURLConnection conn    = null;
        InputStream inputStream   = null;
        ByteArrayOutputStream bos = null;

        try {
            String srcUrl = KATAE_TEST_URL + uri;
            URL url = new URL(srcUrl );

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("token", token);

            byte[] authEncBytes = Base64.encode(auth.getBytes(), Base64.DEFAULT);
            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();

            //传入参数
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(params.getBytes());

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (null != conn.getHeaderField("Content-Encoding")) {
                    inputStream = new GZIPInputStream(conn.getInputStream());
                } else {
                    inputStream = conn.getInputStream();
                }

                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[10240];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();

                byte[] resultByte = bos.toByteArray();
                resultString = new String(resultByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultString;
    }

    // PUT BASE
    private static String putBase(String uri, String params, String token, String auth){

        String resultString       = null;
        HttpURLConnection conn    = null;
        InputStream inputStream   = null;
        ByteArrayOutputStream bos = null;

        try {
            String srcUrl = KATAE_BASE_URL + uri;
            URL url = new URL(srcUrl );

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("token", token);

            byte[] authEncBytes = Base64.encode(auth.getBytes(), Base64.DEFAULT);
            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();

            //传入参数
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(params.getBytes());

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (null != conn.getHeaderField("Content-Encoding")) {
                    inputStream = new GZIPInputStream(conn.getInputStream());
                } else {
                    inputStream = conn.getInputStream();
                }

                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[10240];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();

                byte[] resultByte = bos.toByteArray();
                resultString = new String(resultByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultString;
    }

    // DELETE BASE
    private static String deleteBase(String uri, String params, String token, String auth){

        String resultString       = null;
        HttpURLConnection conn    = null;
        InputStream inputStream   = null;
        ByteArrayOutputStream bos = null;

        try {
            String srcUrl = KATAE_BASE_URL + uri + "?" + params;
            URL url = new URL(srcUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
            conn.setRequestProperty("token", token);

            byte[] authEncBytes = Base64.encode(auth.getBytes(), Base64.DEFAULT);
            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (null != conn.getHeaderField("Content-Encoding")) {
                    inputStream = new GZIPInputStream(conn.getInputStream());
                } else {
                    inputStream = conn.getInputStream();
                }

                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[10240];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();

                byte[] resultByte = bos.toByteArray();
                resultString = new String(resultByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return  resultString;
    }
}
