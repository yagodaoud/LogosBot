package main.java;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class BotCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("test")) {
            event.reply("test").queue();


        }

        else if (command.equals("crypto-price")) {
            OptionMapping cryptoOption = event.getOption("crypto-symbol");
            String cryptoSymbolDiscord = cryptoOption.getAsString().toUpperCase();

            System.out.println(cryptoSymbolDiscord);

            CryptoPrice cmcApi = new CryptoPrice(cryptoSymbolDiscord);
            double price = cmcApi.getPrice(cryptoSymbolDiscord);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            String priceString = formatter.format(price);
            event.reply("Request sent!").setEphemeral(false).queue();
            event.getChannel().sendMessage("The current price of " + cryptoSymbolDiscord + " is " + priceString).queue();
        }
    }

    //Registers the commands
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        //Test
        commandData.add(Commands.slash("test", "testing"));

        //say <message>
        OptionData cryptoTag = new OptionData(OptionType.STRING, "crypto-symbol", "Enter the symbol of the crypto you want the price of", true);
        commandData.add(Commands.slash("crypto-price", "Get the price of a crypto").addOptions(cryptoTag));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}

