package com.yankteam.yank.app.ORM;

/*
 * SQLTable -- Represents a table in SQL
 */
public class SQLTable {

    private String name;
    private SQLColumn[] columns;

    public SQLTable(String _name, SQLColumn[] _columns) {
        name    = _name;
        columns = _columns;
    }

    // getters
    public String getName() {
        return name;
    }
    public SQLColumn[] getColumns() {
        return columns;
    }
    public int numCols() {
        return columns.length;
    }

    // get a specific column
    public SQLColumn getColumn(int index) {
        return columns[index];
    }
}
