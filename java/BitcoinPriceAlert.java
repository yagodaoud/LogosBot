package main.java;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceAlert { //Bitcoin price alert at a certain percentage on a certain timeframe


    private static final String BTC_SYMBOL = "BTC"; //Set the crypto symbol you want
    private static final String apiKey = "e77bacb5-8443-4bc7-8f5b-e0e26b497abd";
    private static final double VARIATION_THRESHOLD = 0.01; //Set the variation deserved

    private static final double THRESHOLD = 0.01; //Same as above
    private static final long ALERT_INTERVAL= 3600; //Set the time frame in seconds
    private boolean alerted;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private double lastPrice;

    public String getApiKey() {
        return apiKey.toString();
    } //Api key getter from .env (not working right now)

    public BitcoinPriceAlert() { //Build CryptoPrice to get the price of Bitcoin to compare further down
        CryptoPrice api = new CryptoPrice(apiKey);
        lastPrice = api.getPrice(BTC_SYMBOL);
        System.out.println("Last price:" + lastPrice); //The price variation will start at 0, because current price and last price will be the same (just for the start)
    }

    public void startAlerts(TextChannel channel) { //Call method from BotCommands
        System.out.println("started");
        executorService.scheduleAtFixedRate(() -> {
            CryptoPrice api = new CryptoPrice(apiKey); //Build the variable to be stored as currentPrice to compare with lastPrice
            double currentPrice = api.getPrice(BTC_SYMBOL);
            double variation = (currentPrice - lastPrice) / lastPrice; //Formula to get the variation e.g. The price was 100, now it's 120 -> (120 - 100) / 100 = 0.2 * 100 = 20%
            System.out.println("Current price is " + currentPrice);
            System.out.println("Variation is: " + String.format("%.2f%%", variation * 100));
            if (Math.abs(variation) >= VARIATION_THRESHOLD) { //Checks if it's an uptrend or downtrend
                String direction = variation > 0 ? "up" : "down";
                String emoji = variation > 0 ? "📈" : "📉";
                String priceString = String.format("$%.2f", currentPrice);
                String bitcoinAlert = "Bitcoin is " + direction + "! " + priceString + " (" + String.format("%.2f%%", variation * 100 + "in the last hour") + ") " + emoji;
                channel.sendMessage(bitcoinAlert).queue();
                System.out.println(bitcoinAlert);
                lastPrice = currentPrice;
            } else if (Math.abs(variation) < THRESHOLD && alerted) {
                alerted = false;
            }
        }, 0, ALERT_INTERVAL, TimeUnit.SECONDS);
    }

    public void stopAlerts(TextChannel channel) { //Stopping the alert
        executorService.shutdown();
        System.out.println("stopped");
    }
}

