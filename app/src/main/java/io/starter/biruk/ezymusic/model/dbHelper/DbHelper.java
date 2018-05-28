package io.starter.biruk.ezymusic.model.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import io.starter.biruk.ezymusic.model.entity.Playlist;
import io.starter.biruk.ezymusic.model.entity.PlaylistSong;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/7/2017.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "ezy.db";
    private static final int DB_VERSION = 1;

    private static DbHelper dbHelper;

    private DbHelper(Context appContext) {
        super(appContext, DB_NAME, null, DB_VERSION);
    }

    public static DbHelper getInstance(Context appContext) {
        if (dbHelper == null) {
            synchronized (DbHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DbHelper(appContext);
                }
            }
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Song.class);
            TableUtils.createTable(connectionSource, Playlist.class);
            TableUtils.createTable(connectionSource, PlaylistSong.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
