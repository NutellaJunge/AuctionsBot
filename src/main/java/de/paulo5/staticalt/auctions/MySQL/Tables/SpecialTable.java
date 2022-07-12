package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.SQLTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialTable extends SQLTable {

    public SpecialTable(SQLTable table) {
        super(table.query, table.name);
    }

    public void createSpecial(Special special) throws SQLException {
        executeUpdate(
                "INSERT INTO %name% (auction, text) VALUES (" + special.getAuction() + ", '" + special.getText() + "')"
        );
        special.setId(execute("SELECT LAST_INSERT_ID() as id;", 1).get(0).get("id", Integer.class));
    }

    public List<Special> getAuctionSpecials(AuctionsTable.Auction auction) {
        List<Special> specials = new ArrayList<>();
        for (Config row : execute("SELECT * FROM %name% WHERE auction=" + auction.getId())) {
            specials.add(new Special(row));
        }
        return specials;
    }

    public int removeSpecialAtIndex(AuctionsTable.Auction auction, String[] index) throws SQLException {
        return executeUpdate("DELETE FROM %name% WHERE id IN (SELECT id FROM (SELECT Row_Number() OVER(PARTITION BY auction) AS r, id FROM %name% WHERE auction=" + auction.getId() + ") t WHERE r IN (" + String.join(",", index) + "))");
    }

    public static class Special {

        private final int auction;
        private final String text;
        private int id;

        public Special(int auction, String text) {
            this.auction = auction;
            this.text = text;
        }

        public Special(Config row) {
            this.id = row.get("id", Integer.class);
            this.auction = row.get("auction", Integer.class);
            this.text = row.get("text", String.class);
        }

        public int getAuction() {
            return auction;
        }

        public String getText() {
            return text;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public AccountTable.Account getAuctionData() {
            return Tables.accountTable.getAccount(auction);
        }
    }
}
