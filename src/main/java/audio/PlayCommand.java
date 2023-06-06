package main.java.audio;

import net.dv8tion.jda.api.entities.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayCommand {

    private Member member;
    private GuildVoiceState voiceState;
    public String url;
    private static boolean joined;
    private static int counter;

    private List<String> urls = new ArrayList<>();

    public PlayCommand(String url) {
        this.url = url;
    }
    public PlayCommand(List<String> urls) {
        this.urls = urls;
    }

    public void Play(TextChannel channel, Member member, GuildVoiceState voiceState) {
        this.member = member;
        this.voiceState = voiceState;
    }

    public static PlayCommand skipTrack(TextChannel channel, Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        musicManager.scheduler.nextTrack();
        channel.sendMessage("Skipped the current song").queue();
        return null;
    }
    public static PlayCommand stopCommand(TextChannel channel, Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        PlayerManager.stopAndClear(musicManager.audioPlayer);
        channel.sendMessage("Stopped and cleared the queue").queue();
        return null;

    }
    public static void addPlaylist(TextChannel channel, Guild guild, List<String> urls) {
        new PlayerManager().loadPlaylist(channel, urls.toString());

        for(String url : urls){
        PlayCommand playCommand = new PlayCommand(url);
        playCommand.Play(channel, guild.getSelfMember(), guild.getSelfMember().getVoiceState());
        playCommand.handle(channel);
        }
    }



    public static void joinVoiceChannel(TextChannel channel, GuildVoiceState voiceState, Guild guild) {


        if (!voiceState.inAudioChannel()) {
            channel.sendMessage("You must be in a voice channel to use this command.").queue();
            return;
        }

        AudioChannel audioChannel = voiceState.getChannel();

        if (audioChannel == null) {
            channel.sendMessage("Failed to join voice channel.").queue();
            return;
        }

        if (voiceState.inAudioChannel()) {

            net.dv8tion.jda.api.managers.AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(audioChannel);
            channel.sendMessage("Joining voice channel: " + audioChannel.getName()).queue();
            joined = true;
            counter += 1 ;
        }

        if (joined & counter == 2) {
            channel.sendMessage("I'm already in the voice channel").queue();
            counter = 0;
        }
    }


    public static void leaveVoiceChannel(TextChannel channel, Guild guild) {
        final AudioChannel connectedChannel = guild.getSelfMember().getVoiceState().getChannel();
        if (connectedChannel != null) {
            connectedChannel.getGuild().getAudioManager().closeAudioConnection();
            channel.sendMessage("Left the voice channel").queue();
            joined = false;
        } else {
            channel.sendMessage("Not connected to a voice channel.").queue();
        }
    }


    public void handle(TextChannel channel) {
        AudioChannel audioChannel = voiceState.getChannel();


        if (!(audioChannel instanceof VoiceChannel)) {
            channel.sendMessage("You have to be in a voice channel first").queue();
            return;
        }

        VoiceChannel voiceChannel = (VoiceChannel) audioChannel;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.getChannel().equals(voiceChannel)) {
            channel.sendMessage("You must be in the same voice channel as me").queue();
            return;
        }

        String link = String.join(" ", url);

        if (!isUrl(link) || !link.startsWith("https://www.youtube.com/playlist")) {
            link = "ytsearch:" + link;
        }
        if (link.startsWith("https://www.youtube.com/playlist")) {
            PlayerManager.getInstance().loadPlaylist(channel, link);
        } else if (!link.isEmpty()) {
            PlayerManager.getInstance().loadAndPlay(channel, link);
        }
    }

    private boolean isUrl(String url) {
        try{
            new URL(url);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}