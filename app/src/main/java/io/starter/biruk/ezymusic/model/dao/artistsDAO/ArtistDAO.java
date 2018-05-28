package io.starter.biruk.ezymusic.model.dao.artistsDAO;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.starter.biruk.ezymusic.model.dao.songDao.SongDao;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public class ArtistDAO extends SongDao implements ArtistRepository {

    public ArtistDAO(Context appContext) {
        super(appContext);
    }

    @Override
    public Single<List<List<Song>>> loadArtists() {
        final List<Song> songList = loadSongsFromDB();

        return Observable.fromIterable(songList)
                .map(new Function<Song, String>() {
                    @Override
                    public String apply(@NonNull Song song) throws Exception {
                        return song.artist;
                    }
                })
                .distinct()
                .flatMap(new Function<String, Observable<List<Song>>>() {
                    @Override
                    public Observable<List<Song>> apply(@NonNull final String artist) throws Exception {
                        return Observable.fromIterable(songList)
                                .filter(new Predicate<Song>() {
                                    @Override
                                    public boolean test(@NonNull Song song) throws Exception {
                                        return artist.equals(song.artist);
                                    }
                                })
                                .toList()
                                .toObservable();
                    }
                }).toList();
    }
}
