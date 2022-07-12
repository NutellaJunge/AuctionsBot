package de.paulo5.staticalt.auctions.MySQL.Tables;


import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.MySQLDataBase;
import de.paulo5.staticalt.auctions.MySQL.SQLSchema;

public class Tables {

    public static AccountTable accountTable;
    public static SnipeFeedTable snipeFeedTable;
    public static AuctionsTable auctionsTable;
    public static BidTable bidTable;
    public static SpecialTable specialTable;
    public static UserTable userTable;
    public static RoleTable roleTable;
    public static UtilTable utilTable;

    public static void registerDataBase(Config config) {
        try {
            MySQLDataBase mySQL = new MySQLDataBase(config);
            SQLSchema dataBase = mySQL.getSchema();

            accountTable = new AccountTable(dataBase.getTable("AccountTresor"));
            snipeFeedTable = new SnipeFeedTable(dataBase.getTable("SnipeFeed"));
            auctionsTable = new AuctionsTable(dataBase.getTable("Auctions"));
            bidTable = new BidTable(dataBase.getTable("AuctionsBid"));
            specialTable = new SpecialTable(dataBase.getTable("AuctionSpecial"));
            roleTable = new RoleTable(dataBase.getTable("Roles"));
            userTable = new UserTable(dataBase.getTable("User"));
            utilTable = new UtilTable(dataBase.getNoNameTable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
