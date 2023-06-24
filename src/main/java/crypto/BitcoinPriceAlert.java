package main.java.crypto;

import net.dv8tion.jda.api.entities.TextChannel;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceAlert { //Bitcoin price alert at a certain percentage on a certain timeframe


    private static final String BTC_SYMBOL = "BTC";
    private static final double VARIATION_THRESHOLD = 0.01; //Set the variation deserved

    private static final double THRESHOLD = 0.01;
    private static final long ALERT_INTERVAL= 3600; //Set the time frame in seconds
    private boolean alerted;
    private double lastPrice;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final DecimalFormat priceFormatter = new DecimalFormat("#,##0.00");


    public BitcoinPriceAlert(){
        lastPrice = BitcoinPriceGetter();
    }
    public double BitcoinPriceGetter() {
        return ApiConnection.getPrice(BTC_SYMBOL);
    }

    public void startAlert(TextChannel channel) {
        System.out.println("started");
        executorService.scheduleAtFixedRate(() -> {

            double currentPrice = BitcoinPriceGetter();
            double variation = (currentPrice - lastPrice) / lastPrice;

              /*Formula to get the
              variation e.g. The price was 100,
              now it's 120 -> (120 - 100) / 100 = 0.2 * 100 = 20%
             */

            System.out.println(variation);
            System.out.println("Current price is " + currentPrice);
            System.out.println("Variation is: " + String.format("%.2f%%", variation * 100));

            if (Math.abs(variation) >= VARIATION_THRESHOLD) {
                String direction = variation > 0 ? "up" : "down";
                String emoji = variation > 0 ? "📈" : "📉";
                String priceString = "$" + priceFormatter.format(currentPrice);
                String variationString = String.format("%.2f%%", variation * 100);
                String bitcoinAlert = "Bitcoin is " + direction + "! " + priceString +
                        " (" + variationString + " in the last hour) " + emoji;
                channel.sendMessage(bitcoinAlert).queue();
                System.out.println(bitcoinAlert);
                lastPrice = currentPrice;
            } else {
                alerted = Math.abs(variation) < THRESHOLD && alerted;
            }
        }, 0, ALERT_INTERVAL, TimeUnit.SECONDS);
    }


    public void stopAlert(TextChannel channel) {
        executorService.shutdown();
        System.out.println("stopped");
    }
}

