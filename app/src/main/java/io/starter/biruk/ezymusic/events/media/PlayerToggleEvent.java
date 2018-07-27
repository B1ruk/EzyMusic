package io.starter.biruk.ezymusic.events.media;

import io.starter.biruk.ezymusic.service.playbackMode.MediaTrigger;

/**
 * Created by biruk on 27/07/18.
 */

public class PlayerToggleEvent {
    private MediaTrigger mediaTrigger;

    public PlayerToggleEvent(MediaTrigger mediaTrigger) {
        this.mediaTrigger = mediaTrigger;
    }

    public MediaTrigger getMediaTrigger() {
        return mediaTrigger;
    }
}
