package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.SQLTable;

import java.util.UUID;

public class AccountTable extends SQLTable {

    public AccountTable(SQLTable table) {
        super(table.query, table.name);
    }

    public Account getAccount(int id) {
        Config result = execute("SELECT name, uuid FROM %name% WHERE id=" + id, 1).get(0);
        String name = result.get("name", String.class);
        UUID uuid = UUID.fromString(result.get("uuid", String.class));
        return new Account(id, uuid, name);
    }

    public static class Account {

        private final int id;
        private final UUID uuid;
        private final String name;

        public Account(int id, UUID uuid, String name) {
            this.id = id;
            this.uuid = uuid;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public UUID getUUID() {
            return uuid;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "id=" + id +
                    ", uuid=" + uuid +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
