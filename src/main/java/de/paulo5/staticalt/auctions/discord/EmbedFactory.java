package de.paulo5.staticalt.auctions.discord;

import de.paulo5.staticalt.auctions.MySQL.Tables.*;
import de.paulo5.staticalt.auctions.discord.manager.PingRoleManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EmbedFactory {

    static public final Color PURPLE = new Color(121, 5, 245);
    static private final Locale locale = Locale.US;

    public static MessageEmbed generateBidEmbed(BidTable.Bid bid) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("New bid placed!");
        embed.addField("Bidder:", "<@" + bid.getBidderDiscordId() + ">", true);
        embed.addField("Offer:", DecimalFormat.getCurrencyInstance(locale).format(bid.getPrice()), true);
        embed.setColor(PURPLE);
        return embed.build();
    }

    public static Message generateAuctionMessage(Guild guild, AuctionsTable.Auction auction, boolean withPing) {
        AccountTable.Account account = auction.getAccountData();

        MessageBuilder headerMessage = new MessageBuilder();
        headerMessage.allowMentions(Message.MentionType.ROLE);

        List<Role> added = new ArrayList<>();
        List<SpecialTable.Special> specials = auction.getSpecials();
        String specialText = "";
        if (!specials.isEmpty()) {
            specialText = "```\rSpecial\r";
            for (SpecialTable.Special special : specials) {
                specialText += "   ⇢ " + special.getText() + "\r";
                if (withPing) {
                    Role ping = PingRoleManager.getPingRoleFromSpecial(special);
                    if (ping != null && !added.contains(ping)) {
                        headerMessage.append(ping);
                        added.add(ping);
                    }
                }
            }
            specialText += "```\r";
        }

        if (withPing) {
            Role role = guild.getRoleById(auction.getRole());
            headerMessage.append(role);
        }

        EmbedBuilder embed = new EmbedBuilder();
        if (auction.isClosed()) {
            embed.setTitle("Auction for " + account.getName() + "! CLOSED", "https://de.namemc.com/search?q=" + account.getName());
            embed.setDescription(specialText + "This Auction was Closed.");
            if (auction.getBuyer() != 0) {
                embed.addField("Buyer:", "<@" + auction.getBuyerDiscordId() + ">", true);
                embed.addField("Price:", DecimalFormat.getCurrencyInstance(locale).format(auction.getBuyPrice()), true);
            }
            embed.setColor(Color.RED);
        } else {
            BidTable.Bid highestBid = Tables.bidTable.getHighestBidOfAuction(auction.getId());

            embed.setTitle("Auction for " + account.getName() + "!", "https://de.namemc.com/search?q=" + account.getName());
            embed.setDescription(specialText + "How do I bid?\rType `/bid <offer>` in this Channel.\rClick the Reaction :bell: to Register for Auction Update Ping's");
            embed.addField("Buy-It-Now:", DecimalFormat.getCurrencyInstance(locale).format(auction.getBin()), true);
            embed.addField("Minimum Offer:", DecimalFormat.getCurrencyInstance(locale).format(auction.getMin()), true);
            embed.addField("Current Offer:", highestBid != null ? DecimalFormat.getCurrencyInstance(locale).format(highestBid.getPrice()) + " <@" + highestBid.getBidderDiscordId() + ">" : "None", true);
            embed.setColor(PURPLE);
        }
        embed.setThumbnail("https://crafatar.com/renders/head/" + account.getUUID().toString() + "?overlay");
        //embed.setImage("https://crafatar.com/renders/body/"+uuid.toString()+"?overlay");
        headerMessage.setEmbeds(embed.build());

        return headerMessage.build();
    }

    public static Message generateOverbidMessage(Guild guild, AuctionsTable.Auction auction, BidTable.Bid bid) {
        AccountTable.Account account = auction.getAccountData();
        BidTable.Bid highestBid = Tables.bidTable.getHighestBidOfAuction(auction.getId());

        MessageBuilder message = new MessageBuilder();
        message.setActionRows(ActionRow.of(Button.link("https://discord.com/channels/" + guild.getIdLong() + "/" + auction.getChannel(), "Show Auction")));
        EmbedBuilder header = new EmbedBuilder();
        header.setTitle("Auction for " + account.getName() + "!", "https://de.namemc.com/search?q=" + account.getName());
        header.setDescription("Your bid was over bidden by <@" + bid.getBidderDiscordId() + ">");
        header.addField("Buy-It-Now:", DecimalFormat.getCurrencyInstance(locale).format(auction.getBin()), true);
        header.addField("Current Offer:", highestBid != null ? DecimalFormat.getCurrencyInstance(locale).format(highestBid.getPrice()) + " <@" + bid.getBidderDiscordId() + ">" : "None", true);
        header.setColor(PURPLE);
        header.setThumbnail("https://crafatar.com/renders/head/" + account.getUUID().toString() + "?overlay");
        message.setEmbeds(header.build());

        return message.build();
    }

    public static MessageEmbed generateMinBinEmbed(AuctionsTable.Auction auction) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Auction Update!");
        embed.addField("New BIN:", DecimalFormat.getCurrencyInstance(locale).format(auction.getBin()), true);
        embed.addField("New Min:", DecimalFormat.getCurrencyInstance(locale).format(auction.getMin()), true);
        embed.setColor(PURPLE);
        return embed.build();
    }

    public static MessageEmbed generatePingEmbed(Guild guild) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Want to be pinged when we add new special accounts?");
        embed.setDescription(
                "" +
                        PingRoleManager.EMOTE_OG.getAsMention() + " | OG\n" +
                        PingRoleManager.EMOTE_SEMI_OG.getAsMention() + " | Semi-OG\n" +
                        PingRoleManager.EMOTE_COOL.getAsMention() + " | Cool Name\n" +
                        PingRoleManager.EMOTE_CAPE.getAsMention() + " | Cape Account\n" +
                        PingRoleManager.EMOTE_GERMAN.getAsMention() + " | German Name\n" +
                        PingRoleManager.EMOTE_3CHAR.getAsMention() + " | 3 Letter / 3 Char\n" +
                        "\n" +
                        PingRoleManager.EMOTE_SNIPE.getAsMention() + " | New Snipe\n" +
                        PingRoleManager.EMOTE_GIVEAWAY.getAsMention() + " | Giveaway\n" +
                        PingRoleManager.EMOTE_OPINION_POLL.getAsMention() + " | Opinion Poll\n" +
                        "\n" +
                        PingRoleManager.EMOTE_ALL.getAsMention() + " | All Pings"
        );
        embed.setColor(PURPLE);
        return embed.build();
    }

    public static MessageEmbed generatePaymentEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("StaticAuction Paymentinformations");
        embed.setDescription(
                "\n" +
                        "PayPal-Email: payconalts@protonmail.com\n" +
                        "\n" +
                        "**Cryptoaddys:**\n" +
                        "\n" +
                        "Bitcoin: bc1qshf0cma7n8u8vlw3rjza3ggc25m984ncqdj6pk\n" +
                        "Ethereum: 0xd9bCf7Ec64762f726E5e370399f5ab9B6813BE1C\n" +
                        "Litecoin: LfzyY5qjFsSDMeNGsoAStf81k5V1vgwaSf\n" +
                        "Dogecoin: DPSMWyZ7SaWDP7c4BpNz78VJkVfnnZTNKo\n" +
                        "\n" +
                        "**Giftcards / Vouchers**\n" +
                        "\n" +
                        "If you want to pay with a German Amazon or PaySafe card, you have to send us the code in the ticket here."
        );
        embed.setColor(PURPLE);
        return embed.build();
    }

    public static MessageEmbed generatePaymentMessageEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("StaticAuction Payment-Methods");
        embed.setDescription(
                "" +
                        "**PayPal**\n" +
                        "\n" +
                        "We accept PayPal with Friend & Family  from you.\n" +
                        "\n" +
                        "**Cryptocurrency**\n" +
                        "\n" +
                        "We accept this 4 Cryptocurrencies: Bitcoin / Ethereum / Litecoin / Dogecoin\n" +
                        "\n" +
                        "**Giftcards / Vouchers**\n" +
                        "\n" +
                        "We accept also GERMAN Paysafecards and GERMAN Amazoncards as payment."
        );
        embed.setColor(PURPLE);
        return embed.build();
    }

    public static Message generateSpecialEmbed(Guild guild, AuctionsTable.Auction auction, List<SpecialTable.Special> specials) {
        MessageBuilder message = new MessageBuilder();

        Role role = guild.getRoleById(auction.getRole());
        message.append(role);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Special Update");

        embed.setDescription(
                "Added Specials:" +
                        "```properties\n" +
                        String.join("\n", specials.stream().map(SpecialTable.Special::getText).collect(Collectors.toList())) +
                        "```"
        );
        embed.setColor(PURPLE);
        message.setEmbeds(embed.build());
        return message.build();
    }
}
