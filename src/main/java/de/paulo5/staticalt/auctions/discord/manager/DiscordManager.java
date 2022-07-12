package de.paulo5.staticalt.auctions.discord.manager;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.RoleManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DiscordManager {

    public static Category createOrGetCategory(Guild guild, String name) {
        List<Category> cats = guild.getCategoriesByName(name, true);
        if (!cats.isEmpty()) {
            Category category = cats.get(0);
            if (category.getChannels().size() != 50) {
                return category;
            } else {
                return category.createCopy().complete();
            }
        }
        return guild.createCategory(name).complete();
    }

    public static Role createOrGetRole(Guild guild, String name, RoleCreater onCreate) {
        List<Role> roles = guild.getRolesByName(name, true);
        if (!roles.isEmpty()) {
            return roles.get(0);
        }
        Role role = guild.createRole().complete();
        onCreate.run(role.getManager().setName(name)).complete();
        return role;
    }

    public static Emote createOrGetEmote(Guild guild, String name, File file) throws IOException {
        List<Emote> emotes = guild.getEmotesByName(name, true);
        if (!emotes.isEmpty()) {
            return emotes.get(0);
        }
        Icon icon = Icon.from(file);
        return guild.createEmote(name, icon).complete();
    }

    public interface RoleCreater {
        RoleManager run(RoleManager createdRole);
    }
}
