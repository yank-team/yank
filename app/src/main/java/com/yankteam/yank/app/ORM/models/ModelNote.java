package com.yankteam.yank.app.ORM.models;

import android.provider.BaseColumns;

/* ModelNote -- Note model SQL */
public final class ModelNote {
    // Empty constructor to prevent instantiation
    public ModelNote() {}

    // Columns of a model item
    public static abstract class NoteItem implements BaseColumns {
        public static final String TABLE_NAME    = "note";
        public static final String COLUMN_OWNER  = "owner";
        public static final String COLUMN_TARGET = "target";
    }

    /* SQL query to create the table if it doesn't exist */
    public static final String SQL_CREATE_TABLE =
            ModelSQL.CREATE_TABLE +
                    NoteItem.TABLE_NAME + "(" +
                        NoteItem._ID           + "," +
                        NoteItem.COLUMN_OWNER  + "," +
                        NoteItem.COLUMN_TARGET +
                    ");";

    /* SQL query to delete the table if it exists */
    public static final String SQL_DROP_TABLE =
            ModelSQL.DROP_TABLE + NoteItem.TABLE_NAME + ";";
}
