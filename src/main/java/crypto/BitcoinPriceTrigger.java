package main.java.crypto;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceTrigger {

    private double targetPrice;
    private final String btc = "BTC";
    private int priceTrendDesired;  //1 for up, 0 for down
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long ALERT_INTERVAL= 300;


    public BitcoinPriceTrigger(Double price){
        this.targetPrice = price;
        if (targetPrice > ApiConnection.getPrice(btc)){
            priceTrendDesired = 1;
        } else {
            priceTrendDesired = 0;
        }
    }


    public void setPriceForNotification(TextChannel channel, String id) {
        executorService.scheduleAtFixedRate(() -> {
            double priceNow = ApiConnection.getPrice(btc);
            if (targetPrice == priceNow) {
                String message = String.format("Bitcoin has reached $%.2f, now at $%.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
            } else if (priceNow > targetPrice && priceTrendDesired == 1) {
                String message = String.format("Bitcoin has exceeded $%.2f, now at $%.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
            } else if (priceNow < targetPrice && priceTrendDesired == 0) {
                String message = String.format("Bitcoin has gone below $%.2f, now at $%.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
            }
            executorService.shutdown();
            
        },120, ALERT_INTERVAL, TimeUnit.SECONDS);

    }
}
