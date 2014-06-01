package com.yankteam.yank.app.ORM;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/*
 * Yank ORM Helper
 */
public class YankORMHelper extends SQLiteOpenHelper {

    // Table IDs
    public static final String ENTITY = "entity";
    public static final String NOTE   = "note";
    public static final String USER   = "user";

    // Database constants
    public static final String DB_NAME  = "yank.db";
    private static final int DB_VERSION = 1;

    // tables proper
    private SQLTable mTableEntity;
    private SQLTable mTableNote;
    private SQLTable mTableUser;

    // the SQL builder
    private SQLBuilder mSQLBuilder;

    public YankORMHelper(Context context) {
        // invoke SQLite to create database
        super(context, DB_NAME, null, DB_VERSION);

        // Entity table
        SQLColumn[] entityCols = {
            new SQLColumn("id", SQLColumn.TYPE_INTEGER, "primary key autoincrement"),
            new SQLColumn("name", SQLColumn.TYPE_VARCHAR, ""),
            new SQLColumn("lat" , SQLColumn.TYPE_FLOAT  , ""),
            new SQLColumn("lng" , SQLColumn.TYPE_FLOAT  , "")
        };
        mTableEntity = new SQLTable(ENTITY, entityCols);

        // Note table
        SQLColumn[] noteCols = {
            new SQLColumn("id", SQLColumn.TYPE_INTEGER, "primary key autoincrement"),
            new SQLColumn("name", SQLColumn.TYPE_VARCHAR, ""),
            new SQLColumn("lat" , SQLColumn.TYPE_FLOAT  , ""),
            new SQLColumn("lng" , SQLColumn.TYPE_FLOAT  , "")
        };
        mTableNote = new SQLTable(NOTE, noteCols);

        // User table
        SQLColumn[] userCols = {
            new SQLColumn("id", SQLColumn.TYPE_INTEGER, "primary key autoincrement"),
            new SQLColumn("owner"  , SQLColumn.TYPE_INTEGER, ""),
            new SQLColumn("target" , SQLColumn.TYPE_INTEGER, ""),
            new SQLColumn("content", SQLColumn.TYPE_VARCHAR, "")
        };
        mTableUser = new SQLTable(USER, userCols);

        // instantiate builder
        mSQLBuilder = new SQLBuilder();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // build table creation commands
        String sqlCreateTableEntity = mSQLBuilder.createTable(mTableEntity.getName(),
                mTableEntity.getColumns());
        String sqlCreateTableNote = mSQLBuilder.createTable(mTableNote.getName(),
                mTableNote.getColumns());
        String sqlCreateTableUser = mSQLBuilder.createTable(mTableNote.getName(),
                mTableNote.getColumns());

        // execute creation commands
        db.execSQL(sqlCreateTableEntity);
        db.execSQL(sqlCreateTableNote);
        db.execSQL(sqlCreateTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Recrreate tables
        db.execSQL(mSQLBuilder.dropTable(ENTITY));
        db.execSQL(mSQLBuilder.dropTable(NOTE));
        db.execSQL(mSQLBuilder.dropTable(USER));
        onCreate(db);
    }

    // table getters
    public SQLTable getTableEntity () {
        return mTableEntity;
    }
    public SQLTable getTableNote () {
        return mTableNote;
    }
    public SQLTable getTableUser () {
        return mTableUser;
    }
}
