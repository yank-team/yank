package com.yankteam.yank.app.ORM;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Yank Object-Relational Model
 * provides really simple bindings for SQLite so we can cache yank-server data
 * without going crazy.
 */
public class YankORM extends SQLiteOpenHelper {
    // Entity table
    public static final String TABLE_ENTITY = "entity";
    public static final SQLColumn[] TABLE_ENTITY_COLS = {
        new SQLColumn("name", SQLColumn.TYPE_VARCHAR, ""),
        new SQLColumn("lat" , SQLColumn.TYPE_FLOAT  , ""),
        new SQLColumn("lng" , SQLColumn.TYPE_FLOAT  , "")
    };

    // Note table
    public static final String TABLE_NOTE = "note";
    public static final SQLColumn[] TABLE_NOTE_COLS = {
        new SQLColumn("owner"  , SQLColumn.TYPE_INTEGER, ""),
        new SQLColumn("target" , SQLColumn.TYPE_INTEGER, ""),
        new SQLColumn("content", SQLColumn.TYPE_VARCHAR, "")
    };

    // User data table
    public static final String TABLE_USER = "user";
    public static final SQLColumn[] TABLE_USER_COLS = {
        new SQLColumn("name", SQLColumn.TYPE_VARCHAR, "")
    };

    // Database constants
    public static final String DB_NAME  = "yank.db";
    private static final int DB_VERSION = 1;

    // the SQL builder
    private SQLBuilder mSQLBuilder;

    public YankORM(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        mSQLBuilder = new SQLBuilder();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // build table creation commands
        String sqlCreateTableEntity = mSQLBuilder.createTable(TABLE_ENTITY,
                TABLE_ENTITY_COLS);
        String sqlCreateTableNote = mSQLBuilder.createTable(TABLE_NOTE,
                TABLE_NOTE_COLS);
        String sqlCreateTableUser = mSQLBuilder.createTable(TABLE_USER,
                TABLE_USER_COLS);

        // execute creation commands
        db.execSQL(sqlCreateTableEntity);
        db.execSQL(sqlCreateTableNote);
        db.execSQL(sqlCreateTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Recrreate tables
        db.execSQL(mSQLBuilder.dropTable(TABLE_ENTITY));
        db.execSQL(mSQLBuilder.dropTable(TABLE_NOTE));
        db.execSQL(mSQLBuilder.dropTable(TABLE_USER));
        onCreate(db);
    }
}
