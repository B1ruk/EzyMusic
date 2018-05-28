package io.starter.biruk.ezymusic.events;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/10/2017.
 */
public class SelectedAlbumEvent {
    List<Song> album;

    public SelectedAlbumEvent(List<Song> album) {
        this.album = album;
    }

    public List<Song> getAlbum() {
        return album;
    }
}
