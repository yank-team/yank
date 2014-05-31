package com.yankteam.yank.app.ORM;

/*
 * SQLColumn -- represents a data type being stored in SQL
 */
public class SQLColumn {

    // Type constants
    public static final int TYPE_INTEGER = 0;
    public static final int TYPE_VARCHAR = 1;
    public static final int TYPE_FLOAT   = 2;

    // Param constants

    // attributes include name and type for now
    private String name;
    private int type;
    private String attrs;

    /*
     * TODO: make the attributes more sophisticated -- ie, make this a bitmap and
     * make it so that we can use constants to turn bits on and off with the OR
     * operator
     */

    public SQLColumn(String _name, int _type, String _attrs) {
        name  = _name;
        type  = _type;
        attrs = _attrs;
    }

    // Construct a column declaration statement
    public String getColumnDecl() {
        StringBuilder ret = new StringBuilder();
        ret.append(name);
        ret.append(" ");
        ret.append(typeDeclString());
        ret.append(" ");
        ret.append(attrs);
        return ret.toString();
    }

    // Get a string for a type declaration
    private String typeDeclString () {
        // give back a string depending on what type it is
        switch (type) {
            case TYPE_INTEGER:
                return "INTEGER";
            case TYPE_VARCHAR:
                return "VARCHAR";
            case TYPE_FLOAT:
                return "FLOAT";

            // TODO: throw exception
            default:
                return "INTEGER";
        }
    }

    // getters
    public String getName() {
        return name;
    }
}
