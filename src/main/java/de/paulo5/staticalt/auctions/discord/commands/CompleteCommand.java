package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import de.paulo5.staticalt.auctions.discord.manager.DiscordManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import javax.management.AttributeNotFoundException;
import java.awt.*;
import java.sql.SQLException;

public class CompleteCommand extends ListenerAdapter {

    private final int CUSTOMER_ROLE;
    private final String id;

    public CompleteCommand(JDA jda) throws AttributeNotFoundException {
        id = jda.upsertCommand("complete", "Complete the Auction")
                .addOption(OptionType.NUMBER, "price", "Buy price.", true)
                .addOption(OptionType.USER, "buyer", "The buyer of the BIN offer.", true)
                .complete().getId();
        jda.addEventListener(this);

        CUSTOMER_ROLE = Tables.roleTable.getRoleIdByName("customer");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        try {
            int user = 0;
            double price = 0;
            if (!event.getOption("buyer").getAsUser().isBot()) {
                user = Tables.userTable.getUser(event.getOption("buyer").getAsMember());
                price = event.getOption("price").getAsDouble();
            }

            try {
                AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel(event.getChannel());
                if (!auction.isClosed()) {
                    auction.setBuyer(user);
                    auction.setBuyPrice(price);
                    AuctionManager.closeAuction(event.getGuild(), auction);

                    Tables.userTable.addRole(user, CUSTOMER_ROLE);
                    event.getGuild().addRoleToMember(event.getOption("buyer").getAsMember(), DiscordManager.createOrGetRole(event.getGuild(), "Customer", createdRole -> createdRole.setColor(Color.ORANGE))).queue();

                    event.reply("Auction was closed.").setEphemeral(true).queue();
                } else {
                    event.reply("Auction is already closed.").setEphemeral(true).queue();
                }
            } catch (IndexOutOfBoundsException e) {
                event.reply("You are not in a auctions channel.").setEphemeral(true).queue();
            }
        } catch (IndexOutOfBoundsException e) {
            event.reply("The selected buyer is not in the tresor.").setEphemeral(true).queue();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                event.reply(e.getMessage()).setEphemeral(true).queue();
            }
        }
    }
}
