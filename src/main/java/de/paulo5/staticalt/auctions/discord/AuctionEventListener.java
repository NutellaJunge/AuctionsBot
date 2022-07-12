package de.paulo5.staticalt.auctions.discord;

import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.BidTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class AuctionEventListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        try {
            if (event.getUser() != null) {
                if (!event.getUser().isBot()) {
                    AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel(event.getChannel());
                    if (!auction.isClosed() && event.getReactionEmote().isEmoji() && event.getReactionEmote().getEmoji().equals("\uD83D\uDD14") && auction.getHeader() == event.getMessageIdLong()) {
                        Role role = event.getGuild().getRoleById(auction.getRole());
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                    } else {
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        try {
            if (event.getUser() != null) {
                if (!event.getUser().isBot()) {
                    AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel(event.getChannel());
                    if (!auction.isClosed() && event.getReactionEmote().isEmoji() && event.getReactionEmote().getEmoji().equals("\uD83D\uDD14") && auction.getHeader() == event.getMessageIdLong()) {
                        Role role = event.getGuild().getRoleById(auction.getRole());
                        event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                    }
                }
            }
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        try {
            AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel(event.getChannel());
            if (!auction.isClosed()) {
                BidTable.Bid bid = Tables.bidTable.getBidByMessageId(event.getMessageIdLong());
                AuctionManager.removeBid(event.getGuild(), auction, bid);
            }
        } catch (IndexOutOfBoundsException ignore) {
        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            AuctionManager.getAuctionByChannel(event.getChannel());
            if (!event.getAuthor().isBot()) {
                event.getMessage().delete().queue();
            }
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        try {
            if (event.getChannel().getType().isMessage()) {
                AuctionsTable.Auction auction = AuctionManager.getAuctionByChannel((MessageChannel) event.getChannel());
                Tables.auctionsTable.deleteAuction(auction);
            }
        } catch (IndexOutOfBoundsException ignore) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
