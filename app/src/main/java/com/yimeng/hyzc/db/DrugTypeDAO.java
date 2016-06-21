package com.yimeng.hyzc.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.yimeng.hyzc.bean.DrugTypeBean;
import com.yimeng.hyzc.utils.MyApp;

/**
 * Created by 依萌 on 2016/6/21.
 */
public class DrugTypeDAO {
    private static DrugTypeDAO instace = new DrugTypeDAO();
    private final HYZCHelper helper;
    private final ContentResolver resolver = MyApp.getAppContext().getContentResolver();
    private final Uri notifyUri = Uri.parse("content://" + MyApp.getAppContext().getPackageName());

    public static final int ID_CODE = 1;
    public static final int ID_ICON = 2;
    public static final int ID_NAME = 3;

    private DrugTypeDAO() {
        helper = new HYZCHelper();
    }

    public static DrugTypeDAO getInstance() {
        return instace;
    }

    public Cursor getAllCursor() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DrugType", null);
        cursor.setNotificationUri(resolver, notifyUri);
        return cursor;
    }

    public Cursor getCursorByLimit(int limit) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DrugType limit ?", new String[]{String.valueOf(limit)});
        cursor.setNotificationUri(resolver, notifyUri);
        return cursor;
    }

    public int update(DrugTypeBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", bean.name);
        values.put("icon", bean.icon);
        int count = db.update("DrugType", values, " code = ?", new String[]{bean.TypeCode});
        if (count == 0) {
            return insert(bean);
        } else {
            resolver.notifyChange(notifyUri, null);
        }
        return 0;
    }

    private int insert(DrugTypeBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", bean.TypeCode);
        values.put("name", bean.name);
        values.put("icon", bean.icon);
        int count = (int) db.insert("DrugType", null, values);
        if (count != -1) {
            resolver.notifyChange(notifyUri, null);
        }
        return count;
    }
}
