package main.java.commands.view;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

public class HelpCryptoView {

    public static MessageCreateData getCryptoView(){

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Crypto Commands")
                .setColor(Color.BLUE)
                .addField("Crypto Price", "Get the price of a crypto by its tag [BTC, ETH, etc].", true)
                .addField("Bitcoin Alert", "Set a threshold percentage that will e triggered every hour if reached.", true)
                .addField("Bitcoin Scheduled Alert", "Schedule a daily alert to check the Bitcoin price at 12 am UTC.", true)
                .addField("Bitcoin Price Trigger", "Create an alert to be triggered when Bitcoin reaches the target price.", true);
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                .addEmbeds(builder.build());

        return messageBuilder.build();
    }
}
