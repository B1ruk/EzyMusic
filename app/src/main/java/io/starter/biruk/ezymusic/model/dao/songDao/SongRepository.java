package io.starter.biruk.ezymusic.model.dao.songDao;

import java.util.List;

import io.reactivex.Single;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/7/2017.
 */
public interface SongRepository {
    Single<List<Song>> addSongs(List<Song> songList);

    Single<List<Song>> loadSongs();

    Single<Song> updateSong(Song song);

    boolean isSongExit(Song song);

    Single<Song> removeSong(Song song);
}
