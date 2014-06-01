package com.yankteam.yank.app.ORM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yankteam.yank.app.components.Entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * Yank Object-Relational Model
 * provides really simple bindings for SQLite so we can cache yank-server data
 * without going crazy.
 */
public class YankORM {

    private SQLiteDatabase db;
    private YankORMHelper helper;
    private SQLBuilder mSQLBuilder;

    public YankORM(Context context) {
        helper      = new YankORMHelper(context);
        mSQLBuilder = new SQLBuilder();
    }

    public void openWritable() throws SQLException {
        db = helper.getWritableDatabase();
    }
    public void openReadable() throws SQLException {
        db = helper.getReadableDatabase();
    }

    public void close() {
        helper.close();
    }
    
    /*
     * ORM Calls:
     *     SearchIDs
     *     Insert
     *     Update
     *     Delete
     */

    public ArrayList<Integer> searchIDs(String table, int[] IDs) throws SQLException {
        openReadable();

        ArrayList<Integer> retList = new ArrayList<Integer>();

        // build SQL SELECT query for all the given ids
        String query = mSQLBuilder.selectIDs(table, IDs);

        // execute query
        Cursor sqlCursor = db.rawQuery(query, null);
        sqlCursor.moveToFirst();

        while (!sqlCursor.isAfterLast()) {
            retList.add(sqlCursor.getInt(0));
            sqlCursor.moveToNext();
        }

        // parse result -- return list of unknown ids
        close();
        return retList;
    }

    public void insertEntities (List<Entity> entities) throws SQLException {
        openWritable();

        SQLTable tableEntity = helper.getTableEntity();

        // build SQLValues
        ArrayList<SQLValue> insertVals = new ArrayList<SQLValue>();
        for (Entity tmpEntity : entities) {
            // encode insertion values
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", tmpEntity.getId());
            contentValues.put("name", tmpEntity.getName());
            contentValues.put("lat", tmpEntity.getLat());
            contentValues.put("lng", tmpEntity.getLng());

            // Insert values
            db.insert("entity", null, contentValues);
        }
        close();
    }

    /*
     * Log contents of database for debug purposes
     */
    public String printORM() throws SQLException{
        openReadable();

        Cursor sqlCursor = db.rawQuery("SELECT * FROM entity;", null);
        StringBuilder retStr = new StringBuilder();

        sqlCursor.moveToFirst();
        while (!sqlCursor.isAfterLast()) {
            retStr.append(sqlCursor.getString(1));
            sqlCursor.moveToNext();
        }
        sqlCursor.close();
        close();
        return retStr.toString();
    }
}
