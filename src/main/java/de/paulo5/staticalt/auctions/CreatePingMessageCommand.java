package de.paulo5.staticalt.auctions;

import de.paulo5.staticalt.auctions.discord.manager.PingRoleManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CreatePingMessageCommand extends ListenerAdapter {

    private final String id;

    public CreatePingMessageCommand(JDA jda) {
        id = jda.upsertCommand("reactionrolles", "Create Reactionrolle Message")
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        try {
            event.reply("test").complete().deleteOriginal().queue();
            PingRoleManager.sendAuctionRoleMessage(event.getTextChannel());
        } catch (IOException e) {
            e.printStackTrace();
            event.reply(e.getMessage()).queue();
        }
    }
}