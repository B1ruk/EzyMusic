package io.starter.biruk.ezymusic.events.media;

/**
 * Created by Biruk on 10/21/2017.
 */
public class SeekToEvent {
    private int index;

    public SeekToEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
