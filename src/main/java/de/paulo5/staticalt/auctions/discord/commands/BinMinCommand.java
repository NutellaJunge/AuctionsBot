package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class BinMinCommand extends ListenerAdapter {

    private final String id;

    public BinMinCommand(JDA jda) {
        id = jda.upsertCommand("setbinmin", "Set a new BIN or Min on a Auction")
                .addOption(OptionType.NUMBER, "bin", "The new BIN.")
                .addOption(OptionType.NUMBER, "min", "The new Min.")
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        try {
            AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel(event.getChannel());
            if (!auction.isClosed()) {
                int change = 0;
                if (event.getOption("min") != null) {
                    auction.setMin(event.getOption("min").getAsDouble());
                    change += 1;
                }
                if (event.getOption("bin") != null) {
                    auction.setBin(event.getOption("bin").getAsDouble());
                    change += 2;
                }

                if (change > 0) {
                    AuctionManager.sendMinBinChange(event.getGuild(), auction);
                    Tables.auctionsTable.changeBinMin(auction);
                }

                switch (change) {
                    case 0:
                        event.reply("Nothing changed").setEphemeral(true).queue();
                        break;
                    case 1:
                        event.reply("Min changed").setEphemeral(true).queue();
                        break;
                    case 2:
                        event.reply("Bin changed").setEphemeral(true).queue();
                        break;
                    case 3:
                        event.reply("Both changed").setEphemeral(true).queue();
                        break;
                }
            } else {
                event.reply("Auction is closed").queue();
            }
        } catch (IndexOutOfBoundsException e) {
            event.reply("You are not in a Auctions Channel.").setEphemeral(true).queue();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                event.reply(e.getMessage()).setEphemeral(true).queue();
            }
        }
    }
}
