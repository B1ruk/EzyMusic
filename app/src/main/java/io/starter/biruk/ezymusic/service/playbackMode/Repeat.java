package io.starter.biruk.ezymusic.service.playbackMode;

/**
 * Created by Biruk on 10/31/2017.
 */
public enum Repeat {
    NONE,
    ALL,
    ONE;

    public static Repeat toggleMode(Repeat repeatMode) {
        switch (repeatMode) {
            case NONE:
                return ONE;
            case ONE:
                return ALL;
            case ALL:
                return NONE;
            default:
                return NONE;
        }
    }

}
