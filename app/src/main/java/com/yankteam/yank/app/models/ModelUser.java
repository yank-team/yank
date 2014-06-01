package com.yankteam.yank.app.models;

import android.provider.BaseColumns;

/* ModelUser -- User model SQL */
public class ModelUser {
    // Empty constructor to prevent instantiation
    public ModelUser() {}

    // columns of a model item
    public static abstract class UserItem implements BaseColumns {
        public static final String TABLE_NAME  = "user";
        public static final String COLUMN_NAME = "name";
    }

    /* SQL query to create the table if it doesn't exist */
    public static final String SQL_CREATE_TABLE =
            ModelSQL.CREATE_TABLE +
                    UserItem.TABLE_NAME + "(" +
                        UserItem._ID         + "," +
                        UserItem.COLUMN_NAME +
                    ")";

    /* SQL query to delete the table if it exists */
    public static final String SQL_DROP_TABLE =
            ModelSQL.DROP_TABLE + UserItem.TABLE_NAME + ";";
}
