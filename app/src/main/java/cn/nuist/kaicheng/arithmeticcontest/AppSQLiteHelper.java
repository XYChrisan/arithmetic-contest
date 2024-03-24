package cn.nuist.kaicheng.arithmeticcontest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AppSQLiteHelper extends SQLiteOpenHelper {

    public static String ID = "id";
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String TABLE_NAME_INFO = "user_info";
    public static String RIGHT_ANSWER_NUM = "right_answer_num";
    public static String WRONG_ANSWER_NUM = "wrong_answer_num";
    public static String LAST_DATE = "last_date";
    public static String TABLE_NAME_RECORD = "game_record_info";
    public static String ACCURACY = "accuracy";
    public static String TABLE_NAME_STATISTIC = "game_record_statistic";

    public AppSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //初始化表
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建用户信息表
        //表总共两列，分别为：用户ID（自增的ID）、用户名
        String sqlInfo = "create table if not exists " + TABLE_NAME_INFO + "("
                + ID + " integer primary key autoincrement,"
                + USER_NAME + " char);";
        db.execSQL(sqlInfo);
        //创建游戏记录表
        //表总共五列，分别为：用户ID、用户名、正确数、错误数、日期
        String sqlRecord = "create table if not exists " + TABLE_NAME_RECORD + "("
                + USER_ID + " integer,"
                + USER_NAME + " char,"
                + RIGHT_ANSWER_NUM + " int,"
                + WRONG_ANSWER_NUM + " int,"
                + LAST_DATE + " char);";
        db.execSQL(sqlRecord);
        //创建正确率记录表
        //表总共四列，分别为：用户ID、用户名、正确率、日期
        String sqlRecordStatistic = "create table if not exists " + TABLE_NAME_STATISTIC + "("
                + USER_ID + " integer,"
                + USER_NAME + " char,"
                + ACCURACY + " char,"
                + LAST_DATE + " char);";
        db.execSQL(sqlRecordStatistic);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
