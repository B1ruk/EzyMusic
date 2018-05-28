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
                .map(new Function<Song, Long>() {
                    @Override
                    public Long apply(@NonNull Song song) throws Exception {
                        return song.albumId;
                    }
                })
                .distinct()
                .flatMap(new Function<Long, Observable<List<Song>>>() {
                    @Override
                    public Observable<List<Song>> apply(@NonNull final Long albumId) throws Exception {
                        return Observable.fromIterable(songList)
                                .filter(new Predicate<Song>() {
                                    @Override
                                    public boolean test(@NonNull Song song) throws Exception {
                                        return albumId == song.albumId;
                                    }
                                })
                                .toList()
                                .toObservable();
                    }
                })
                .toList();

    }

    @Override
    public Single<List<List<Song>>> loadAlbums(final List<Song> songList) {
        return Observable.fromIterable(songList)
                .map(new Function<Song, Long>() {
                    @Override
                    public Long apply(@NonNull Song song) throws Exception {
                        return song.albumId;
                    }
                })
                .distinct()
                .flatMap(new Function<Long, Observable<List<Song>>>() {
                    @Override
                    public Observable<List<Song>> apply(@NonNull final Long albumId) throws Exception {
                        return Observable.fromIterable(songList)
                                .filter(new Predicate<Song>() {
                                    @Override
                                    public boolean test(@NonNull Song song) throws Exception {
                                        return albumId == song.albumId;
                                    }
                                })
                                .toList()
                                .toObservable();
                    }
                })
                .toList();
    }
}
