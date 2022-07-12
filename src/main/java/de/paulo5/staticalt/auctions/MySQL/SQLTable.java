package de.paulo5.staticalt.auctions.MySQL;

import de.paulo5.staticalt.auctions.Config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;


public class SQLTable {

    public Statement query;
    public String name;

    public SQLTable(Statement query, String name) {
        this.query = query;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected ArrayList<Config> execute(String sql, int limit) throws IndexOutOfBoundsException {
        try {
            ArrayList<Config> list = new ArrayList<>();
            if (!sql.endsWith(";")) {
                sql += ";";
            }
            sql = sql.replace("%name%", name);
            ResultSet result = query.executeQuery(sql);
            while (result.next()) {
                Config row = new Config();
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    Object value;
                    switch (result.getMetaData().getColumnType(i)) {
                        case Types.TIMESTAMP:
                            value = result.getTimestamp(i).getTime();
                            break;
                        case Types.BIGINT:
                            value = result.getBigDecimal(i).intValue();
                            break;
                        default:
                            value = result.getObject(i);
                    }
                    row.set(result.getMetaData().getColumnName(i), value);
                }
                list.add(row);
                if (limit != -1 && list.size() >= limit) {
                    break;
                }
            }
            if (limit != -1 && list.size() < limit) {
                throw new IndexOutOfBoundsException("To low Results Count for Set limit");
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    protected ArrayList<Config> execute(String sql) {
        return execute(sql, -1);
    }

    protected int executeUpdate(String sql) throws SQLException {
        sql = sql.replace("%name%", name);
        return query.executeUpdate(sql);
    }
}
