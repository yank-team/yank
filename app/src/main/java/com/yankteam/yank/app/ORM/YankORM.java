package com.yankteam.yank.app.ORM;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/*
 * Yank Object-Relational Model
 * provides really simple bindings for SQLite so we can cache yank-server data
 * without going crazy.
 */
public class YankORM {

    private SQLiteDatabase db;
    private YankORMHelper helper;

    public YankORM(Context context) {
        helper = new YankORMHelper(context);
    }

    /*
     * ORM Calls:
     *     Insert
     *     Update
     *     Delete
     *     SearchIDs
     */

    public int[] SearchIDs(int table, int[] IDs) {
        // build SQL SELECT query for all the given ids

        // execute query

        // parse result -- return list of unknown ids

        return null;
    }
}
