package io.starter.biruk.ezymusic.view.albumsView;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface AlbumView {
    void displayAlbums(List<List<Song>> albumList);
    void emptyAlbumList();

    void launchSelectedAlbumView();

    void scrollTo(int index);
}
