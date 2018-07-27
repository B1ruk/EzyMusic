package io.starter.biruk.ezymusic.view.nowplayingView;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface NowPlayingView {
    void songIsFavoriteView();

    void songIsNotFavoriteView();

    void updateView(Song song);

    void updateTopView(Song song);

    void updatePlayPause(boolean isPlaynig);

    void playPauseToggle();

    void nextSong();

    void previousSong();

    void updateSeekBar(long duration);

    void updateBackground(Song song);

    void updateShuffleView(Shuffle shuffleMode);

    void updateRepeatView(Repeat repeatMode);

}
