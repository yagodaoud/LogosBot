package main.java.commands;

import main.java.audio.PlayCommand;
import main.java.audio.RandomAudioPlayer;
import main.java.crypto.BitcoinPriceAlert;
import main.java.crypto.BitcoinPriceTrigger;
import main.java.crypto.CryptoPriceDiscord;
import main.java.crypto.ScheduledAlert;
import net.dv8tion.jda.api.entities.*;
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

    private final Map<String, ScheduledAlert> scheduledAlertMap = new HashMap<>();
    //private final String audioDirectoryPath = "C:\\Users\\yagod\\Desktop\\Audios";

    private int toggle = 0;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        AudioManager audioManager = null;
        Locale locale = Locale.US;

        switch (command) {
            case "test" -> event.reply("test").queue();
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
                double finalPercentage = thresholdPercentage/100;
                BitcoinPriceAlert alert = new BitcoinPriceAlert(finalPercentage);
                alert.startAlert(event.getTextChannel());
                event.reply("Alert created!").queue();

            }
            case "bitcoin-alert-stop" -> {
                BitcoinPriceAlert alert = new BitcoinPriceAlert();
                alert.stopAlert();
                event.reply("Alert disabled!").queue();

            }
            case "bitcoin-scheduled-alert-start" -> {
                ScheduledAlert scheduledAlert = new ScheduledAlert(event.getTextChannel());
                scheduledAlert.start(LocalTime.of(21, 0));
                scheduledAlertMap.put(event.getTextChannel().getId(), scheduledAlert);
                event.reply("The daily closing price of Bitcoin will be displayed from now on!").queue();

            }
            case "bitcoin-scheduled-alert-stop" -> {
                ScheduledAlert scheduledAlert = scheduledAlertMap.get(event.getTextChannel().getId());
                if (scheduledAlert != null) {
                    scheduledAlert.stop();
                    scheduledAlertMap.remove(event.getTextChannel().getId());
                    event.reply("The current scheduled alert has been stopped!").queue();
                }
            }
            case "bitcoin-price-trigger" -> {
                double targetPrice = Objects.requireNonNull(event.getOption("target-price")).getAsDouble();
                BitcoinPriceTrigger bitcoinPriceTrigger = new BitcoinPriceTrigger(targetPrice);
                bitcoinPriceTrigger.setPriceForNotification(event.getTextChannel(), event.getUser().getId());
                event.reply(String.format(locale,"Tracking Bitcoin price when it reaches $%,.2f!", targetPrice)).queue();

            }
            case "random-audio-player" -> {
                RandomAudioPlayer randomAudioPlayer = new RandomAudioPlayer(event.getGuild(), event.getVoiceChannel());
                randomAudioPlayer.playRandomAudio();
                event.reply("EU SOU MAIS LOUCO QUE TODOS VOCÊS").queue();
            }
            case "join" -> {
                Member member = event.getMember();
                Guild guild = event.getGuild();
                TextChannel channel = event.getTextChannel();
                GuildVoiceState voiceState = member.getVoiceState();

                PlayCommand.joinVoiceChannel(channel, voiceState, guild);

                event.reply("Joining voice channel").setEphemeral(true).queue();

            }
            case "play" -> {
                OptionMapping songOption = event.getOption("song_search_or_link");
                String songUrl = songOption.getAsString();
                System.out.println(songUrl);

                TextChannel channel = event.getTextChannel();
                Member member = event.getMember();
                GuildVoiceState voiceState = member.getVoiceState();
                member.getVoiceState();

                event.reply("Adding song!").setEphemeral(true).queue();


                PlayCommand playCommand = new PlayCommand(songUrl);
                playCommand.Play(channel, member, voiceState);
                playCommand.handle(channel);

                VoiceChannel audioChannel = (VoiceChannel) voiceState.getChannel();
                if (!audioChannel.getGuild().getAudioManager().isConnected()) {
                    audioChannel.getGuild().getAudioManager().openAudioConnection(audioChannel);
                }
            }
            case "playlist-link" -> {
                OptionMapping playlistOption = event.getOption("playlist_link");

                List<String> urls = new ArrayList<>(Collections.singletonList(playlistOption.getAsString()));
                System.out.println(urls);

                TextChannel channel = event.getTextChannel();
                Member member = event.getMember();
                GuildVoiceState voiceState = member.getVoiceState();
                member.getVoiceState();

                event.reply("Adding playlist!").setEphemeral(true).queue();

                PlayCommand playCommand = new PlayCommand(urls);
                playCommand.Play(channel, member, voiceState);
                PlayCommand.addPlaylist(event.getTextChannel(), event.getGuild(), urls);
                playCommand.handle(channel);

            }
            case "skip" -> {
                PlayCommand.skipTrack(event.getTextChannel(), event.getGuild());
                event.reply("Skipped to next track").setEphemeral(true).queue();
            }
            case "leave" -> {
                PlayCommand.leaveVoiceChannel(event.getTextChannel(), event.getGuild());
                event.reply("Left the voice channel").setEphemeral(true).queue();
            }
            case "stop" -> {
                PlayCommand.stopCommand(event.getTextChannel(), event.getGuild());
                event.reply("Stopped the queue").setEphemeral(true).queue();
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
        //Test
        commandData.add(Commands.slash("test", "testing"));

        OptionData cryptoTag = new OptionData(OptionType.STRING, "crypto-symbol", "Enter the symbol of the crypto you want the price of", true);
        commandData.add(Commands.slash("crypto-price", "Get the price of a crypto").addOptions(cryptoTag));

        OptionData threshold = new OptionData(OptionType.STRING, "percentage", "Percentage that will trigger the alert (in %)", true);
        commandData.add(Commands.slash("bitcoin-alert-start", "Create a tracker for Bitcoin").addOptions(threshold));
        commandData.add(Commands.slash("bitcoin-alert-stop", "Disable previous tracker for Bitcoin"));

        commandData.add(Commands.slash("bitcoin-scheduled-alert-start", "Send the price of Bitcoin at 9:00 PM BRT everyday"));
        commandData.add(Commands.slash("bitcoin-scheduled-alert-stop", "Disable the scheduled alert"));

        OptionData targetPrice = new OptionData(OptionType.STRING, "target-price", "Target Price desired", true);
        commandData.add(Commands.slash("bitcoin-price-trigger", "Bitcoin Price tracker, if the value is reached, the bot will send a notification").addOptions(targetPrice));

        OptionData songUrl = new OptionData(OptionType.STRING, "song_search_or_link", "Enter the song search or url", true);
        commandData.add(Commands.slash("play", "Plays a song").addOptions(songUrl));

        OptionData playlistOption = new OptionData(OptionType.STRING, "playlist_link", "Enter the playlist link", true);
        commandData.add(Commands.slash("playlist-link", "Load and play a playlist from Youtube").addOptions(playlistOption));

        commandData.add(Commands.slash("join", "The bot will join the current channel"));
        commandData.add(Commands.slash("skip", "Skips to next track"));
        commandData.add(Commands.slash("leave", "The bot will leave the current channel"));
        commandData.add(Commands.slash("stop", "Stops the track and clears the queue"));
        commandData.add(Commands.slash("loop", "Loops through the queue"));
        commandData.add(Commands.slash("random-audio-player", "The bot will join a voice channel and play a random audio"));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}

