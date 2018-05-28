package io.starter.biruk.ezymusic.events;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public class SelectedSongQueueEvent {
    private int index;
    private List<Song> songList;

    public SelectedSongQueueEvent(int index, List<Song> songList) {
        this.index = index;
        this.songList = songList;
    }

    public int getIndex() {
        return index;
    }

    public List<Song> getSongList() {
        return songList;
    }
}
