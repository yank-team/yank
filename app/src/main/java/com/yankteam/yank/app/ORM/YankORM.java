package com.yankteam.yank.app.ORM;

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
        for (int i=0; i<entities.size(); i++) {
            Entity tmpEntity = entities.get(i);

            // convert doubles to floats
            // TODO: figure out if we can keep doubles on SQLite
            Float fLat = new Float(tmpEntity.getLat());
            Float fLng = new Float(tmpEntity.getLng());

            // encode insertion values
            insertVals.add(new SQLValue(tableEntity.getColumn(0), tmpEntity.getId()));
            insertVals.add(new SQLValue(tableEntity.getColumn(1), tmpEntity.getName()));
            insertVals.add(new SQLValue(tableEntity.getColumn(2), fLat));
            insertVals.add(new SQLValue(tableEntity.getColumn(3), fLng));

            // get the query
            String query = mSQLBuilder.tableInsert(tableEntity, insertVals);

            // execute the query
            Cursor sqlCursor = db.rawQuery(query, null);
            // TODO: if we want to do something with the inserted value, do it here
            sqlCursor.close();
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
            retStr.append(sqlCursor.toString());
            sqlCursor.moveToNext();
        }
        sqlCursor.close();
        close();
        return retStr.toString();
    }
}
