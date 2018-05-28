package io.starter.biruk.ezymusic.model.dao.albumDAO;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.starter.biruk.ezymusic.model.dao.songDao.SongDao;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public class AlbumDAO extends SongDao implements AlbumRepository {

    public AlbumDAO(Context appContext) {
        super(appContext);
    }

    @Override
    public Single<List<List<Song>>> loadAlbums() {

        final List<Song> songList = this.loadSongsFromDB();

        return Observable.fromIterable(songList)
                .map(song -> song.albumId)
                .distinct()
                .flatMap(albumId -> Observable.fromIterable(songList)
                        .filter(song -> albumId == song.albumId)
                        .toList()
                        .toObservable())
                .toList();

    }

    @Override
    public Single<List<List<Song>>> loadAlbums(final List<Song> songList) {
        return Observable.fromIterable(songList)
                .map(song -> song.albumId)
                .distinct()
                .flatMap(albumId -> Observable.fromIterable(songList)
                        .filter(song -> albumId == song.albumId)
                        .toList()
                        .toObservable())
                .toList();
    }
}
