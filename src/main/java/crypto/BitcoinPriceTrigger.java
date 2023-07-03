package main.java.crypto;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceTrigger {

    private final double targetPrice;
    private final int priceTrendDesired;  //1 for uptrend, 0 for downtrend
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Locale locale = Locale.US;

    public BitcoinPriceTrigger(Double price){
        this.targetPrice = price;
        double btcStartPrice = BitcoinGeneralPriceScheduler.getBtcPrice();
        if (targetPrice > btcStartPrice){
            priceTrendDesired = 1;
        } else {
            priceTrendDesired = 0;
        }
    }


    public void setPriceForNotification(TextChannel channel, String id) {
        long ALERT_INTERVAL = 600;
        executorService.scheduleAtFixedRate(() -> {
            System.out.println("start here");
            double priceNow = BitcoinGeneralPriceScheduler.getBtcPrice();
            if (targetPrice == priceNow) {
                String message = String.format("Bitcoin has reached $%,.2f, now at $%,.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
                executorService.shutdown();
            } else if (priceNow > targetPrice && priceTrendDesired == 1) {
                String message = String.format("Bitcoin has exceeded $%,.2f, now at $%,.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
                executorService.shutdown();
            } else if (priceNow < targetPrice && priceTrendDesired == 0) {
                String message = String.format("Bitcoin has gone below $%,.2f, now at $%,.2f <@%s>!", targetPrice, priceNow, id);
                channel.sendMessage(message).queue();
                System.out.println(message);
                executorService.shutdown();
            }
            
        },0, ALERT_INTERVAL, TimeUnit.SECONDS);


    }
}
