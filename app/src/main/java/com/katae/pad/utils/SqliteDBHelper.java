package com.katae.pad.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * Created by longmz on 2018/07/09.
 */
public class SqliteDBHelper extends SQLiteOpenHelper {

    private static boolean mainTmpDirSet = false;

    private static final String TAG = "KataeSQLite";
    public static final String DB_NAME = "katae.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_USR = "usr";
    public static final String TABLE_APP = "app";
    public static final String TABLE_TASK0 = "task0";
    public static final String TABLE_INSPECT = "inspect";
    public static final String TABLE_ITEM = "item";

    //创建 usr 表的 sql 语句
    private static final String KATAE_CREATE_TABLE_USR = "create table " + TABLE_USR + "("
            + "account varchar(20) not null,"
            + "password varchar(20) not null,"
            + "imei varchar(20) not null,"
            + "emp_id varchar(20) not null,"
            + "user_name varchar(50) not null,"
            + "primary key (account));";

    //创建 app 表的 sql 语句
    private static final String KATAE_CREATE_TABLE_APP = "create table " + TABLE_APP + "("
            + "account varchar(20) not null,"
            + "app_id varchar(50) not null,"
            + "app_name varchar(50) not null,"
            + "app_icon varchar(10) not null,"
            + "app_type varchar(10) not null,"
            + "serial_no varchar(2) not null,"
            + "primary key (account,app_id));";

    //创建 Unfinished Tasks 表的 sql 语句
    private static final String KATAE_CREATE_TABLE_TASK0 = "create table " + TABLE_TASK0 + "("
            + "account varchar(20) not null,"
            + "app_id varchar(50) not null,"
            + "task_id varchar(50) not null,"
            + "type_id varchar(50) not null,"
            + "type_no varchar(20) not null,"
            + "type_name varchar(50) not null,"
            + "inspect_date varchar(20) not null,"
            + "booker_id varchar(50) not null,"
            + "status int not null,"
            + "task_num int not null,"
            + "primary key (account,app_id,task_id));";

    //创建 Inspect 表的 sql 语句
    private static final String KATAE_CREATE_TABLE_INSPECT = "create table " + TABLE_INSPECT + "("
            + "account varchar(20) not null,"
            + "app_id varchar(50) not null,"
            + "rec_num int not null,"
            + "biz_code varchar(50) not null,"
            + "task_id varchar(50) not null,"
            + "inspect_id varchar(50) not null,"
            + "inspect_date varchar(20) not null,"
            + "inspect_result varchar(20) not null,"
            + "inconformity_desc varchar(100) not null,"
            + "comment varchar(100) not null,"
            + "pic varchar(50) not null,"
            + "group_id varchar(50) not null,"
            + "group_name varchar(50) not null,"
            + "inspect_no varchar(50) not null,"
            + "inspect_name varchar(50) not null,"
            + "inspect_desc varchar(100) not null,"
            + "value_type varchar(50) not null,"
            + "show_button boolean not null,"
            + "value_hint varchar(50) not null,"
            + "primary key (account,app_id,task_id,inspect_id));";

    //创建 InspectItem 表的 sql 语句
    private static final String KATAE_CREATE_TABLE_ITEM = "create table " + TABLE_ITEM + "("
            + "inspect_group_id varchar(50) not null,"
            + "inspect_id varchar(50) not null,"
            + "item_id varchar(50) not null,"
            + "item_no varchar(50) not null,"
            + "item_name varchar(50) not null,"
            + "item_unit varchar(20) not null,"
            + "inspect_result varchar(20) not null,"
            + "inconformity_desc varchar(100) not null,"
            + "comment varchar(100) not null,"
            + "pic varchar(50) not null,"
            + "primary key (inspect_id,item_id));";

    public SqliteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * BUG:SQLiteCantOpenDatabaseException: unable to open database file (code 14)
     *
     * @return
     */
    /*@Override
    public SQLiteDatabase getReadableDatabase() {
        if (!mainTmpDirSet) {
            boolean rs = new File("/data/data/com.katae.pad/databases/main").mkdir();
            Log.d(TAG, rs + "");
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/com.katae.pad/databases/main'");
            mainTmpDirSet = true;
            return super.getReadableDatabase();
        }
        return super.getReadableDatabase();
    }*/

    // 当第一次创建数据库的时候，调用该方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        //输出创建数据库的日志信息
        Log.i(TAG, "create Database------------->");

        //execSQL函数用于执行SQL语句
        db.execSQL(KATAE_CREATE_TABLE_USR);
        db.execSQL(KATAE_CREATE_TABLE_APP);
        db.execSQL(KATAE_CREATE_TABLE_TASK0);
        db.execSQL(KATAE_CREATE_TABLE_INSPECT);
        db.execSQL(KATAE_CREATE_TABLE_ITEM);
    }

    //当更新数据库的时候执行该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //输出更新数据库的日志信息
        Log.i(TAG, "update Database------------->");
    }
}