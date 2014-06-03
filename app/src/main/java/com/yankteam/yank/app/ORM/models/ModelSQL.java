package com.yankteam.yank.app.ORM.models;

/*
 * data types for SQL models. We use this to make
 * building SQL easier
 */
public abstract class ModelSQL {
    // Data types
    public static final String SQL_INTEGER = "INTEGER";
    public static final String SQL_FLOAT   = "REAL";
    public static final String SQL_STRING  = "VARCHAR";

    // SQL snippets
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS";
    public static final String DROP_TABLE   = "DROP TABLE IF EXISTS";

    public static final String SELECT_ALL   = "SELECT * FROM";
}
