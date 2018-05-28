package io.starter.biruk.ezymusic.presenter;

import android.util.Log;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.SongLoadCompletedEvent;
import io.starter.biruk.ezymusic.model.dao.searchDAO.SearchRepository;
import io.starter.biruk.ezymusic.model.dao.songDao.SongRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.model.songFetcher.SongStorageUtil;
import io.starter.biruk.ezymusic.view.mainView.MainView;

/**
 * Created by Biruk on 10/7/2017.
 */
public class MainPresenter {
    private SongStorageUtil songStorageUtil;
    private MainView mainView;
    private SongRepository songsRepository;
    private SearchRepository searchRepository;
    private Scheduler schedulerIO;
    private Scheduler mainThread;


    public MainPresenter(SongStorageUtil songStorageUtil,MainView mainView, SongRepository songsRepository,
                         SearchRepository searchRepository,
                         Scheduler schedulers, Scheduler mainThread) {
        this.songStorageUtil = songStorageUtil;
        this.mainView = mainView;
        this.songsRepository = songsRepository;
        this.searchRepository = searchRepository;
        this.schedulerIO = schedulers;
        this.mainThread = mainThread;
    }

    public void fetchSongs() {

        List<Song> songs =songStorageUtil.fetchFromCard();
        songsRepository.addSongs(songs)
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribe(new DisposableSingleObserver<List<Song>>() {
                    @Override
                    public void onSuccess(@NonNull List<Song> songList) {
                        ReplayEventBus.getInstance().post(new SongLoadCompletedEvent(songList));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mainView.displaySongError();
                    }
                });
    }

    public void searchSong(String query){
        searchRepository.searchResults(query)
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribeWith(new DisposableObserver<List<Song>>() {
                    @Override
                    public void onNext(@NonNull List<Song> songs) {
                        for (Song song:songs){
                            Log.i("\t",song.title);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
