package io.starter.biruk.ezymusic.events.media;

/**
 * Created by Biruk on 10/21/2017.
 */
public class PlayTrackEvent {
    private int index;

    public PlayTrackEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
