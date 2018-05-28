package io.starter.biruk.ezymusic.events.media.playbackMode;

import io.starter.biruk.ezymusic.service.playbackMode.Repeat;

/**
 * Created by Biruk on 10/31/2017.
 */
public class RepeatPostEvent {
    private Repeat repeat;

    public RepeatPostEvent(Repeat repeat) {
        this.repeat = repeat;
    }

    public Repeat getRepeatMode() {
        return repeat;
    }
}
