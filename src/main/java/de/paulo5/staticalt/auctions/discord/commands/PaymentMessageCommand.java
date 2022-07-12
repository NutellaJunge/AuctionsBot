package de.paulo5.staticalt.auctions.discord.commands;

import de.paulo5.staticalt.auctions.discord.EmbedFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PaymentMessageCommand extends ListenerAdapter {

    private final String id;

    public PaymentMessageCommand(JDA jda) {
        id = jda.upsertCommand("paymentmessage", "Show Payment Infos")
                .complete().getId();
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getCommandId().equals(id)) return;
        try {
            event.getChannel().sendMessageEmbeds(EmbedFactory.generatePaymentMessageEmbed()).queue();
            event.reply("Send").setEphemeral(true).queue();
        } catch (IndexOutOfBoundsException e) {
            event.reply("You are not in a Auctions Channel.").setEphemeral(true).queue();
        }
    }
}
