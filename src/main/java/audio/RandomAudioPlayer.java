package main.java.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.Random;


public class RandomAudioPlayer extends AudioPlayerSendHandler {
    private final Guild guild;
    private final VoiceChannel voiceChannel;
    private String audioDirectoryPath = "C:/Users/yagod/Desktop/Audios";
    private final Random rand = new Random();

     public RandomAudioPlayer(Guild guild, VoiceChannel voiceChannel) {
         this.guild = guild;
         this.voiceChannel = voiceChannel;

    }


    public void playRandomAudio() {
        try {
            File[] files = new File(audioDirectoryPath).listFiles();
            if (files == null || files.length == 0) {
                System.out.println("No audio files found in directory");
                return;
            }
            int index = rand.nextInt(files.length);
            File randomFile = files[index];
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(randomFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            GuildVoiceState state = guild.getSelfMember().getVoiceState();
            VoiceChannel voiceChannel = (VoiceChannel) state.getChannel();
            if (voiceChannel == null) {
                System.out.println("Failed to get voice channel");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
