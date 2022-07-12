package de.paulo5.staticalt.auctions.discord.manager;

import de.paulo5.staticalt.auctions.MySQL.Tables.*;
import de.paulo5.staticalt.auctions.discord.EmbedFactory;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.sql.SQLException;

public class AuctionManager {
    public static void purgeAuction(Guild guild, AuctionsTable.Auction auction) {
        if (auction.getChannel() > 0) {
            guild.getGuildChannelById(auction.getChannel()).delete().queue();
        }
        if (auction.getRole() > 0) {
            guild.getRoleById(auction.getRole()).delete().queue();
        }
    }

    public static AuctionsTable.Auction createNewAuction(Guild guild, int account, double bin, double min) throws Exception {
        AuctionsTable.Auction auction = null;
        try {
            auction = new AuctionsTable.Auction(account, bin, min);
            AuctionManager.sendNewAuction(guild, auction);
            Tables.auctionsTable.createAuction(auction);

            SnipeFeedTable.SnipeFeed snipe = Tables.snipeFeedTable.getSnipeFeedByAccount(auction.getAccount());
            if (snipe != null) {
                SnipeFeedManager.addAuctionButton(guild, snipe, auction);
            }
            return auction;
        } catch (Exception e) {
            e.printStackTrace();

            if (auction != null && guild != null) {
                AuctionManager.purgeAuction(guild, auction);
            }
            throw e;
        }
    }

    private static void sendNewAuction(Guild guild, AuctionsTable.Auction auction) {
        AccountTable.Account account = auction.getAccountData();

        Category category = DiscordManager.createOrGetCategory(guild, "Auctions");
        TextChannel channel = guild.createTextChannel(account.getName(), category).complete();
        auction.setChannel(channel.getIdLong());

        Role role = DiscordManager.createOrGetRole(guild, "ping-" + account.getName(), createdRole -> createdRole.setColor(Color.CYAN));
        auction.setRole(role.getIdLong());

        Message header = channel.sendMessage(EmbedFactory.generateAuctionMessage(guild, auction, true)).complete();
        auction.setHeader(header.getIdLong());

        header.addReaction("U+1f514").queue();
    }

    public static void updateAuctionHeader(Guild guild, AuctionsTable.Auction auction) {
        TextChannel auctionChannel = guild.getTextChannelById(auction.getChannel());
        auctionChannel.editMessageById(auction.getHeader(), EmbedFactory.generateAuctionMessage(guild, auction, true)).queue();
    }

    public static AuctionsTable.Auction getAuctionByChannel(MessageChannel channel) {
        return Tables.auctionsTable.getAuctionByChannel(channel.getIdLong());
    }

    public static Message createBid(Guild guild, AuctionsTable.Auction auction, BidTable.Bid bid) {
        Role role = guild.getRoleById(auction.getRole());

        MessageBuilder message = new MessageBuilder();
        message.allowMentions(Message.MentionType.ROLE);
        message.append(role);
        message.setEmbeds(EmbedFactory.generateBidEmbed(bid));
        return message.build();
    }


    public static void sendOverBidMessage(Guild guild, PrivateChannel channel, AuctionsTable.Auction auction, BidTable.Bid bid) {
        channel.sendMessage(EmbedFactory.generateOverbidMessage(guild, auction, bid)).complete();
    }

    public static void closeAuction(Guild guild, AuctionsTable.Auction auction) throws SQLException {
        AccountTable.Account account = auction.getAccountData();

        auction.setClosed(true);

        guild.getRoleById(auction.getRole()).delete().queue();
        TextChannel channel = guild.getTextChannelById(auction.getChannel());
        Category category = DiscordManager.createOrGetCategory(guild, "Auctions-Closed");
        channel.getManager().setParent(category).setName(account.getName() + "-Closed").queue();

        channel.clearReactionsById(auction.getHeader()).queue();

        channel.sendMessage(EmbedFactory.generateAuctionMessage(guild, auction, false)).complete();
        updateAuctionHeader(guild, auction);
        Tables.auctionsTable.closeAuction(auction);
    }

    public static void sendMinBinChange(Guild guild, AuctionsTable.Auction auction) {
        TextChannel auctionChannel = guild.getTextChannelById(auction.getChannel());
        Role role = guild.getRoleById(auction.getRole());

        MessageBuilder message = new MessageBuilder();
        message.allowMentions(Message.MentionType.ROLE);
        message.append(role);
        message.setEmbeds(EmbedFactory.generateMinBinEmbed(auction));

        auctionChannel.sendMessage(message.build()).queue();

        updateAuctionHeader(guild, auction);
    }

    public static void removeBid(Guild guild, AuctionsTable.Auction auction, BidTable.Bid bid) throws SQLException {
        TextChannel auctionChannel = guild.getTextChannelById(auction.getChannel());
        try {
            auctionChannel.deleteMessageById(bid.getMessage()).complete();
        } catch (Exception ignore) {
        }

        Tables.bidTable.removeBid(bid);

        updateAuctionHeader(guild, auction);
    }
}
