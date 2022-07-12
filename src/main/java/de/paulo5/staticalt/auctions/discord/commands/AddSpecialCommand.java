package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.SpecialTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.EmbedFactory;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddSpecialCommand extends ListenerAdapter {

    private final String id;

    public AddSpecialCommand(JDA jda) {
        id = jda.upsertCommand("addspecial", "Add new Special")
                .addOption(OptionType.STRING, "special", "New Special(s) seperated by ,", true)
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
                String specials = event.getOption("special").getAsString();
                reply = event.deferReply().setEphemeral(true).complete();
                int i = 0;
                List<SpecialTable.Special> specialList = new ArrayList<>();
                for (String specialText : specials.split(" *, *")) {
                    SpecialTable.Special special = new SpecialTable.Special(auction.getId(), specialText);
                    Tables.specialTable.createSpecial(special);
                    specialList.add(special);
                    i++;
                }
                AuctionManager.updateAuctionHeader(event.getGuild(), auction);
                event.getChannel().sendMessage(EmbedFactory.generateSpecialEmbed(event.getGuild(), auction, specialList)).queue();
                reply.sendMessage("Added " + (i > 1 ? i + " Specials" : "Special")).setEphemeral(true).queue();
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
