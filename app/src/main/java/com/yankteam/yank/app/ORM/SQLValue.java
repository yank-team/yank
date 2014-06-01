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

    private int type;

    /*
     * avert your eyes while I overload this constructor to hades
     */
    public SQLValue (SQLColumn _col, String val) {
        col    = _col;
        strVal = val;
        type   = SQLColumn.TYPE_VARCHAR;
    }
    public SQLValue (SQLColumn _col, Integer val) {
        col    = _col;
        intVal = val;
        type   = SQLColumn.TYPE_INTEGER;
    }
    public SQLValue (SQLColumn _col, Float val) {
        col      = _col;
        floatVal = val;
        type     = SQLColumn.TYPE_FLOAT;
    }

    public Object getValue() {
        switch (type) {
            case SQLColumn.TYPE_INTEGER:
                return intVal;
            case SQLColumn.TYPE_VARCHAR:
                return strVal;
            case SQLColumn.TYPE_FLOAT:
                return floatVal;
        }
        // TODO: throw exception
        return null;
    }

    public int getType(){
        return type;
    }
}
