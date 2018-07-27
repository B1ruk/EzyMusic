package io.starter.biruk.ezymusic.events.media;

import java.util.List;

import io.starter.biruk.ezymusic.events.media.playbackMode.QueueEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;

/**
 * Created by biruk on 27/07/18.
 */

public class MediaStatusEvent {
    private boolean isPlaying;
    private Shuffle shuffleState;
    private Repeat repeatState;
    private QueueEvent queueEvent;

    public MediaStatusEvent(boolean isPlaying, Shuffle shuffleState, Repeat repeatState, QueueEvent queueEvent) {
        this.isPlaying = isPlaying;
        this.shuffleState = shuffleState;
        this.repeatState = repeatState;
        this.queueEvent = queueEvent;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Shuffle getShuffleState() {
        return shuffleState;
    }

    public Repeat getRepeatState() {
        return repeatState;
    }

    public QueueEvent getQueueEvent() {
        return queueEvent;
    }
}
