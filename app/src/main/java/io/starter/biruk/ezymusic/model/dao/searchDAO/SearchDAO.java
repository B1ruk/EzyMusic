package io.starter.biruk.ezymusic.model.dao.searchDAO;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.events.SongLoadCompletedEvent;
import io.starter.biruk.ezymusic.model.dao.songDao.SongDao;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/13/2017.
 */
public class SearchDAO implements SearchRepository {

    private List<Song> songs;

    public SearchDAO() {
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
    public Observable<List<Song>> searchLibrary(String query) {
        return Observable.just(query)
                .filter(q -> q.length() > 2)
                .map(String::toLowerCase)
                .flatMap(searchQuery -> searchResults(query));
    }

    private Observable<List<Song>> searchResults(final String query) {

        return Observable.fromIterable(songs)
                .filter(song -> searchSong(query, song))
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
