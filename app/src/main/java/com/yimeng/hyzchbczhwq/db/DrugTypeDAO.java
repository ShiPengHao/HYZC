package com.yimeng.hyzchbczhwq.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.yimeng.hyzchbczhwq.bean.DrugTypeBean;
import com.yimeng.hyzchbczhwq.utils.MyApp;

/**
 * Created by 依萌 on 2016/6/21.
 */
public class DrugTypeDAO {
    private static DrugTypeDAO instance = new DrugTypeDAO();
    private final HYZCHelper helper;
    private final ContentResolver resolver = MyApp.getAppContext().getContentResolver();
    public static final Uri DRUG_TYPE_URI = Uri.parse("content://" + MyApp.getAppContext().getPackageName());

    public static final int ID_CODE = 1;
    public static final int ID_ICON = 2;
    public static final int ID_NAME = 3;

    private DrugTypeDAO() {
        helper = new HYZCHelper();
    }

    public static DrugTypeDAO getInstance() {
        return instance;
    }

    public synchronized Cursor getAllCursor() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DrugType", null);
        cursor.setNotificationUri(resolver, DRUG_TYPE_URI);
        return cursor;
    }

    public synchronized Cursor getCursorByLimit(int limit) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from DrugType limit ?", new String[]{String.valueOf(limit)});
        cursor.setNotificationUri(resolver, DRUG_TYPE_URI);
        return cursor;
    }

    public synchronized void update(DrugTypeBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from DrugType where code = ?", new String[]{bean.TypeCode});
        if (cursor.moveToNext()) {
            if (null == bean.IconUrl){
                bean.IconUrl = "";
            }
            if (null == bean.CnName){
                bean.CnName = "";
            }
            if (!cursor.getString(ID_NAME).equals(bean.CnName) || !cursor.getString(ID_ICON).equals(bean.IconUrl)) {
                ContentValues values = new ContentValues();
                values.put("CnName", bean.CnName);
                values.put("IconUrl", bean.IconUrl);
                int count = db.update("DrugType", values, " code = ?", new String[]{bean.TypeCode});
                if (count > 0) {
                    resolver.notifyChange(DRUG_TYPE_URI, null);
                }
            }
        } else {
            insert(bean);
        }
    }

    private synchronized void insert(DrugTypeBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", bean.TypeCode);
        values.put("CnName", bean.CnName == null ? "" : bean.CnName);
        values.put("IconUrl", bean.IconUrl == null ? "" : bean.IconUrl);
        int count = (int) db.insert("DrugType", null, values);
        if (count != -1) {
            resolver.notifyChange(DRUG_TYPE_URI, null);
        }
    }
}
