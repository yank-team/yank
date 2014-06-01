package com.yankteam.yank.app.ORM;

/*
 * An SQL value is a value tied to a column on an SQL row
 */
public class SQLValue {

    private SQLColumn col;

    // encode possible values
    private String strVal;
    private Integer intVal;
    private Float floatVal;

    /*
     * avert your eyes while I overload this constructor to hades
     */
    public SQLValue (SQLColumn _col, String val) {
        col    = _col;
        strVal = val;
    }
    public SQLValue (SQLColumn _col, Integer val) {
        col    = _col;
        intVal = val;
    }
    public SQLValue (SQLColumn _col, Float val) {
        col      = _col;
        floatVal = val;
    }
}
