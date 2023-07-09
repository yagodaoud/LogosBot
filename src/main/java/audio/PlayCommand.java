package main.java.audio;

import net.dv8tion.jda.api.entities.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static main.java.audio.PlayerManager.*;

public class PlayCommand {

    private Member member;
    private GuildVoiceState voiceState;
    public String url;
    private static boolean joined;
    private static int counter;
    private static int toggleRepeat = 0;


    public PlayCommand(String url) {
        this.url = url;
    }

    public void Play (Member member, GuildVoiceState voiceState) {
        this.member = member;
        this.voiceState = voiceState;
    }

    public static void skipTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        musicManager.scheduler.nextTrack();
    }
    public static boolean stopTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return stop(musicManager.audioPlayer);
    }

    public static boolean resumeTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return resume(musicManager.audioPlayer);
    }

    public static boolean clearQueue(TextChannel channel){
       return PlayerManager.getInstance().clearQueue(channel);
    }

    public static boolean shuffleQueue(TextChannel channel){
        return PlayerManager.getInstance().shuffleQueue(channel);
    }

    public static void loopTrack(Guild guild) {
        toggleRepeat += 1;
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        if (toggleRepeat == 1) {
            musicManager.scheduler.setRepeat(true);
        } else if (toggleRepeat == 2){
            musicManager.scheduler.setRepeat(false);
            toggleRepeat = 0;
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


        if (url != null) {
            if (isUrl(url)) {
                PlayerManager.getInstance().loadAndPlay(channel, url);
            } else if (!isUrl(url)) {
                String search = String.join(" ", url);
                String link = "ytsearch:" + search;
                PlayerManager.getInstance().loadAndPlay(channel, link);
            } else {
                channel.sendMessage("Song not found").queue();
            }
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