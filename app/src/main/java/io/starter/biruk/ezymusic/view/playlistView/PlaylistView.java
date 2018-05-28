package io.starter.biruk.ezymusic.view.playlistView;

import android.graphics.Bitmap;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface PlaylistView {
    void displayFavoritesInfo(List<Song> songs);

    void updateFavorites();

    void displayEmptyFavoritesView();

    void displayFavoriteArtWorks(List<Song> artworks);

    void launchFavoriteView();

}
