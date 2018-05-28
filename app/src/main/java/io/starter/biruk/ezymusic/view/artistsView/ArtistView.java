package io.starter.biruk.ezymusic.view.artistsView;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface ArtistView {
    void displayArtists(List<List<Song>> artists);
    void emptyArtistList();
    void scrollTo(int position);
    void displaySelectedArtistView();
}
