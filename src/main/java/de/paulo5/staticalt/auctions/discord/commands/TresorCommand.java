package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class TresorCommand extends ListenerAdapter {

    private final String id;

    public TresorCommand(JDA jda) {
        id = jda.upsertCommand("tresor", "Show Tresor Infos")
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        try {
            AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel(event.getChannel());
            event.reply("https://tresor.staticalt.de/?t=0&e=" + auction.getAccount()).setEphemeral(true).queue();
        } catch (IndexOutOfBoundsException e) {
            event.reply("You are not in a Auctions Channel.").setEphemeral(true).queue();
        }
    }
}
