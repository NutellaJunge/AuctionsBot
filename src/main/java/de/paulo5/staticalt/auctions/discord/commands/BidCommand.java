package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.AuctionsBot;
import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.BidTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class BidCommand extends ListenerAdapter {

    private final String id;

    public BidCommand(JDA jda) {
        id = jda.upsertCommand("bid", "Bid on a Auction")
                .addOption(OptionType.NUMBER, "offer", "Your pay Offer", true)
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
                int user = Tables.userTable.getUser(event.getMember());
                double price = event.getOption("offer").getAsDouble();
                if (price >= auction.getMin()) {
                    BidTable.Bid highestBid = Tables.bidTable.getHighestBidOfAuction(auction.getId());
                    if (highestBid == null || price > highestBid.getPrice()) {
                        if (highestBid == null || price - highestBid.getPrice() >= 3) {
                            reply = event.deferReply().complete();
                            BidTable.Bid bid = new BidTable.Bid(auction.getId(), user, price);
                            Message message = AuctionManager.createBid(event.getGuild(), auction, bid);
                            message = reply.sendMessage(message).complete();
                            bid.setMessage(message.getIdLong());
                            Tables.bidTable.createBid(bid);

                            AuctionManager.updateAuctionHeader(event.getGuild(), auction);

                            if (highestBid != null && highestBid.getBidder() != bid.getBidder()) {
                                PrivateChannel privateChannel = AuctionsBot.jda.openPrivateChannelById(highestBid.getBidderDiscordId()).complete();
                                AuctionManager.sendOverBidMessage(event.getGuild(), privateChannel, auction, bid);
                            }
                        } else {
                            event.reply("You must bid minimal $3 more than the bid before.").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("You must bid more than the bid before.").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("You must bid more than the minimum of the auction.").setEphemeral(true).queue();
                }
            } else {
                event.reply("Auction is closed").queue();
            }
        } catch (IndexOutOfBoundsException e) {
            event.reply("You are not in a Auctions Channel.").setEphemeral(true).queue();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                try {
                    reply.sendMessage(e.getMessage()).setEphemeral(true).queue();
                } catch (IllegalStateException e1) {
                    event.reply(e.getMessage()).setEphemeral(true).queue();
                }
            }
        }
    }
}
