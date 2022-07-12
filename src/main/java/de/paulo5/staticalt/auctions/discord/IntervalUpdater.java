package de.paulo5.staticalt.auctions.discord;

import de.paulo5.staticalt.auctions.AuctionsBot;
import de.paulo5.staticalt.auctions.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.TimerTask;

public class IntervalUpdater extends TimerTask {

    private final Guild guild;
    private final TextChannel snipeFeedChannel;

    public IntervalUpdater() {
        guild = AuctionsBot.jda.getGuildById(AuctionsBot.config.get("guild", String.class));
        snipeFeedChannel = guild.getTextChannelById(AuctionsBot.config.get("snipeFeed", Config.class).get(guild.getId() + "", Long.class));
    }

    @Override
    public void run() {

    }
}
