package io.starter.biruk.ezymusic.events.view;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/26/2017.
 */
public class CurrentSongEvent {
    private Song song;

    public CurrentSongEvent(Song song) {
        this.song = song;
    }

    public Song getSong() {
        return song;
    }
}
