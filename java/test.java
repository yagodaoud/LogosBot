package main.java;

import main.java.crypto.CryptoPrice;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class test {


    private static final String BTC_SYMBOL = "BTC";
    private static final String apiKey = "e77bacb5-8443-4bc7-8f5b-e0e26b497abd";
    private static final double VARIATION_THRESHOLD = 0.001;

    private static final double THRESHOLD = 0.001;
    private static final long ALERT_INTERVAL = 10; // seconds
    private boolean alerted;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private double lastPrice;

    public String getApiKey() {
        return apiKey.toString();
    }

    public test() {
        CryptoPrice api = new CryptoPrice(apiKey);
        lastPrice = api.getPrice(BTC_SYMBOL);
    }

    public String start; {
        executorService.scheduleAtFixedRate(() -> {
            CryptoPrice api = new CryptoPrice(apiKey);
            double currentPrice = api.getPrice(BTC_SYMBOL);
            double variation = (currentPrice - lastPrice) / lastPrice;
            if (Math.abs(variation) >= VARIATION_THRESHOLD) {
                String direction = variation > 0 ? "up" : "down";
                String emoji = variation > 0 ? "📈" : "📉";
                String priceString = String.format("$%.2f", currentPrice);
                String bitcoinAlert = "Bitcoin is " + direction + "! " + priceString + " (" + (variation * 100) + "%) " + emoji;
                System.out.println(bitcoinAlert);
                lastPrice = currentPrice;
                return;
            } else if (Math.abs(variation) < THRESHOLD && alerted) {
                alerted = false;
            }
        }, 0, ALERT_INTERVAL, TimeUnit.SECONDS);
    }

    public void stopAlerts() {
        executorService.shutdown();
    }
}

