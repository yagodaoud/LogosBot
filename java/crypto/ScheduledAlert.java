package main.java.crypto;


import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.TextChannel;

public class ScheduledAlert { //Bitcoin update at every candle close (12 am GMT [9 pm BRT])
    private final Timer timer;
    private TimerTask task;
    private final TextChannel channel;
    private final String symbol;

    public ScheduledAlert(TextChannel channel) {
        this.channel = channel;
        this.symbol = "BTC";
        this.timer = new Timer();
    }

    public void start(LocalTime time) { //Getting the time from BotCommands parameter
        // Cancel the task if it is already scheduled
        if (task != null) {
            task.cancel();
        }

        task = new TimerTask() { //Start TimerTask
            @Override
            public void run() {
                System.out.println("Started");
                CryptoPrice cmcApi = new CryptoPrice(symbol); //Get Bitcoin price
                double price = cmcApi.getPrice(symbol);

                NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                String priceString = formatter.format(price);
                System.out.println((priceString));
                channel.sendMessage("The closing price of Bitcoin is " + priceString).queue();
            }
        };

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")); //Sync date with time zone
        ZonedDateTime scheduledTime = ZonedDateTime.of(now.toLocalDate(), time, now.getZone());

        if (now.compareTo(scheduledTime) > 0) {
            scheduledTime = scheduledTime.plusDays(1); //If the command has been triggered after time in LocalTime, set it to next day at the set time
        }

        long delay = Duration.between(now, scheduledTime).toMillis();
        timer.schedule(task, delay, TimeUnit.DAYS.toMillis(1));
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            System.out.println("Stopped");
        }
    }
}