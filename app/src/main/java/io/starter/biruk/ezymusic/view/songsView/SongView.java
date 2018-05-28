package io.starter.biruk.ezymusic.view.songsView;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/7/2017.
 */
public interface SongView {
    void displaySongs(List<Song> songs);
    void emptySongsNotFound();
    void displaySongLoadSuccessMsg(List<Song> songList);

    void onSongSelected(Song song);
    void scrollTo(int index);

}
