package de.paulo5.staticalt.auctions.MySQL;

import de.paulo5.staticalt.auctions.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQLDataBase {

    private final String dbHost;
    private final String dbUser;
    private final String dbPass;
    private final String dbName;
    private String dbPort;

    public MySQLDataBase(Config c) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        dbHost = c.get("host", String.class);
        dbPort = c.get("port", String.class);
        if (dbPort == null) {
            dbPort = "3306";
        }
        dbUser = c.get("user", String.class);
        dbPass = c.get("password", String.class);
        dbName = c.get("database", String.class);
    }

    public SQLSchema getSchema() throws Exception {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                    + "?allowPublicKeyRetrieval=true&useSSL=false&autoReconnect=true&enabledTLSProtocols=TLSv1.2", dbUser, dbPass);

            System.out.println("Connected to sql server: " + dbHost + ":" + dbPort);

            return new SQLSchema(con);
        } catch (SQLException e) {
            throw new Exception("Can't connect to sql server: " + dbHost + ":" + dbPort, e);
        }
    }
}
