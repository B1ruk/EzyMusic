package io.starter.biruk.ezymusic.model.dao.FavoriteDao;

import android.graphics.Bitmap;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by Biruk on 10/9/2017.
 */
public interface FavoriteRepository {
    Single<Song> updateFavoriteStatus(Song song);

    Single<Boolean> isFavorite(Song song);

    Single<List<Song>> loadFavorites();

    /*
    * loads only distinct favorite song album ids and then loads there respective bitmap artwork
    * */
    Single<List<Song>> loadDistinctFavoriteCoverArt();


    /*
    * loads only distinct favorite song album ids and then loads there respective
    * bitmap artwork by using the provided song list argument
    * */
    Single<List<Song>> loadDistinctFavoriteCoverArt(List<Song> songs);
}
