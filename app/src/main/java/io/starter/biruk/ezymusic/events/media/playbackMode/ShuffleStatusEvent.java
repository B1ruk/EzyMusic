package io.starter.biruk.ezymusic.events.media.playbackMode;

import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;

/**
 * Created by biruk on 27/07/18.
 */

public class ShuffleStatusEvent {

    private Shuffle shuffle;

    public ShuffleStatusEvent(Shuffle shuffle) {
        this.shuffle = shuffle;
    }

    public Shuffle getShuffle() {
        return shuffle;
    }
}
