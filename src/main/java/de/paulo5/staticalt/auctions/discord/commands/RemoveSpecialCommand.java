package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class RemoveSpecialCommand extends ListenerAdapter {

    private final String id;

    public RemoveSpecialCommand(JDA jda) {
        id = jda.upsertCommand("removespecial", "Remove Special")
                .addOption(OptionType.STRING, "index", "Special indexe(s) seperated by ,", true)
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        InteractionHook reply = null;
        try {
            AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel(event.getChannel());
            if (!auction.isClosed()) {
                reply = event.deferReply().setEphemeral(true).complete();
                int i = Tables.specialTable.removeSpecialAtIndex(auction, event.getOption("index").getAsString().split(" *, *"));
                AuctionManager.updateAuctionHeader(event.getGuild(), auction);
                reply.sendMessage("Removed " + (i > 1 ? i + " Specials" : "Special")).setEphemeral(true).queue();
            } else {
                event.reply("Auction is closed").queue();
            }
        } catch (IndexOutOfBoundsException e) {
            event.reply("You are not in a Auctions Channel.").setEphemeral(true).queue();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                try {
                    reply.sendMessage(e.getMessage()).setEphemeral(true).complete();
                } catch (Exception ignore) {
                    event.reply(e.getMessage()).setEphemeral(true).queue();
                }
            }
        }
    }
}
