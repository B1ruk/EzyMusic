package io.starter.biruk.ezymusic.view.miniView;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface MiniView {
    void updateUi(Song song);
    void launchNowPlaying();
    void play(int index, List<Song> songList);

    void displayPauseIcon();
    void displayPlayIcon();
}
