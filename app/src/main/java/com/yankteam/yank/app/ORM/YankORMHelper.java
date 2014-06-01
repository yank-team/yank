package com.yankteam.yank.app.ORM;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yankteam.yank.app.models.ModelEntity;
import com.yankteam.yank.app.models.ModelNote;
import com.yankteam.yank.app.models.ModelUser;

import java.util.HashMap;

/*
 * Yank ORM Helper
 */
public class YankORMHelper extends SQLiteOpenHelper {

    // Database constants
    public static final String DB_NAME  = "yank.db";
    private static final int DB_VERSION = 1;

    // Constructor -- invoke SQLiteOpenHelper superconstructor
    public YankORMHelper(Context context) {
        // invoke SQLite to create database
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ModelEntity.SQL_CREATE_TABLE);
        db.execSQL(ModelNote.SQL_CREATE_TABLE);
        db.execSQL(ModelUser.SQL_CREATE_TABLE);
    }

    // Handle a database upgrade or downgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ModelEntity.SQL_DROP_TABLE);
        db.execSQL(ModelNote.SQL_DROP_TABLE);
        db.execSQL(ModelUser.SQL_DROP_TABLE);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
        onCreate(db);
    }
}
