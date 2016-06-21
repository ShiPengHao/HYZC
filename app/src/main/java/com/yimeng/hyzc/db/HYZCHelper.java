package com.yimeng.hyzc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yimeng.hyzc.utils.MyApp;

/**
 * Created by 依萌 on 2016/6/21.
 */
public class HYZCHelper extends SQLiteOpenHelper {
    public HYZCHelper() {
        super(MyApp.getAppContext(), "hyzc.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists DrugType (" +
                "_id integer primary key autoincrement, " +
                "code varchar(16), " +
                "icon varchar(16), " +
                "name varchar(16) " +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
