package io.starter.biruk.ezymusic.events.media.playbackMode;

/**
 * Created by biruk on 27/07/18.
 */

public class PlayPauseStatusEvent {

    private boolean isPlaying;

    public PlayPauseStatusEvent(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
