package com.yankteam.yank.app.ORM;

import java.util.ArrayList;

/*
 * SQLBuilder
 * Builds SQL automatically, because seriously... fuck multiline strings.
 */
public class SQLBuilder {

    // Create a table
    public String createTable (String name, SQLColumn[] cols) {

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(name);
        sql.append(" (");

        // Iterate over columns
        for (int i=0; i<cols.length; i++){
            // get column declaration
            sql.append(cols[i].getColumnDecl());

            // append the correct number of commas
            if (i < cols.length-1) {
                sql.append(",");
            }
        }

        // finish the statement
        sql.append(");");
        return sql.toString();
    }

    // Drop a table
    public String dropTable(String name){
        return "DROP TABLE IF EXISTS" + name;
    }

    // Search for IDs with a SELECT statement
    // SELECT * FROM <table> WHERE id = <id> OR ... OR id = <id>;
    public String selectIDs (String table, int[] IDs) {
        StringBuilder sql = new StringBuilder();

        // Start selection statement
        sql.append("SELECT id FROM ");
        sql.append(table);
        sql.append(" WHERE ");

        // iterate over ids
        for(int i=0; i<IDs.length; i++) {

            // construct an OR complex...
            sql.append("id=");
            sql.append(IDs[i]);
            if (i < IDs.length-1) {
                sql.append(" OR ");
            }
        }

        // return finished statement
        sql.append(";");
        return sql.toString();
    }
}
