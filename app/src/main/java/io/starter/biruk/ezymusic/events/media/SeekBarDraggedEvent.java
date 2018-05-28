package io.starter.biruk.ezymusic.events.media;

/**
 * Created by Biruk on 10/21/2017.
 */
public class SeekBarDraggedEvent {
    private int index;

    public SeekBarDraggedEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
