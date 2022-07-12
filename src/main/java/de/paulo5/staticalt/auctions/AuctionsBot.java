package de.paulo5.staticalt.auctions;

import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.api.AuctionsAPI;
import de.paulo5.staticalt.auctions.discord.AuctionEventListener;
import de.paulo5.staticalt.auctions.discord.commands.*;
import de.paulo5.staticalt.auctions.discord.manager.PingRoleManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.IOException;

public class AuctionsBot {

    public static JDA jda;
    public static Config config;
    public static AuctionsAPI api;

    public static void main(String[] args) {
        try {
            config = new Config(new File("config.json"));

            System.out.println("Starting API");
            try {
                api = new AuctionsAPI();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Tables.registerDataBase(config.get("sql", Config.class));

            jda = JDABuilder.createDefault(config.get("token", String.class))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.watching("at the running Bid's"))
                    .addEventListeners(new AuctionEventListener(), new PingRoleManager())
                    .build();

            jda.setAutoReconnect(true);
            jda.awaitReady();

            PingRoleManager.registerRoles();

            new BidCommand(jda);
            new TresorCommand(jda);
            new CompleteCommand(jda);
            new BinMinCommand(jda);
            new AddSpecialCommand(jda);
            new NewAuctionCommand(jda);
            new NewSnipeCommand(jda);
            new RemoveSpecialCommand(jda);
            new CreatePingMessageCommand(jda);
            new PaymentCommand(jda);
            new PaymentMessageCommand(jda);
            new AddBidCommand(jda);

            //Timer updater = new Timer();
            //updater.scheduleAtFixedRate(new IntervalUpdater(), 0, 1000 * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
