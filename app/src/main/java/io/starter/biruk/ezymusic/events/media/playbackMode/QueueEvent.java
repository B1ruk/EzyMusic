package io.starter.biruk.ezymusic.events.media.playbackMode;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by biruk on 27/07/18.
 */

public class QueueEvent {
    private int index;
    private List<Song> songs;

    public QueueEvent(int index, List<Song> songs) {
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
