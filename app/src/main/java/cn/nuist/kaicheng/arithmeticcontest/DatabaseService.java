package cn.nuist.kaicheng.arithmeticcontest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseService {

    private static AppSQLiteHelper appSQLiteHelper;
    private static SQLiteDatabase db;
    private final static int INIT_VALUE = 0;

    //初始化数据库
    public static void initDB(Context context) {
        appSQLiteHelper = new AppSQLiteHelper(context, "gameRecord.db", null, 1);
        db = appSQLiteHelper.getWritableDatabase();
    }

    //添加用户并返回是否已经存在
    protected static boolean addUser(String userName) {
        ArrayList<String> userNameList = new ArrayList<>();
        userNameList = DatabaseService.getUserList();
        for (int i = 0; i < userNameList.size(); i++) {
            if (userName.equals(userNameList.get(i))) {
                return true;//已经存在此用户
            }
        }
        ContentValues values = new ContentValues();
        values.put(AppSQLiteHelper.USER_NAME, userName);
        db.insert(AppSQLiteHelper.TABLE_NAME_INFO, null, values);
        initUserRecord(userName);
        initUserStatistic(userName);
        return false;//不存在此用户，新增用户
    }

    //获取所有用户列表
    public static ArrayList getUserList() {
        ArrayList<String> userNameList = new ArrayList<>();
        Cursor cursor = db.query(AppSQLiteHelper.TABLE_NAME_INFO, null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int nameIndex = cursor.getColumnIndex(AppSQLiteHelper.USER_NAME);
            String userName = cursor.getString(nameIndex);
            userNameList.add(userName);
        }
        return userNameList;
    }

    //通过用户名查询ID
    public static int lookForUserId(String userName) {
        int id = -1;
        Cursor cursor = db.query(AppSQLiteHelper.TABLE_NAME_INFO, null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int nameIndex = cursor.getColumnIndex(AppSQLiteHelper.USER_NAME);
            int idIndex = cursor.getColumnIndex(AppSQLiteHelper.ID);
            if (userName.equals(cursor.getString(nameIndex))) {
                id = cursor.getInt(idIndex);
            }
        }
        return id;
    }

    //初始化用户游戏记录
    public static void initUserRecord(String userName) {
        ContentValues values = new ContentValues();
        values.put(AppSQLiteHelper.USER_ID, String.valueOf(DatabaseService.lookForUserId(userName)));
        values.put(AppSQLiteHelper.USER_NAME, userName);
        values.put(AppSQLiteHelper.RIGHT_ANSWER_NUM, INIT_VALUE);
        values.put(AppSQLiteHelper.WRONG_ANSWER_NUM, INIT_VALUE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String formatDate = sdf.format(date);
        values.put(AppSQLiteHelper.LAST_DATE, formatDate.trim());
        db.insert(AppSQLiteHelper.TABLE_NAME_RECORD, null, values);
    }

    //初始化用户游戏统计记录（统计记录即为正确率记录表）
    public static void initUserStatistic(String userName) {
        ContentValues values = new ContentValues();
        values.put(AppSQLiteHelper.USER_ID, String.valueOf(DatabaseService.lookForUserId(userName)));
        values.put(AppSQLiteHelper.USER_NAME, userName);
        values.put(AppSQLiteHelper.ACCURACY, INIT_VALUE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String formatDate = sdf.format(date);
        values.put(AppSQLiteHelper.LAST_DATE, formatDate.trim());
        db.insert(AppSQLiteHelper.TABLE_NAME_STATISTIC, null, values);
    }

    //更新用户游戏记录
    public static void updateUserRecord(String userName, int rightAnswerNum, int wrongAnswerNum, String date) {
        ContentValues values = new ContentValues();
        int id = lookForUserId(userName);
        values.put(AppSQLiteHelper.USER_ID, id);
        values.put(AppSQLiteHelper.USER_NAME, userName);
        values.put(AppSQLiteHelper.RIGHT_ANSWER_NUM, rightAnswerNum);
        values.put(AppSQLiteHelper.WRONG_ANSWER_NUM, wrongAnswerNum);
        values.put(AppSQLiteHelper.LAST_DATE, date.trim());
        db.update(AppSQLiteHelper.TABLE_NAME_RECORD, values, AppSQLiteHelper.USER_ID + "=? and " + AppSQLiteHelper.LAST_DATE + "=?"
                , new String[]{String.valueOf(id), date});
    }

    //更新用户游戏统计记录
    public static void updateUserStatistics(String userName, int rightAnswerNum, int wrongAnswerNum, String date) {
        double accuracy = (double) rightAnswerNum / ((double) rightAnswerNum + (double) wrongAnswerNum);
        ContentValues values = new ContentValues();
        int id = lookForUserId(userName);
        values.put(AppSQLiteHelper.USER_ID, id);
        values.put(AppSQLiteHelper.USER_NAME, userName);
        values.put(AppSQLiteHelper.ACCURACY, accuracy);
        values.put(AppSQLiteHelper.LAST_DATE, date.trim());
        db.update(AppSQLiteHelper.TABLE_NAME_STATISTIC, values, AppSQLiteHelper.USER_ID + "=? and " + AppSQLiteHelper.LAST_DATE + "=?"
                , new String[]{String.valueOf(id), date});
    }

    //查询上一条记录是不是当天的
    public static boolean checkWhetherToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String formatDate = sdf.format(date);
        Cursor cursor = db.query(AppSQLiteHelper.TABLE_NAME_RECORD, null, null, null, null, null, null);
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            int dateIndex = cursor.getColumnIndex(AppSQLiteHelper.LAST_DATE);
            String thisDate = cursor.getString(dateIndex);
            if (formatDate.equals(thisDate)) {
                return true;
            }
        }
        return false;
    }

    //获取指定用户正确数量
    public static int getRightNum(String userName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String formatDate = sdf.format(date);
        Cursor cursor = db.query(AppSQLiteHelper.TABLE_NAME_RECORD
                , null
                , "user_name=? and last_date=?"
                , new String[]{userName, formatDate.trim()}
                , null, null, null);
        cursor.moveToFirst();
        int rightIndex = cursor.getColumnIndex(AppSQLiteHelper.RIGHT_ANSWER_NUM);
        int thisRightNum = cursor.getInt(rightIndex);
        return thisRightNum;
    }

    //获取指定用户错误数量
    public static int getWrongNum(String userName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String formatDate = sdf.format(date);
        Cursor cursor = db.query(AppSQLiteHelper.TABLE_NAME_RECORD
                , new String[]{AppSQLiteHelper.WRONG_ANSWER_NUM}
                , "user_name=? and last_date=?"
                , new String[]{userName, formatDate}
                , null, null, null);
        cursor.moveToFirst();
        int wrongIndex = cursor.getColumnIndex(AppSQLiteHelper.WRONG_ANSWER_NUM);
        int thisWrongNum = cursor.getInt(wrongIndex);
        return thisWrongNum;
    }

    //获取指定用户游戏日期列表
    public static List<String> getAllDate(String userName) {
        List<String> list = new ArrayList<>();
        String sqlDate = "select * from " + AppSQLiteHelper.TABLE_NAME_RECORD + " where user_name=?";
        Cursor cursor = db.rawQuery(sqlDate, new String[]{userName});
        int dateIndex = cursor.getColumnIndex(AppSQLiteHelper.LAST_DATE);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(dateIndex));
        }
        return list;
    }

    //获取指定用户指定日期的正确数量
    public static int getRightNum(String userName, String date) {
        int rightNum = 0;
        String sqlRecord = "select * from " + AppSQLiteHelper.TABLE_NAME_RECORD + " where user_name=? and last_date=?";
        Cursor cursorRecord = db.rawQuery(sqlRecord, new String[]{userName, date});
        int rightIndex = cursorRecord.getColumnIndex(AppSQLiteHelper.RIGHT_ANSWER_NUM);
        while (cursorRecord.moveToNext()) {
            rightNum = (cursorRecord.getInt(rightIndex));
        }
        return rightNum;
    }

    //获取指定用户指定日期的错误数量
    public static int getWrongNum(String userName, String date) {
        int wrongNum = 0;
        String sqlRecord = "select * from " + AppSQLiteHelper.TABLE_NAME_RECORD + " where user_name=? and last_date=?";
        Cursor cursorRecord = db.rawQuery(sqlRecord, new String[]{userName, date});
        int rightIndex = cursorRecord.getColumnIndex(AppSQLiteHelper.WRONG_ANSWER_NUM);
        while (cursorRecord.moveToNext()) {
            wrongNum = (cursorRecord.getInt(rightIndex));
        }
        return wrongNum;
    }

    //获取指定用户指定日期的正确率
    public static double getAccuracy(String userName, String date) {
        double accuracy = 0;
        String sqlStatistic = "select * from " + AppSQLiteHelper.TABLE_NAME_STATISTIC + " where user_name=? and last_date=?";
        Cursor cursorStatistic = db.rawQuery(sqlStatistic, new String[]{userName, date});
        int accuracyIndex = cursorStatistic.getColumnIndex(AppSQLiteHelper.ACCURACY);
        while (cursorStatistic.moveToNext()) {
            accuracy = (cursorStatistic.getDouble(accuracyIndex));
        }
        return accuracy;
    }
}
