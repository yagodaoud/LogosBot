package main.java.commands;

import main.java.audio.PlayCommand;
import main.java.audio.RandomAudioPlayer;
import main.java.crypto.BitcoinPriceAlert;
import main.java.crypto.BitcoinPriceTrigger;
import main.java.crypto.BitcoinScheduledAlert;
import main.java.crypto.CryptoPriceDiscord;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.*;
import java.text.NumberFormat;
import java.util.List;

public class BotCommands extends ListenerAdapter {

    private final Map<String, BitcoinScheduledAlert> scheduledAlertMap = new HashMap<>();
    private int toggle = 0;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        AudioManager audioManager = null;
        Locale locale = Locale.US;

        switch (command) {
            case "crypto-price" -> {
                OptionMapping cryptoOption = event.getOption("crypto-symbol");
                String cryptoSymbolDiscord = cryptoOption.getAsString().toUpperCase();
                System.out.println(cryptoSymbolDiscord);
                double price = CryptoPriceDiscord.getPrice(cryptoSymbolDiscord);
                NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                String priceString = formatter.format(price);
                event.reply("Request sent!").setEphemeral(false).queue();
                event.getChannel().sendMessage("The current price of " + cryptoSymbolDiscord + " is " + priceString).queue();
            }
            case "bitcoin-alert-start" -> {
                double thresholdPercentage = Objects.requireNonNull(event.getOption("percentage")).getAsDouble();
                BitcoinPriceAlert alert = new BitcoinPriceAlert(thresholdPercentage);
                alert.startAlert(event.getChannel().asTextChannel());
                String message = String.format("Alert created! Threshold is %s%%.", thresholdPercentage);
                event.reply(message).queue();
            }
            case "bitcoin-alert-stop" -> {
                BitcoinPriceAlert alert = new BitcoinPriceAlert();
                alert.stopAlert();
                event.reply("Alert disabled!").queue();

            }
            case "bitcoin-scheduled-alert-start" -> {
                BitcoinScheduledAlert scheduledAlert = new BitcoinScheduledAlert(event.getChannel().asTextChannel());
                scheduledAlert.start(LocalTime.of(21, 0));
                scheduledAlertMap.put(event.getChannel().asTextChannel().getId(), scheduledAlert);
                event.reply("The daily closing price of Bitcoin will be displayed from now on!").queue();

            }
            case "bitcoin-scheduled-alert-stop" -> {
                BitcoinScheduledAlert scheduledAlert = scheduledAlertMap.get(event.getChannel().asTextChannel().getId());
                if (scheduledAlert != null) {
                    scheduledAlert.stop();
                    scheduledAlertMap.remove(event.getChannel().asTextChannel().getId());
                    event.reply("The current scheduled alert has been stopped!").queue();
                }
            }
            case "bitcoin-price-trigger" -> {
                double targetPrice = Objects.requireNonNull(event.getOption("target-price")).getAsDouble();
                BitcoinPriceTrigger bitcoinPriceTrigger = new BitcoinPriceTrigger(targetPrice);
                bitcoinPriceTrigger.setPriceForNotification(event.getChannel().asTextChannel(), event.getUser().getId());
                event.reply(String.format(locale, "Tracking Bitcoin price when it reaches $%,.2f!", targetPrice)).queue();

            }
            case "join" -> {
                Member member = event.getMember();
                Guild guild = event.getGuild();
                TextChannel channel = event.getChannel().asTextChannel();
                GuildVoiceState voiceState = member.getVoiceState();

                PlayCommand.joinVoiceChannel(channel, voiceState, guild);
            }
            case "play" -> {
                OptionMapping songOption = event.getOption("song_search_or_link");
                String songUrl = songOption.getAsString();
                System.out.println(songUrl);

                TextChannel channel = event.getChannel().asTextChannel();
                Member member = event.getMember();
                GuildVoiceState voiceState = member.getVoiceState();
                member.getVoiceState();

                event.reply("Adding song!").setEphemeral(false).queue();

                PlayCommand playCommand = new PlayCommand(songUrl);
                playCommand.Play(member, voiceState);
                playCommand.handle(channel);

                VoiceChannel audioChannel = (VoiceChannel) voiceState.getChannel();
                if (!audioChannel.getGuild().getAudioManager().isConnected()) {
                    audioChannel.getGuild().getAudioManager().openAudioConnection(audioChannel);
                }
            }
            case "skip" -> {
                PlayCommand.skipTrack(event.getGuild());
                event.reply("Skipped to next track").setEphemeral(false).queue();
            }
            case "leave" -> {
                PlayCommand.leaveVoiceChannel(event.getChannel().asTextChannel(), event.getGuild());
                event.reply("Left the voice channel").setEphemeral(false).queue();
            }
            case "stop" -> {
                if (PlayCommand.stopTrack(event.getGuild())) {
                    event.reply("Stopped the queue").queue();
                } else {
                    event.reply("The queue is already paused").queue();
                }
            }
            case "resume" -> {
                if (PlayCommand.resumeTrack(event.getGuild())) {
                    event.reply("Resumed the queue").queue();
                } else {
                    event.reply("The queue is already playing").queue();
                }
            }
            case "clear" -> {
                if (PlayCommand.clearQueue(event.getChannel().asTextChannel())) {
                    event.reply("Cleared the queue").queue();
                } else {
                    event.reply("Queue already empty").queue();
                }
            }
            case "shuffle" -> {
                if (PlayCommand.shuffleQueue(event.getChannel().asTextChannel())) {
                    event.reply("Shuffle is on!").queue();
                } else {
                    event.reply("Shuffle is off!").queue();
                }
            }
            case "queue" -> {
                event.reply(String.format("Queue requested by <@%s>", event.getUser().getId())).queue();
                PlayCommand.getQueueTracks(event.getChannel().asTextChannel());
            }
            case "loop" -> {
                PlayCommand.loopTrack(event.getGuild());
                toggle += 1;
                switch (toggle) {
                    case 1 -> event.reply("Loop is on!").queue();
                    case 2 -> {
                        event.reply("Loop is off!").queue();
                        toggle = 0;
                    }
                }
            }
        }

    }


    //Registers the commands
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        OptionData cryptoTag = new OptionData(OptionType.STRING, "crypto-symbol", "Enter the symbol of the crypto you want the price of", true);
        commandData.add(Commands.slash("crypto-price", "Get the price of a crypto.").addOptions(cryptoTag));

        OptionData threshold = new OptionData(OptionType.STRING, "percentage", "Percentage that will trigger the alert (in %)", true);
        commandData.add(Commands.slash("bitcoin-alert-start", "Create a tracker for Bitcoin.").addOptions(threshold));
        commandData.add(Commands.slash("bitcoin-alert-stop", "Disable previous tracker for Bitcoin."));

        commandData.add(Commands.slash("bitcoin-scheduled-alert-start", "Send the price of Bitcoin at 9:00 PM BRT everyday."));
        commandData.add(Commands.slash("bitcoin-scheduled-alert-stop", "Disable the scheduled alert"));

        OptionData targetPrice = new OptionData(OptionType.STRING, "target-price", "Target Price desired", true);
        commandData.add(Commands.slash("bitcoin-price-trigger", "Bitcoin Price tracker, if the value is reached, the bot will send a notification.").addOptions(targetPrice));

        OptionData songUrl = new OptionData(OptionType.STRING, "song_search_or_link", "Enter the song search or url", true);
        commandData.add(Commands.slash("play", "Play a song or a playlist.").addOptions(songUrl));
        commandData.add(Commands.slash("join", "Joins the current channel."));
        commandData.add(Commands.slash("skip", "Skips to next track."));
        commandData.add(Commands.slash("leave", "Disconnects the bot from the current channel."));
        commandData.add(Commands.slash("stop", "Stops the current queue."));
        commandData.add(Commands.slash("resume", "Resume the current queue."));
        commandData.add(Commands.slash("clear", "Clear the queue."));
        commandData.add(Commands.slash("shuffle", "Shuffle the queue."));
        commandData.add(Commands.slash("queue", "Display the queue."));
        commandData.add(Commands.slash("loop", "Toggles the loop of the queue."));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}