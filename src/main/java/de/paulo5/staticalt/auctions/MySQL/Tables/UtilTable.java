package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.SQLTable;

public class UtilTable extends SQLTable {

    public UtilTable(SQLTable table) {
        super(table.query, table.name);
    }

    public int getAccountByLog(int logId) {
        Config result = execute("SELECT snipedAccount FROM SnipeLog WHERE task=" + logId, 1).get(0);
        return result.get("snipedAccount", Integer.class);
    }

    public long getDiscordAccountByUser(int userId) {
        Config result = execute("SELECT id FROM DiscordAccounts WHERE user=" + userId, 1).get(0);
        return result.get("id", Long.class);
    }
}
