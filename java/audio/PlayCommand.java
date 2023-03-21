package main.java.audio;

import net.dv8tion.jda.api.entities.*;

public class PlayCommand {

    private final TextChannel channel;
    private final Member member;
    private final GuildVoiceState voiceState;
    private final String url;

    public PlayCommand(TextChannel channel, Member member, GuildVoiceState voiceState) {
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
                .loadAndPlay(channel, "https://www.youtube.com/watch?v=79BE4kOPbdk");
    }
}
