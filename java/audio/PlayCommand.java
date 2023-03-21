package main.java.audio;

import net.dv8tion.jda.api.entities.*;

public class PlayCommand {

    private TextChannel channel;
    private  Member member;
    private  GuildVoiceState voiceState;
    public String url;

    public PlayCommand(String urlDiscord){
        this.url = urlDiscord;
    }

    public void Play(TextChannel channel, Member member, GuildVoiceState voiceState) {
        this.channel = channel;
        this.member = member;
        this.voiceState = voiceState;
    }

    public void handle(TextChannel channel) {
        AudioChannel audioChannel = voiceState.getChannel();

        if (!(audioChannel instanceof VoiceChannel)) {
            channel.sendMessage("I have to be in voice channel first").queue();
            return;
        }

        VoiceChannel voiceChannel = (VoiceChannel) audioChannel;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.getChannel().equals(voiceChannel)) {
            channel.sendMessage("You must be in the same voice channel as me").queue();
            return;
        }

        PlayerManager.getInstance(url)
                .loadAndPlay(channel, url);
    }
}
