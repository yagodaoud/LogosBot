package main.java.audio;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import java.net.URL;

import static main.java.audio.PlayerManager.*;

public class PlayCommand {

    private Member member;
    private GuildVoiceState voiceState;
    public String url;
    private static boolean joined = false;
    private static int toggleRepeat = 0;


    public PlayCommand(String url) {
        this.url = url;
    }

    public void Play (Member member, GuildVoiceState voiceState) {
        this.member = member;
        this.voiceState = voiceState;
    }

    public static String skipTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return musicManager.scheduler.nextTrack();
    }
    public static String stopTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return stop(musicManager.audioPlayer);
    }

    public static String resumeTrack(Guild guild) {
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        return resume(musicManager.audioPlayer);
    }

    public static String clearQueue(TextChannel channel){
       return PlayerManager.getInstance().clearQueue(channel);
    }

    public static String shuffleQueue(TextChannel channel){
        return PlayerManager.getInstance().shuffleQueue(channel);
    }

    public static StringBuilder getCurrentTrack(TextChannel channel){
        return PlayerManager.getInstance().getCurrentTrack(channel);
    }

    public static StringBuilder getQueueTracks(TextChannel channel){
        return PlayerManager.getInstance().getQueueTracks(channel);
    }

    public static String loopTrack(Guild guild) {
        toggleRepeat += 1;
        final AudioManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        if (toggleRepeat == 1) {
            musicManager.scheduler.setRepeat(true);
            return ("Loop is on!");
        } else if (toggleRepeat == 2){
            musicManager.scheduler.setRepeat(false);
            toggleRepeat = 0;
            return ("Loop is off!");
        }
        return ("An error occurred.");
    }


    public static String joinVoiceChannel(GuildVoiceState voiceState, Guild guild) {


        if (!voiceState.inAudioChannel()) {
            return ("You must be in a voice channel to use this command.");
        }

        AudioChannel audioChannel = voiceState.getChannel();

        if (audioChannel == null) {
            return ("Failed to join voice channel.");
        }

        if (!joined) {
            net.dv8tion.jda.api.managers.AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(audioChannel);
            joined = true;
            return ("Joining voice channel: `" + audioChannel.getName() + "`.");
        } else if (joined){
            return ("I'm already in a voice channel.");
        }
        return ("Failed to join voice channel.");
    }


    public static String leaveVoiceChannel(Guild guild) {
        final AudioChannel connectedChannel = guild.getSelfMember().getVoiceState().getChannel();
        if (connectedChannel != null) {
            connectedChannel.getGuild().getAudioManager().closeAudioConnection();
            joined = false;
            return "Left the voice channel.";
        } else {
            return ("Not connected to a voice channel.");
        }
    }


    public String handle(TextChannel channel) {
        AudioChannel audioChannel = voiceState.getChannel();


        if (!(audioChannel instanceof VoiceChannel)) {
            return ("You must be in a voice channel to use this command.");
        }

        VoiceChannel voiceChannel = (VoiceChannel) audioChannel;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.getChannel().equals(voiceChannel)) {
            return ("You must be in the same voice channel as me.");
        }


        if (url != null) {
            if (isUrl(url)) {
               return PlayerManager.getInstance().loadAndPlay(channel, url);
            } else if (!isUrl(url)) {
                String search = String.join(" ", url);
                String link = "ytsearch:" + search;
                return PlayerManager.getInstance().loadAndPlay(channel, link);
            } else {
                return ("Song not found.");
            }
        }
        return ("An error occurred.");
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