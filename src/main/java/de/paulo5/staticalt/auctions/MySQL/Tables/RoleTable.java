package de.paulo5.staticalt.auctions.MySQL.Tables;

import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.SQLTable;

import javax.management.AttributeNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class RoleTable extends SQLTable {

    private final HashMap<String, Integer> roles = new HashMap<>();

    public RoleTable(SQLTable table) {
        super(table.query, table.name);

        try {
            List<Config> result = execute("SELECT * FROM %name%");
            for (Config row : result) {
                String name = row.get("name", String.class);
                int id = row.get("id", Integer.class);
                roles.put(name, id);
            }
            System.out.println("Loaded Roles: " + roles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRoleIdByName(String name) throws AttributeNotFoundException {
        for (Entry<String, Integer> role : roles.entrySet()) {
            if (role.getKey().equalsIgnoreCase(name.replace(' ', '_'))) {
                return role.getValue();
            }
        }
        throw new AttributeNotFoundException("There is no Role with the Name: " + name);
    }

    public String getRoleNameById(int id) throws AttributeNotFoundException {
        for (Entry<String, Integer> role : roles.entrySet()) {
            if (role.getValue() == id) {
                return role.getKey().replace('_', ' ');
            }
        }
        throw new AttributeNotFoundException("There is no Role with the Id: " + id);
    }
}
