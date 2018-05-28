package io.starter.biruk.ezymusic.model.dao.albumDAO;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface AlbumRepository {
    Single<List<List<Song>>> loadAlbums();
    Single<List<List<Song>>> loadAlbums(List<Song> songList);
}
