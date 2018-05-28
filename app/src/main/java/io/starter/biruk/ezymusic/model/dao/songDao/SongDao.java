package io.starter.biruk.ezymusic.model.dao.songDao;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.starter.biruk.ezymusic.model.dbHelper.DbHelper;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/7/2017.
 */
public class SongDao implements SongRepository {
    private static final String TAG = "songDAO";

    private Context appContext;

    protected Dao<Song, String> songDao;
    private DbHelper dbInstance;

    public SongDao(Context appContext) {
        this.appContext = appContext;

        dbInstance = DbHelper.getInstance(appContext);
        this.songDao = getSongDao();
    }

    private Dao<Song, String> getSongDao() {
        if (songDao == null) {
            try {
                songDao = dbInstance.getDao(Song.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return songDao;
    }

    @Override
    public Single<List<Song>> addSongs(final List<Song> songList) {
        return Single.fromCallable(new Callable<List<Song>>() {
            @Override
            public List<Song> call() throws Exception {
                addSongsToDB(songList);
                return songList;
            }
        });
    }

    @Override
    public Single<List<Song>> loadSongs() {
        return Single.fromCallable(new Callable<List<Song>>() {
            @Override
            public List<Song> call() throws Exception {
                return loadSongsFromDB();
            }
        });
    }


    @Override
    public Single<Song> updateSong(Song song) {
        return null;
    }

    @Override
    public boolean isSongExit(Song song) {
        return false;
    }

    @Override
    public Single<Song> removeSong(Song song) {
        return null;
    }

    protected List<Song> loadSongsFromDB() {
        List<Song> songList = new ArrayList<>();
        try {
            songList = songDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"size"+songList.size());
        return songList;
    }

    private void addSongsToDB(List<Song> songList) {
        for (Song song : songList) {
            try {
                songDao.createIfNotExists(song);
            } catch (SQLException e) {
                Log.d(TAG,"error adding song to db");
                e.printStackTrace();
            }
        }
    }

}
