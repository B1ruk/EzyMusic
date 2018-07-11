package io.starter.biruk.ezymusic.model.dao.searchDAO;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.events.SongLoadCompletedEvent;
import io.starter.biruk.ezymusic.model.dao.songDao.SongDao;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/13/2017.
 */
public class SearchDAO extends SongDao implements SearchRepository {

    private List<Song> songs;

    public SearchDAO(Context appContext) {
        super(appContext);

        init();
    }

    private void init() {
        ReplayEventBus.getInstance().subscribe(o -> {
            if (o instanceof SongLoadCompletedEvent) {
                List<Song> songList = ((SongLoadCompletedEvent) o).getSongList();
                setSongs(songList);
            }
        });
    }

    @Override
    public Observable<List<Song>> searchResults(final String query) {

        String searchQuery = query.toLowerCase();

        return Observable.fromIterable(this.songs)
                .filter(song -> searchSong(searchQuery, song))
                .toList()
                .toObservable();

    }

    private boolean searchSong(String query, Song song) {
        boolean titleMatch = song.title.toLowerCase().contains(query);
        boolean artistMath = song.artist.toLowerCase().contains(query);
        boolean albumMatch = song.albumTitle.toLowerCase().contains(query);

        return titleMatch || artistMath || albumMatch;
    }

    private void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
