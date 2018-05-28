package io.starter.biruk.ezymusic.events.adapterPosition;

/**
 * Created by Biruk on 10/10/2017.
 */
public class ArtistAdapterPositionEvent {
    private int index;

    public ArtistAdapterPositionEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
