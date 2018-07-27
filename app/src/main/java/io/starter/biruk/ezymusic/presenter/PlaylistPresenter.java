package io.starter.biruk.ezymusic.presenter;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.events.view.SelectedArtistEvent;
import io.starter.biruk.ezymusic.model.dao.FavoriteDao.FavoriteRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.view.playlistView.PlaylistView;

/**
 * Created by Biruk on 10/9/2017.
 */
public class PlaylistPresenter {

    private PlaylistView playlistView;
    private FavoriteRepository favoriteRepository;

    private Scheduler schedulerIO;
    private Scheduler mainThread;

    private List<Song> favoriteSongs;

    public PlaylistPresenter(PlaylistView playlistView, FavoriteRepository favoriteRepository,
                             Scheduler schedulerIO, Scheduler mainThread) {
        this.playlistView = playlistView;
        this.favoriteRepository = favoriteRepository;
        this.schedulerIO = schedulerIO;
        this.mainThread = mainThread;
    }

    public void loadFavorites() {
        favoriteRepository.loadFavorites()
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribeWith(new DisposableSingleObserver<List<Song>>() {
                    @Override
                    public void onSuccess(@NonNull List<Song> songList) {
                        if (!songList.isEmpty()) {
                            setFavoriteSongs(songList);
                            playlistView.displayFavoritesInfo(songList);
                        } else {
                            playlistView.displayEmptyFavoritesView();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    public void loadFavoritesImage() {
        favoriteRepository.loadDistinctFavoriteCoverArt(this.favoriteSongs)
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribeWith(new DisposableSingleObserver<List<Song>>() {
                    @Override
                    public void onSuccess(@NonNull List<Song> songs) {
                        playlistView.displayFavoriteArtWorks(songs);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        playlistView.displayEmptyFavoritesView();
                    }
                });
    }


    public void setFavoriteSongs(List<Song> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    public void launchFavoriteView() {
        ReplayEventBus.getInstance().post(new SelectedArtistEvent(favoriteSongs));
        playlistView.launchFavoriteView();
    }
}
