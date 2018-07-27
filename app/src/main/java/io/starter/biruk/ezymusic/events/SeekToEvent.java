package io.starter.biruk.ezymusic.events;

/**
 * Created by biruk on 27/07/18.
 */
public class SeekToEvent {
    private int progress;

    public SeekToEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
