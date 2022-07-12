package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.MySQL.Tables.SnipeFeedTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.SnipeFeedManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

public class NewSnipeCommand extends ListenerAdapter {

    private final String id;

    public NewSnipeCommand(JDA jda) {
        id = jda.upsertCommand("newsnipe", "Create new Auction")
                .addOption(OptionType.INTEGER, "log_id", "Log ID in Snirt", true)
                .addOption(OptionType.INTEGER, "searches", "BIN value", true)
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        InteractionHook reply = null;
        try {
            reply = event.deferReply().setEphemeral(true).complete();

            SnipeFeedTable.SnipeFeed snipe = new SnipeFeedTable.SnipeFeed(event.getOption("log_id").getAsInt(), event.getOption("searches").getAsInt(), System.currentTimeMillis());
            SnipeFeedManager.newSnipe(event.getGuild(), snipe);
            Tables.snipeFeedTable.createSnipeFeed(snipe);

            reply.sendMessage("Auction Created").setEphemeral(true).queue();
        } catch (IndexOutOfBoundsException e) {
            try {
                reply.sendMessage("Cant find account in tresor").setEphemeral(true).complete();
            } catch (Exception ignore) {
                event.reply("Cant find account in tresor").setEphemeral(true).queue();
            }
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
