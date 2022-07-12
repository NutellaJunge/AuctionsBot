package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.MySQL.SQLTable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import javax.management.AttributeNotFoundException;
import java.sql.SQLException;

public class UserTable extends SQLTable {

    public UserTable(SQLTable table) {
        super(table.query, table.name);
    }

    public boolean contains(Member member) {
        try {
            execute("SELECT * FROM DiscordAccounts WHERE id=" + member.getIdLong(), 1);
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public int getUser(Member member) {
        try {
            if (!contains(member)) {
                executeUpdate("INSERT INTO User VALUES (null)");
                int userId = execute("SELECT LAST_INSERT_ID() AS id;").get(0).get("id", Integer.class);
                executeUpdate("INSERT INTO DiscordAccounts (id, user) VALUES ('" + member.getIdLong() + "', " + userId + ")");

                try {
                    int roleId = Tables.roleTable.getRoleIdByName("user");
                    executeUpdate("INSERT INTO UserRoles (role, user) VALUES (" + roleId + ", " + userId + ")");
                } catch (AttributeNotFoundException e1) {
                }

                for (Role role : member.getRoles()) {
                    try {
                        int roleId = Tables.roleTable.getRoleIdByName(role.getName());
                        executeUpdate("INSERT INTO UserRoles (role, user) VALUES (" + roleId + ", " + userId + ")");
                    } catch (AttributeNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return userId;
            } else {
                return execute("SELECT * FROM DiscordAccounts WHERE id=" + member.getIdLong()).get(0).get("user", Integer.class);
            }
        } catch (IndexOutOfBoundsException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addRole(int user, int role) {
        try {
            executeUpdate("INSERT INTO UserRoles (role, user) VALUES (" + role + ", " + user + ")");
        } catch (SQLException ignore) {
        }
    }
}
