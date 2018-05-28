package io.starter.biruk.ezymusic.events;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/13/2017.
 */
public class SongLoadCompletedEvent {
    private List<Song> songList;

    public SongLoadCompletedEvent(List<Song> songList) {
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return songList;
    }
}
