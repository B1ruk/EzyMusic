package io.starter.biruk.ezymusic.util;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

/**
 * Created by Biruk on 10/7/2017.
 */
public class SongFormatUtil {
    private Context appContext;

    private final Uri ALBUM_URI = Uri.parse("content://media/external/audio/albumart");

    public SongFormatUtil(Context appContext) {
        this.appContext = appContext;
    }

    public Uri getAlbumArtWorkUri(long albumId) {
        return ContentUris.withAppendedId(ALBUM_URI, albumId);
    }

    public String formatString(String text, int length) {
        if (text.length() > length)
            return text.substring(0, length).concat("...");
        else
            return text;
    }

    public String formatSongDuration(long duration) {
        long timeSec = duration / 1000;
        long min = timeSec / 60;
        long sec = timeSec % 60;

        return String.format("%2d:%02d", min, sec);
    }

    public String songCount(int count) {
        return count == 1 ? "1 song" : count + " songs";
    }


    public String albumCount(int count) {
        return count == 1 ? "1 album" : count + " albums";
    }


}
