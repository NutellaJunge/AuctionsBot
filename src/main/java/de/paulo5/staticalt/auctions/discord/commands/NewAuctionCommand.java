package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.MySQL.Tables.AccountTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.SpecialTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

public class NewAuctionCommand extends ListenerAdapter {

    private final String id;

    public NewAuctionCommand(JDA jda) {
        id = jda.upsertCommand("newauction", "Create new Auction")
                .addOption(OptionType.INTEGER, "account_id", "Account ID in Tresor", true)
                .addOption(OptionType.NUMBER, "bin", "BIN value", true)
                .addOption(OptionType.NUMBER, "min", "Min value", true)
                .addOption(OptionType.STRING, "specials", "Specials seperated by ,", false)
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        InteractionHook reply = null;
        try {
            AccountTable.Account account = Tables.accountTable.getAccount(event.getOption("account_id").getAsInt());
            reply = event.deferReply().setEphemeral(true).complete();

            Thread.sleep(3000);
            AuctionsTable.Auction auction = AuctionManager.createNewAuction(event.getGuild(), account.getId(), event.getOption("bin").getAsDouble(), event.getOption("min").getAsDouble());

            if (event.getOption("specials") != null) {
                String specials = event.getOption("special").getAsString();
                for (String specialText : specials.split(" *, *")) {
                    SpecialTable.Special special = new SpecialTable.Special(auction.getId(), specialText);
                    Tables.specialTable.createSpecial(special);
                }
                AuctionManager.updateAuctionHeader(event.getGuild(), auction);
            }

            reply.sendMessage("Auction Created").setEphemeral(true).queue();
        } catch (IndexOutOfBoundsException e) {
            event.reply("Cant find account in tresor").setEphemeral(true).queue();
        } catch (Exception e) {
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
