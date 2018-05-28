package io.starter.biruk.ezymusic.model.dao.playlistDao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import io.starter.biruk.ezymusic.model.dbHelper.DbHelper;
import io.starter.biruk.ezymusic.model.entity.Playlist;
import io.starter.biruk.ezymusic.model.entity.PlaylistSong;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/29/2017.
 */
public class PlaylistDao {

//    private Dao<Song,String> songDao;
    private Dao<Playlist,String > playlistDao;
    private Dao<PlaylistSong,Integer> playlistSongDao;

    /*
    * for getting the DB instance
    * */
    private DbHelper dbHelper;

    private Context appContext;

    public PlaylistDao(Context appContext) {
        this.appContext = appContext;

        this.dbHelper=DbHelper.getInstance(appContext);
        init();
    }

    private void init(){
        getPlaylistDao();
        getPlaylistSongDao();
    }

    /*
    * returns the playlistDao object
    * */
    private Dao<Playlist, String> getPlaylistDao() {
        if (playlistDao==null){
            try {
                this.playlistDao=dbHelper.getDao(Playlist.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return playlistDao;
    }

    /*
    * returns the songDao object
    * */
    private Dao<PlaylistSong,Integer> getPlaylistSongDao(){
        if (playlistSongDao==null){
            try {
                this.playlistSongDao=dbHelper.getDao(PlaylistSong.class);
            } catch (SQLException e) {

            }
        }
        return playlistSongDao;
    }
}
