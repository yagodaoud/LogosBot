package main.java.crypto;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceTrigger {

    private double targetPrice;
    private final String btc = "BTC";
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long ALERT_INTERVAL= 60;


    public BitcoinPriceTrigger(Double price){
        this.targetPrice = price;
    }


    public void setPriceForNotification(TextChannel channel, String id) {
        executorService.scheduleAtFixedRate(() -> {
            double priceNow = ApiConnection.getPrice(btc);
            if (targetPrice == priceNow) {
                String message = String.format("Bitcoin has reached $%.2f <@%s>!", targetPrice, id);
                channel.sendMessage(message).queue();
                executorService.shutdown();
            }
        },0, ALERT_INTERVAL, TimeUnit.SECONDS);

    }
}
