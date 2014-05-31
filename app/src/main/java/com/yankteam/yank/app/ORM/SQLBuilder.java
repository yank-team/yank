package com.yankteam.yank.app.ORM;

/*
 * SQLBuilder
 * Builds SQL automatically, because seriously... fuck multiline strings.
 */
public class SQLBuilder {

    // Create a table
    public String createTable (String name, SQLColumn[] cols) {

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(name);
        sql.append("(");

        // We always add a primary key
        SQLColumn pk = new SQLColumn("id", SQLColumn.TYPE_INTEGER,
                "primary key autoincrement");
        sql.append(pk.getColumnDecl());
        sql.append(",");

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

    // Search for IDs with a SELECT statement
    public void

    // Drop a table
    public String dropTable(String name){
        return "DROP TABLE IF EXISTS" + name;
    }
}
