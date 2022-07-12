package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.SQLTable;

import java.sql.SQLException;
import java.sql.Timestamp;

public class SnipeFeedTable extends SQLTable {

    public SnipeFeedTable(SQLTable table) {
        super(table.query, table.name);
    }

    public void createSnipeFeed(SnipeFeed snipe) throws SQLException {
        executeUpdate("INSERT INTO %name% VALUES (" + snipe.getLog() + ", " + snipe.getSearches() + ", '" + snipe.getMessage() + "', '" + new Timestamp(snipe.getTime()) + "')");
    }

    public SnipeFeed getSnipeFeedByAccount(int account) {
        try {
            Config result = execute("SELECT %name%.* FROM %name% LEFT JOIN SnipeLog ON log=task WHERE snipedAccount=" + account, 1).get(0);
            return new SnipeFeed(result);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static class SnipeFeed {

        private final int log;
        private final int searches;
        private final long time;
        private final int account;
        private long message;

        public SnipeFeed(int log, int searches, long time) {
            this.searches = searches;
            this.log = log;
            this.time = time;
            this.account = Tables.utilTable.getAccountByLog(log);
        }

        public SnipeFeed(Config data) {
            this.searches = data.get("searches", Integer.class);
            this.log = data.get("log", Integer.class);
            this.time = data.get("time", Long.class);
            this.message = data.get("message", Long.class);
            this.account = Tables.utilTable.getAccountByLog(log);
        }

        public int getLog() {
            return log;
        }

        public int getSearches() {
            return searches;
        }

        public long getTime() {
            return time;
        }

        public long getMessage() {
            return message;
        }

        public void setMessage(long message) {
            this.message = message;
        }

        public int getAccount() {
            return account;
        }

        public AccountTable.Account getAccountData() {
            return Tables.accountTable.getAccount(account);
        }
    }
}
