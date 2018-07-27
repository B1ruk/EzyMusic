package io.starter.biruk.ezymusic.events.media.playbackMode;

import io.starter.biruk.ezymusic.service.playbackMode.Repeat;

/**
 * Created by biruk on 27/07/18.
 */

public class RepeatStatusEvent {
    private Repeat repeat;

    public RepeatStatusEvent(Repeat repeat) {
        this.repeat = repeat;
    }

    public Repeat getRepeat() {
        return repeat;
    }
}
