package io.starter.biruk.ezymusic.events.media;

/**
 * Created by Biruk on 10/21/2017.
 */
public class ChangePlayPauseEvent {
    private boolean playing;

    public ChangePlayPauseEvent(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }
}
