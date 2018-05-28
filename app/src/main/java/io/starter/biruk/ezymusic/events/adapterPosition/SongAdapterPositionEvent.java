package io.starter.biruk.ezymusic.events.adapterPosition;

/**
 * Created by Biruk on 10/10/2017.
 */
public class SongAdapterPositionEvent {
    private int index;

    public SongAdapterPositionEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
