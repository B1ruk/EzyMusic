package io.starter.biruk.ezymusic.events.media.playbackMode;

import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;

/**
 * Created by Biruk on 10/31/2017.
 */
public class ShufflePostEvent {
    private Shuffle shuffle;

    public ShufflePostEvent(Shuffle shuffle) {
        this.shuffle = shuffle;
    }

    public Shuffle getShuffleMode() {
        return shuffle;
    }
}

