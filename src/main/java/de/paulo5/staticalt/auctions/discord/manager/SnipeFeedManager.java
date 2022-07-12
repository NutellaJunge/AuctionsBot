package de.paulo5.staticalt.auctions.discord.manager;

import de.paulo5.staticalt.auctions.AuctionsBot;
import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.Tables.AccountTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.SnipeFeedTable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

public class SnipeFeedManager {

    static private final Color PURPLE = new Color(121, 5, 245);


    public static void newSnipe(Guild guild, SnipeFeedTable.SnipeFeed snipe) {
        AccountTable.Account account = snipe.getAccountData();

        boolean news = false;
        long id = AuctionsBot.config.get("snipeFeed", Config.class).get(guild.getId() + "", Long.class);
        MessageChannel snipeFeedChannel = guild.getTextChannelById(id);
        if (snipeFeedChannel == null) {
            snipeFeedChannel = guild.getNewsChannelById(id);
            if (snipeFeedChannel == null) {
                snipeFeedChannel = guild.createTextChannel("SnipeFeed").complete();
                AuctionsBot.config.get("snipeFeed", Config.class).set(guild.getId(), snipeFeedChannel.getIdLong());
            } else {
                news = true;
            }
        }
        EmbedBuilder banner = new EmbedBuilder();
        banner.setImage("https://staticalt.de/banner.gif?n=" + account.getName());
        banner.setColor(PURPLE);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail("https://crafatar.com/renders/head/" + account.getUUID().toString() + "?overlay");
        embed.setTitle("New Fresh Snipe", "https://de.namemc.com/profile/" + account.getName());
        embed.setDescription("We sniped a new name! Check it out! <t:" + (snipe.getTime() / 1000) + ":R>");
        embed.addField("Username", account.getName(), true);
        embed.addField("Searches", snipe.getSearches() + "", true);
        embed.addField("Accountprofile on NameMC", "[NameMC](https://de.namemc.com/search?q=" + account.getName() + ")", true);
        embed.setColor(PURPLE);

        MessageBuilder snipeMessage = new MessageBuilder();
        snipeMessage.allowMentions(Message.MentionType.ROLE);
        snipeMessage.append(PingRoleManager.NEW_SNIPE);
        snipeMessage.setEmbeds(banner.build(), embed.build());

        Message message = snipeFeedChannel.sendMessage(snipeMessage.build()).complete();
        snipe.setMessage(message.getIdLong());
        if (news) {
            message.crosspost().queue();
        }
    }

    public static void purgeSnipe(Guild guild, SnipeFeedTable.SnipeFeed snipe) {
        long id = AuctionsBot.config.get("snipeFeed", Config.class).get(guild.getId() + "", Long.class);
        MessageChannel snipeFeedChannel = guild.getTextChannelById(id);
        if (snipeFeedChannel == null) {
            snipeFeedChannel = guild.getNewsChannelById(id);
            if (snipeFeedChannel == null) {
                return;
            }
        }
        snipeFeedChannel.deleteMessageById(snipe.getMessage()).queue();
    }

    public static void addAuctionButton(Guild guild, SnipeFeedTable.SnipeFeed snipe, AuctionsTable.Auction auction) {
        long id = AuctionsBot.config.get("snipeFeed", Config.class).get(guild.getId() + "", Long.class);
        MessageChannel snipeFeedChannel = guild.getTextChannelById(id);
        if (snipeFeedChannel == null) {
            snipeFeedChannel = guild.getNewsChannelById(id);
            if (snipeFeedChannel == null) {
                return;
            }
        }
        Button linkButton = Button.link("https://discord.com/channels/" + guild.getIdLong() + "/" + auction.getChannel(), "Show Auction");
        snipeFeedChannel.editMessageComponentsById(snipe.getMessage(), ActionRow.of(linkButton)).complete();
    }
}
