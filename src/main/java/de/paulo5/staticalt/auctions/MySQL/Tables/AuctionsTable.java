package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.SQLTable;

import java.sql.SQLException;
import java.util.List;

public class AuctionsTable extends SQLTable {

    public AuctionsTable(SQLTable table) {
        super(table.query, table.name);
    }

    public void createAuction(Auction auction) throws SQLException {
        executeUpdate(
                "INSERT INTO %name% "
                        + "(account, bin, min, channel, header, role) "
                        + "VALUES "
                        + "(" + auction.getAccount() + ", " + auction.getBin() + ", " + auction.getMin() + ", '" + auction.getChannel() + "', '" + auction.getHeader() + "', '" + auction.getRole() + "')"
        );
    }

    public Auction getAuctionByChannel(long channelId) {
        Config result = execute("SELECT * FROM %name% WHERE channel=" + channelId, 1).get(0);
        return new Auction(result);
    }

    public Auction getAuctionByAccount(int account) {
        try {
            Config result = execute("SELECT * FROM %name% WHERE account=" + account, 1).get(0);
            return new Auction(result);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void closeAuction(Auction auction) throws SQLException {
        executeUpdate(
                "UPDATE %name% SET closed=1" + (auction.getBuyer() != 0 ? ", buyer=" + auction.getBuyer() + ", buyPrice=" + auction.getBuyPrice() : "") + " WHERE account=" + auction.getId()
        );
    }

    public void changeBinMin(Auction auction) throws SQLException {
        executeUpdate(
                "UPDATE %name% SET bin=" + auction.getBin() + ", min=" + auction.getMin() + " WHERE account=" + auction.getId()
        );
    }

    public void deleteAuction(Auction auction) throws SQLException {
        executeUpdate(
                "DELETE FROM %name% WHERE account=" + auction.getId()
        );
    }

    public static class Auction {

        private final int account;
        private double bin;
        private double min;
        private boolean closed = false;
        private long channel;
        private long header;
        private long role;
        private int buyer;
        private double buyPrice;
        private int vouche;

        public Auction(int account, double bin, double min) {
            this.account = account;
            this.bin = bin;
            this.min = min;
        }

        public Auction(Config row) {
            this.account = row.get("account", Integer.class);
            this.bin = Math.round(row.get("bin", Double.class) * 100.0) / 100.0;
            this.min = Math.round(row.get("min", Double.class) * 100.0) / 100.0;
            this.closed = row.get("closed", Boolean.class);
            this.channel = row.get("channel", Long.class);
            this.header = row.get("header", Long.class);
            this.role = row.get("role", Long.class);
            this.buyer = row.get("buyer", Integer.class);
            this.buyPrice = Math.round(row.get("buyPrice", Double.class) * 100.0) / 100.0;
            this.vouche = row.get("vouche", Integer.class);
        }

        public double getBin() {
            return bin;
        }

        public void setBin(double bin) {
            this.bin = bin;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public boolean isClosed() {
            return closed;
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }

        public int getBuyer() {
            return buyer;
        }

        public void setBuyer(int buyer) {
            this.buyer = buyer;
        }

        public long getBuyerDiscordId() {
            return Tables.utilTable.getDiscordAccountByUser(buyer);
        }

        public double getBuyPrice() {
            return buyPrice;
        }

        public void setBuyPrice(double buyPrice) {
            this.buyPrice = buyPrice;
        }

        public int getVouche() {
            return vouche;
        }

        public void setVouche(int vouche) {
            this.vouche = vouche;
        }

        public int getAccount() {
            return account;
        }

        public long getChannel() {
            return channel;
        }

        public void setChannel(long channel) {
            this.channel = channel;
        }

        public long getRole() {
            return role;
        }

        public void setRole(long role) {
            this.role = role;
        }

        public long getHeader() {
            return header;
        }

        public void setHeader(long header) {
            this.header = header;
        }

        public int getId() {
            return account;
        }

        public AccountTable.Account getAccountData() {
            return Tables.accountTable.getAccount(account);
        }

        @Override
        public String toString() {
            return "Auction{" +
                    "account=" + account +
                    ", bin=" + bin +
                    ", min=" + min +
                    ", closed=" + closed +
                    ", channel=" + channel +
                    ", header=" + header +
                    ", role=" + role +
                    ", buyer=" + buyer +
                    ", buyPrice=" + buyPrice +
                    ", vouche=" + vouche +
                    '}';
        }

        public List<SpecialTable.Special> getSpecials() {
            return Tables.specialTable.getAuctionSpecials(this);
        }
    }
}
