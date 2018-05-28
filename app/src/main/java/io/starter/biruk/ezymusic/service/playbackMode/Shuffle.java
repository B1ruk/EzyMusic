package io.starter.biruk.ezymusic.service.playbackMode;

/**
 * Created by Biruk on 10/31/2017.
 */
public enum  Shuffle {
    ON,
    OFF;

    public static Shuffle toggleMode(Shuffle shuffle){
        if (shuffle==ON){
            return OFF;
        }
        return ON;
    }

}
