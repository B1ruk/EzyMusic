package io.starter.biruk.ezymusic.events.adapterPosition;

/**
 * Created by Biruk on 10/10/2017.
 */
public class AlbumAdapterPositionEvent {
    private int index;

    public AlbumAdapterPositionEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
