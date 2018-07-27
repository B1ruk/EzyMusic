package io.starter.biruk.ezymusic;

/**
 * Created by biruk on 27/07/18.
 */

public class SeekBarDraggedEvent {
    private int progress;

    public SeekBarDraggedEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }



}
