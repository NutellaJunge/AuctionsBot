package de.paulo5.staticalt.auctions.MySQL;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLSchema {

    private Connection con;

    public SQLSchema(Connection con) {
        this.con = con;
    }

    public SQLTable getTable(String name) {
        try {
            return new SQLTable(con.createStatement(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SQLTable getNoNameTable() {
        return getTable("");
    }
}
