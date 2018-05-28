package io.starter.biruk.ezymusic.model.songFetcher;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.LinkedList;
import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Playlist;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/7/2017.
 */
public class SongStorageUtil {
    private Context appContext;

    public SongStorageUtil(Context appContext) {
        this.appContext = appContext;
    }


    public List<Song> fetchFromCard() {
        List<Song> songs =new LinkedList<>();
        ContentResolver musicResolver=appContext.getContentResolver();
        Uri musicUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor=musicResolver.query(musicUri,null,null,null,null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            int idColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int album_Id=musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int dataColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int artistColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int titleColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int durationColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int trackNumberColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            do{
                long id=musicCursor.getLong(titleColumn);
                long albumId=musicCursor.getLong(album_Id);
                String data=musicCursor.getString(dataColumn);
                String artist=musicCursor.getString(artistColumn);
                String albumTitle=musicCursor.getString(albumColumn);
                String title=musicCursor.getString(titleColumn);
                long duration=musicCursor.getLong(durationColumn);
                int trackNumber=musicCursor.getInt(trackNumberColumn);

                Song song=new Song();

                song.songId=id;
                song.albumId=albumId;
                song.artist=artist;
                song.title=title;
                song.albumTitle=albumTitle;
                song.duration=duration;
                song.trackNumber=trackNumber;
                song.favorite=false;
                song.data=data;

                songs.add(song);

            }while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return songs;
    }

}
