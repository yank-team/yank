package com.yankteam.yank.app.ORM.models;

import android.provider.BaseColumns;

/* ModelEntity -- Entity SQL model */
public final class ModelEntity {

    // Empty constructor to prevent instantiation
    public ModelEntity() {}

    // Columns of a model item
    public static abstract class EntityItem implements BaseColumns {
        public static final String TABLE_NAME  = "entity";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAT  = "lat";
        public static final String COLUMN_LNG  = "lng";
    }

    /* SQL query to create the table if it doesn't exist */
    public static final String SQL_CREATE_TABLE =
            ModelSQL.CREATE_TABLE +
                   EntityItem.TABLE_NAME + "(" +
                       EntityItem._ID         + "," +
                       EntityItem.COLUMN_NAME + "," +
                       EntityItem.COLUMN_LAT  + "," +
                       EntityItem.COLUMN_LNG  +
                   ");";

    /* SQL query to delete the table if it exists */
    public static final String SQL_DROP_TABLE =
            ModelSQL.DROP_TABLE + EntityItem.TABLE_NAME + ";";
}
