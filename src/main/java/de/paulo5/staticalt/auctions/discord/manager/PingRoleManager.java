package de.paulo5.staticalt.auctions.discord.manager;

import de.paulo5.staticalt.auctions.AuctionsBot;
import de.paulo5.staticalt.auctions.MySQL.Tables.SpecialTable;
import de.paulo5.staticalt.auctions.discord.EmbedFactory;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class PingRoleManager extends ListenerAdapter {

    public static Role PING_OG;
    public static Role PING_SEMI_OG;
    public static Role PING_COOL;
    public static Role PING_CAPE;
    public static Role PING_GERMAN;
    public static Role PING_3CHAR;
    public static Role NEW_SNIPE;
    public static Role GIVEAWAY;
    public static Role OPINION_POLL;
    public static Emote EMOTE_OG;
    public static Emote EMOTE_SEMI_OG;
    public static Emote EMOTE_COOL;
    public static Emote EMOTE_CAPE;
    public static Emote EMOTE_GERMAN;
    public static Emote EMOTE_SNIPE;
    public static Emote EMOTE_GIVEAWAY;
    public static Emote EMOTE_OPINION_POLL;
    public static Emote EMOTE_ALL;
    public static Emote EMOTE_3CHAR;

    public static void registerRoles() {
        Guild guild = AuctionsBot.jda.getGuildById(AuctionsBot.config.get("guild", String.class));
        PING_OG = DiscordManager.createOrGetRole(guild, "ping-og", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        PING_SEMI_OG = DiscordManager.createOrGetRole(guild, "ping-semi-og", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        PING_COOL = DiscordManager.createOrGetRole(guild, "ping-cool", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        PING_CAPE = DiscordManager.createOrGetRole(guild, "ping-cape", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        PING_GERMAN = DiscordManager.createOrGetRole(guild, "ping-german", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        PING_3CHAR = DiscordManager.createOrGetRole(guild, "ping-3char", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        NEW_SNIPE = DiscordManager.createOrGetRole(guild, "new-snipe", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        GIVEAWAY = DiscordManager.createOrGetRole(guild, "giveaway", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));
        OPINION_POLL = DiscordManager.createOrGetRole(guild, "opinion-poll", createdRole -> createdRole.setColor(EmbedFactory.PURPLE));

        try {
            EMOTE_OG = DiscordManager.createOrGetEmote(guild, "og", new File("emotes/og.png"));
            EMOTE_SEMI_OG = DiscordManager.createOrGetEmote(guild, "semi_og", new File("emotes/semi_og.png"));
            EMOTE_COOL = DiscordManager.createOrGetEmote(guild, "cool", new File("emotes/cool.png"));
            EMOTE_CAPE = DiscordManager.createOrGetEmote(guild, "cape", new File("emotes/cape.png"));
            EMOTE_GERMAN = DiscordManager.createOrGetEmote(guild, "german", new File("emotes/german.png"));
            EMOTE_3CHAR = DiscordManager.createOrGetEmote(guild, "3char", new File("emotes/3.png"));
            EMOTE_SNIPE = DiscordManager.createOrGetEmote(guild, "snipe", new File("emotes/snipe.png"));
            EMOTE_GIVEAWAY = DiscordManager.createOrGetEmote(guild, "giveaway", new File("emotes/giveaway.png"));
            EMOTE_OPINION_POLL = DiscordManager.createOrGetEmote(guild, "opinion_poll", new File("emotes/opinion_poll.png"));
            EMOTE_ALL = DiscordManager.createOrGetEmote(guild, "all", new File("emotes/all.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Role getPingRoleFromSpecial(SpecialTable.Special special) {
        if (Pattern.compile("(^| )semi[- ]?og([- ]|$)", Pattern.CASE_INSENSITIVE).matcher(special.getText()).find()) {
            return PING_SEMI_OG;
        }
        if (Pattern.compile("(^ *)og([- ]|$)", Pattern.CASE_INSENSITIVE).matcher(special.getText()).find()) {
            return PING_OG;
        }
        if (Pattern.compile("(^| )cool([- ]|$)", Pattern.CASE_INSENSITIVE).matcher(special.getText()).find()) {
            return PING_COOL;
        }
        if (Pattern.compile("(^| )minecon([- ]|$)", Pattern.CASE_INSENSITIVE).matcher(special.getText()).find()) {
            return PING_CAPE;
        }
        if (Pattern.compile("(^| )german([- ]|$)", Pattern.CASE_INSENSITIVE).matcher(special.getText()).find()) {
            return PING_GERMAN;
        }
        if (Pattern.compile("(^| )3[ -]?(chars?|letters?)([- ]|$)", Pattern.CASE_INSENSITIVE).matcher(special.getText()).find()) {
            return PING_3CHAR;
        }
        return null;
    }

    public static void sendAuctionRoleMessage(TextChannel channel) throws IOException {
        Guild guild = channel.getGuild();

        if (AuctionsBot.config.contains("reactionRoleMessage")) {
            try {
                channel.deleteMessageById(AuctionsBot.config.get("reactionRoleMessage", Long.class)).complete();
            } catch (ErrorResponseException ignored) {
            }
        }

        Message message = channel.sendMessageEmbeds(EmbedFactory.generatePingEmbed(guild)).complete();

        message.addReaction(EMOTE_OG).complete();
        message.addReaction(EMOTE_SEMI_OG).complete();
        message.addReaction(EMOTE_COOL).complete();
        message.addReaction(EMOTE_CAPE).complete();
        message.addReaction(EMOTE_GERMAN).complete();
        message.addReaction(EMOTE_3CHAR).complete();

        message.addReaction(EMOTE_SNIPE).complete();
        message.addReaction(EMOTE_GIVEAWAY).complete();
        message.addReaction(EMOTE_OPINION_POLL).complete();

        message.addReaction(EMOTE_ALL).complete();

        AuctionsBot.config.set("reactionRoleMessage", message.getIdLong());
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser() != null) {
            if (!event.getUser().isBot()) {
                if (AuctionsBot.config.contains("reactionRoleMessage")) {
                    if (event.getMessageIdLong() == AuctionsBot.config.get("reactionRoleMessage", Long.class)) {
                        if (event.getReactionEmote().isEmote()) {
                            Emote emote = event.getReactionEmote().getEmote();
                            if (EMOTE_OG.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), PING_OG).queue();
                                return;
                            } else if (EMOTE_SEMI_OG.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), PING_SEMI_OG).queue();
                                return;
                            } else if (EMOTE_COOL.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), PING_COOL).queue();
                                return;
                            } else if (EMOTE_CAPE.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), PING_CAPE).queue();
                                return;
                            } else if (EMOTE_GERMAN.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), PING_GERMAN).queue();
                                return;
                            } else if (EMOTE_3CHAR.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), PING_3CHAR).queue();
                                return;
                            } else if (EMOTE_SNIPE.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), NEW_SNIPE).queue();
                                return;
                            } else if (EMOTE_GIVEAWAY.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), GIVEAWAY).queue();
                                return;
                            } else if (EMOTE_OPINION_POLL.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), OPINION_POLL).queue();
                                return;
                            } else if (EMOTE_ALL.equals(emote)) {
                                event.getGuild().addRoleToMember(event.getMember(), PING_OG).queue();
                                event.getGuild().addRoleToMember(event.getMember(), PING_SEMI_OG).queue();
                                event.getGuild().addRoleToMember(event.getMember(), PING_COOL).queue();
                                event.getGuild().addRoleToMember(event.getMember(), PING_CAPE).queue();
                                event.getGuild().addRoleToMember(event.getMember(), PING_GERMAN).queue();
                                event.getGuild().addRoleToMember(event.getMember(), PING_3CHAR).queue();
                                event.getGuild().addRoleToMember(event.getMember(), NEW_SNIPE).queue();
                                event.getGuild().addRoleToMember(event.getMember(), GIVEAWAY).queue();
                                event.getGuild().addRoleToMember(event.getMember(), OPINION_POLL).queue();
                                return;
                            }
                        }
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.getUser() != null) {
            if (!event.getUser().isBot()) {
                if (AuctionsBot.config.contains("reactionRoleMessage")) {
                    if (event.getMessageIdLong() == AuctionsBot.config.get("reactionRoleMessage", Long.class)) {
                        if (event.getReactionEmote().isEmote()) {
                            Emote emote = event.getReactionEmote().getEmote();
                            if (EMOTE_OG.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_OG).queue();
                                return;
                            } else if (EMOTE_SEMI_OG.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_SEMI_OG).queue();
                                return;
                            } else if (EMOTE_COOL.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_COOL).queue();
                                return;
                            } else if (EMOTE_CAPE.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_CAPE).queue();
                                return;
                            } else if (EMOTE_GERMAN.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_GERMAN).queue();
                                return;
                            } else if (EMOTE_3CHAR.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_3CHAR).queue();
                                return;
                            } else if (EMOTE_SNIPE.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), NEW_SNIPE).queue();
                                return;
                            } else if (EMOTE_GIVEAWAY.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), GIVEAWAY).queue();
                                return;
                            } else if (EMOTE_OPINION_POLL.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), OPINION_POLL).queue();
                                return;
                            } else if (EMOTE_ALL.equals(emote)) {
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_OG).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_SEMI_OG).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_COOL).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_CAPE).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_GERMAN).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), PING_3CHAR).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), NEW_SNIPE).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), GIVEAWAY).queue();
                                event.getGuild().removeRoleFromMember(event.getMember(), OPINION_POLL).queue();
                                return;
                            }
                        }
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                }
            }
        }
    }
}
