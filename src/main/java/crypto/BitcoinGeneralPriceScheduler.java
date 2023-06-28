package main.java.crypto;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitcoinGeneralPriceScheduler {
    private static final String btc = "BTC";
    private static double btcPrice;
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long ALERT_INTERVAL= 600;

    public BitcoinGeneralPriceScheduler(){
        btcPrice = CryptoPriceDiscord.getPrice(btc);
    }

    public static double getBtcPrice() {
        return btcPrice;
    }

    public static double getBtcPriceEveryTenMinutes(){
        executorService.scheduleAtFixedRate(() -> btcPrice = CryptoPriceDiscord.getPrice(btc), 0, ALERT_INTERVAL, TimeUnit.SECONDS);
        return btcPrice;
    }
}
