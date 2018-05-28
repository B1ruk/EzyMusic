package io.starter.biruk.ezymusic.model.dao.searchDAO;

import java.util.List;

import io.reactivex.Observable;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/13/2017.
 */
public interface SearchRepository {

    Observable<List<Song>> searchResults(String query);
}
