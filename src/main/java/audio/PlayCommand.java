package main.java.audio;

import net.dv8tion.jda.api.entities.*;

import java.net.URL;

public class PlayCommand {

    private Member member;
    private GuildVoiceState voiceState;
    public String url;


    public PlayCommand(String urlDiscord) {
        this.url = urlDiscord;
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

        net.dv8tion.jda.api.managers.AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(audioChannel);
        channel.sendMessage("Joined voice channel: " + audioChannel.getName()).queue();
    }


    public static void leaveVoiceChannel(TextChannel channel, Guild guild) {
        final AudioChannel connectedChannel = guild.getSelfMember().getVoiceState().getChannel();
        if (connectedChannel != null) {
            connectedChannel.getGuild().getAudioManager().closeAudioConnection();
            channel.sendMessage("Left the voice channel").queue();
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

//        if (selfVoiceState.inAudioChannel()){
//            channel.sendMessage("I'm already in a voice channel!").queue();
//            return;
//        }

        VoiceChannel voiceChannel = (VoiceChannel) audioChannel;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.getChannel().equals(voiceChannel)) {
            channel.sendMessage("You must be in the same voice channel as me").queue();
            return;
        }

        String link = String.join(" ", url);

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance()
                .loadAndPlay(channel, link);
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