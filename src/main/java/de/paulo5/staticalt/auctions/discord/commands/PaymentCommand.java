package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.discord.EmbedFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PaymentCommand extends ListenerAdapter {

    private final String id;

    public PaymentCommand(JDA jda) {
        id = jda.upsertCommand("payment", "Show Payment Infos")
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        try {
            event.getChannel().sendMessageEmbeds(EmbedFactory.generatePaymentEmbed()).queue();
            event.reply("Send").setEphemeral(true).queue();
        } catch (IndexOutOfBoundsException e) {
            event.reply("You are not in a Auctions Channel.").setEphemeral(true).queue();
        }
    }
}
