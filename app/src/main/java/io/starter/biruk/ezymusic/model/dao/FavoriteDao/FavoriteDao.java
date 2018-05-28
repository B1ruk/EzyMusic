package io.starter.biruk.ezymusic.model.dao.FavoriteDao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.starter.biruk.ezymusic.model.dao.songDao.SongDao;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.SongFormatUtil;

/**
 * Created by Biruk on 10/9/2017.
 */
public class FavoriteDao extends SongDao implements FavoriteRepository {
    private static final String TAG = "favoriteDAO";
    private SongFormatUtil songFormatUtil;

    private Context apContext;

    public FavoriteDao(Context appContext) {
        super(appContext);
        this.apContext = appContext;
        this.songFormatUtil = new SongFormatUtil(appContext);
    }

    @Override
    public Single<Song> updateFavoriteStatus(final Song song) {
        return isFavorite(song)
                .map(favorite -> {
                    Log.i(TAG, "\t" + song.title);
                    if (favorite) {
                        //remove from favorite
                        song.favorite = false;
                        Log.i(TAG, "\t" + song.title + "\t" + false);
                        songDao.update(song);
                    } else if (!favorite) {
                        //add to favorite
                        song.favorite = true;
                        Log.i(TAG, "\t" + song.title + "\t" + true);
                        songDao.update(song);
                    }
                    return song;
                });
    }

    @Override
    public Single<Boolean> isFavorite(final Song song) {
        return Single.fromCallable(() -> songDao.queryForId(song.data))
                .map(song1 -> {
                    Log.i(TAG, "\t" + song1.title);
                    return song1.favorite;
                });
    }

    @Override
    public Single<List<Song>> loadFavorites() {
        return Observable.fromIterable(this.loadSongsFromDB())
                .filter(song -> song.favorite)
                .toList();
    }

    @Override
    public Single<List<Song>> loadDistinctFavoriteCoverArt() {
        return Observable.fromIterable(this.loadSongsFromDB())
                .filter(song -> song.favorite)
                .distinct(song -> song.albumId)
                .filter(song -> {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(apContext.getContentResolver(), songFormatUtil.getAlbumArtWorkUri(song.albumId));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap == null) {

                        return false;
                    }
                    return true;
                })
                .toList();
    }

    @Override
    public Single<List<Song>> loadDistinctFavoriteCoverArt(List<Song> songs) {
        return Observable.fromIterable(this.loadSongsFromDB())
                .filter(song -> song.favorite)
                .distinct(song -> song.albumId)
                .filter(song -> {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(apContext.getContentResolver(), songFormatUtil.getAlbumArtWorkUri(song.albumId));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap == null) {

                        return false;
                    }
                    return true;
                })
                .toList();
    }

}
