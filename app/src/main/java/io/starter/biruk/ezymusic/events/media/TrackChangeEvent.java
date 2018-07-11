package io.starter.biruk.ezymusic.events.media;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by biruk on 11/07/18.
 */

public class TrackChangeEvent {
    private int index;
    private List<Song> songs;

    public TrackChangeEvent(int index, List<Song> songs) {
        this.index = index;
        this.songs = songs;
    }

    public int getIndex() {
        return index;
    }

    public List<Song> getSongs() {
        return songs;
    }
}
