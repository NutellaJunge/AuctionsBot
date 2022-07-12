package de.paulo5.staticalt.auctions.api;

import com.sun.net.httpserver.HttpServer;
import de.paulo5.staticalt.auctions.AuctionsBot;
import de.paulo5.staticalt.auctions.Config;
import de.paulo5.staticalt.auctions.MySQL.Tables.AuctionsTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.SnipeFeedTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.SpecialTable;
import de.paulo5.staticalt.auctions.MySQL.Tables.Tables;
import de.paulo5.staticalt.auctions.discord.manager.AuctionManager;
import de.paulo5.staticalt.auctions.discord.manager.SnipeFeedManager;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class AuctionsAPI {

    public AuctionsAPI() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8005), 0);
        server.createContext("/", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String text = "Hello from Auctions API";
                exchange.sendResponseHeaders(200, text.length());
                exchange.getResponseBody().write(text.getBytes());
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });
        server.createContext("/newsnipe", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                int status = 400;
                try {
                    Config data = new Config(exchange.getRequestBody());
                    Map<String, ?> arguments = Map.of("log", Integer.class, "searches", Integer.class);
                    if (data.testSchema(arguments)) {
                        Guild guild = AuctionsBot.jda.getGuildById(AuctionsBot.config.get("guild", String.class));
                        SnipeFeedTable.SnipeFeed snipe = null;

                        long time = System.currentTimeMillis();
                        if (data.contains("time")) {
                            time = data.get("time", Long.class);
                        }

                        try {
                            snipe = new SnipeFeedTable.SnipeFeed(data.get("log", Integer.class), data.get("searches", Integer.class), time);
                            SnipeFeedManager.newSnipe(guild, snipe);
                            Tables.snipeFeedTable.createSnipeFeed(snipe);

                            exchange.sendResponseHeaders(200, 0);
                            exchange.close();
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            status = 500;

                            if (snipe != null && guild != null) {
                                SnipeFeedManager.purgeSnipe(guild, snipe);
                            }
                            throw e;
                        }
                    } else {
                        throw new Exception("Not All Arguments are fulfilled\r" + arguments);
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(status, e.toString().length());
                    exchange.getResponseBody().write(e.toString().getBytes());
                    exchange.close();
                    return;
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });
        server.createContext("/newauction", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                int status = 400;
                try {
                    Config data = new Config(exchange.getRequestBody());
                    Map<String, ?> arguments = Map.of("account", Integer.class, "bin", Number.class, "min", Number.class);
                    if (data.testSchema(arguments)) {
                        Guild guild = AuctionsBot.jda.getGuildById(AuctionsBot.config.get("guild", String.class));
                        AuctionsTable.Auction auction = AuctionManager.createNewAuction(guild, data.get("account", Integer.class), data.get("bin", Double.class), data.get("min", Double.class));
                        if (data.contains("specials")) {
                            for (String specialText : data.getArray("specials", String.class)) {
                                SpecialTable.Special special = new SpecialTable.Special(auction.getId(), specialText);
                                Tables.specialTable.createSpecial(special);
                            }
                            AuctionManager.updateAuctionHeader(guild, auction);
                        }
                        exchange.sendResponseHeaders(200, 0);
                        exchange.close();
                        return;
                    } else {
                        throw new Exception("Not All Arguments are fulfilled\r" + arguments);
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(status, e.toString().length());
                    exchange.getResponseBody().write(e.toString().getBytes());
                    exchange.close();
                    return;
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        });
        server.setExecutor(null);
        server.start();
    }
}
