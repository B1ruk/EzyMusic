package io.starter.biruk.ezymusic.model.entity;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Biruk on 10/29/2017.
 */
public class PlaylistSong {
    public final static String SONG_ID_FIELD_NAME = "song_id";
    public final static String PLAYLIST_ID_FILED_NAME = "playlist_id";

    @DatabaseField(generatedId = true)
    int id;

    //    This is a foreign object which just stores the id from the song object in this table
    @DatabaseField(foreign = true, columnName = SONG_ID_FIELD_NAME)
    Song song;

    //    This is a foreign object which just stores the id from the playlist object in this table
    @DatabaseField(foreign = true, columnName = PLAYLIST_ID_FILED_NAME)
    Playlist playlist;

    PlaylistSong(){
        //for ormlite
    }

    public PlaylistSong(Song song, Playlist playlist) {
        this.song = song;
        this.playlist = playlist;
    }
}
