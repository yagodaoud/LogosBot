package main.java.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private BlockingQueue<AudioTrack> queue;
    private boolean isRepeat = false;
    boolean isShuffled = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track){
        if (!this.player.startTrack(track, true)){
            this.queue.offer(track);
        }
    }

    public boolean clearQueue() {
        if (!this.queue.isEmpty()) {
            this.queue.clear();
            nextTrack();
            return true;
        } else {
            return false;
        }
    }

    public boolean shuffleQueue() {
        BlockingQueue<AudioTrack> copyQueue = new LinkedBlockingQueue<>(queue);
        List<AudioTrack> list = new ArrayList<>(copyQueue);

        if (!isShuffled) {
            Collections.shuffle(list);
            this.queue = new LinkedBlockingQueue<>(list);
            isShuffled = true;
        } else {
            this.queue.removeIf(track -> !copyQueue.contains(track));
            this.queue = copyQueue;
            isShuffled = false;
        }

        return isShuffled;
    }

    public void nextTrack(){
        if (isRepeat){
            queue(player.getPlayingTrack().makeClone());
        }
        this.player.startTrack(this.queue.poll(), false);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
        if (endReason.mayStartNext) {
            nextTrack();
            if (isRepeat) {
                queue(track.makeClone());
                nextTrack();
            }
        }
    }

    public boolean setRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
        return isRepeat;
    }
}
