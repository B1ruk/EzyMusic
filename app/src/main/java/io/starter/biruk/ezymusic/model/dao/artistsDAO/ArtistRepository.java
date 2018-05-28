package io.starter.biruk.ezymusic.model.dao.artistsDAO;

import java.util.List;

import io.reactivex.Single;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface ArtistRepository {
    Single<List<List<Song>>> loadArtists();
}
