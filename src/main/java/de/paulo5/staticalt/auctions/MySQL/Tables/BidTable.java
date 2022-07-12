package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.SQLTable;

import java.sql.SQLException;

public class BidTable extends SQLTable {

    public BidTable(SQLTable table) {
        super(table.query, table.name);
    }

    public void createBid(Bid bid) throws SQLException {
        executeUpdate(
                "INSERT INTO %name% "
                        + "(auction, bidder, price, message) "
                        + "VALUES "
                        + "(" + bid.getAuction() + ", " + bid.getBidder() + ", " + bid.getPrice() + ", '" + bid.getMessage() + "')"
        );
        bid.setId(execute("SELECT LAST_INSERT_ID() as id;", 1).get(0).get("id", Integer.class));
    }

    public Bid getHighestBidOfAuction(int auctionId) {
        try {
            Config highest = execute("SELECT * FROM %name% WHERE auction=" + auctionId + " ORDER BY price DESC LIMIT 1", 1).get(0);
            return new Bid(highest);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Bid getBidByMessageId(long messageId) {
        Config bid = execute("SELECT * FROM %name% WHERE message=" + messageId, 1).get(0);
        return new Bid(bid);
    }

    public void removeBid(Bid bid) throws SQLException {
        executeUpdate("DELETE FROM %name% WHERE id=" + bid.getId());
    }

    public static class Bid {

        private final int auction;
        private final int bidder;
        private final double price;
        private int id;
        private long message;

        public Bid(int auction, int bidder, double price) {
            this.auction = auction;
            this.bidder = bidder;
            this.price = price;
        }

        public Bid(Config row) {
            this.id = row.get("id", Integer.class);
            this.auction = row.get("auction", Integer.class);
            this.bidder = row.get("bidder", Integer.class);
            this.message = row.get("message", Long.class);
            this.price = Math.round(row.get("price", Double.class) * 100.0) / 100.0;
        }

        public double getPrice() {
            return price;
        }

        public int getBidder() {
            return bidder;
        }

        public long getBidderDiscordId() {
            return Tables.utilTable.getDiscordAccountByUser(bidder);
        }

        public int getAuction() {
            return auction;
        }

        public long getMessage() {
            return message;
        }

        public void setMessage(long message) {
            this.message = message;
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
