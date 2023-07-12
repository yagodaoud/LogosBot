package main.java.crypto;

import net.dv8tion.jda.api.entities.TextChannel;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinPriceAlert {

    private static double VARIATION_THRESHOLD;

    private static double THRESHOLD;
    private static final long ALERT_INTERVAL= 3600;
    private boolean alerted;
    private double lastPrice;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final DecimalFormat priceFormatter = new DecimalFormat("#,##0.00");


    public BitcoinPriceAlert(){
    }

    public BitcoinPriceAlert(double percentage){
        lastPrice = BitcoinPriceGetter();
        THRESHOLD = percentage;
        VARIATION_THRESHOLD = percentage;
    }
    public double BitcoinPriceGetter() {
        return BitcoinGeneralPriceScheduler.getBtcPrice();
    }

   public double priceVariationCalculator(double currentPrice, double lastPrice){
        /*Formula to get the
          variation e.g. The price was 100,
          now it's 120 -> (120 - 100) / 100 = 0.2 * 100 = 20%
        */
       return ((currentPrice - lastPrice) / lastPrice) * 100;
    }

    public void startAlert(TextChannel channel) {
        System.out.println("started");
        executorService.scheduleAtFixedRate(() -> {

            double currentPrice = BitcoinPriceGetter();
            double variation = priceVariationCalculator(currentPrice, lastPrice);



            System.out.println(variation);
            System.out.println("Current price is " + currentPrice);
            System.out.println("Variation is: " + String.format("%.2f%%", variation));

            if (Math.abs(variation) >= VARIATION_THRESHOLD) {
                String direction = variation > 0 ? "up" : "down";
                String emoji = variation > 0 ? "📈" : "📉";
                String priceString = "$" + priceFormatter.format(currentPrice);
                String variationString = String.format("%.2f%%", variation);
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


    public void stopAlert() {
        executorService.shutdown();
        System.out.println("stopped");
    }
}

