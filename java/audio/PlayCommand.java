package main.java.audio;


import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PlayCommand {

     final TextChannel channel;
     final Member member;
     final GuildVoiceState voiceState;

    public PlayCommand(TextChannel channel, Member member, GuildVoiceState voiceState) {
        this.channel = channel;
        this.member = member;
        this.voiceState = voiceState;
    }

    public void handle(TextChannel channel){

        if (!voiceState.inAudioChannel()){
            channel.sendMessage("I have to be in voice channel first").queue();
        }

        final GuildVoiceState memberVoiceState = member.getVoiceState();

            if (!memberVoiceState.inAudioChannel()){
                channel.sendMessage("You must be in a voice channel");
            }


        PlayerManager.getInstance()
                .loadAndPlay(channel, "https://www.youtube.com/watch?v=K4DyBUG242c");

        }


    }


