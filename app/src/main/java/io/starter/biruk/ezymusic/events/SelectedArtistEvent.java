package io.starter.biruk.ezymusic.events;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/10/2017.
 */
public class SelectedArtistEvent {
    private List<Song> artistSongs;

    public SelectedArtistEvent(List<Song> artistSongs) {
        this.artistSongs = artistSongs;
    }

    public List<Song> getArtistSongs() {
        return artistSongs;
    }
}
