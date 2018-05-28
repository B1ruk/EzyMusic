package io.starter.biruk.ezymusic.presenter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.events.SelectedArtistEvent;
import io.starter.biruk.ezymusic.events.adapterPosition.SongAdapterPositionEvent;
import io.starter.biruk.ezymusic.model.dao.songDao.SongRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.view.songsView.SongView;

/**
 * Created by Biruk on 10/7/2017.
 */
public class SongPresenter {
    private SongRepository songsRepository;
    private SongView songView;
    private Scheduler schedulerIO;
    private Scheduler mainThread;

    private List<Song> songs;

    private CompositeDisposable compositeDisposable;

    public SongPresenter(SongRepository songsRepository, SongView songView, Scheduler schedulerIO, Scheduler mainThread) {
        this.songsRepository = songsRepository;
        this.songView = songView;
        this.schedulerIO = schedulerIO;
        this.mainThread = mainThread;

        compositeDisposable=new CompositeDisposable();
    }


    public void init() {
        ReplayEventBus.getInstance().subscribe(o -> {
            if (o instanceof SelectedArtistEvent) {
                List<Song> songList = ((SelectedArtistEvent) o).getArtistSongs();
                setSongs(songList);
            }
        });
    }

    public void loadSongs() {
        compositeDisposable.add(songsRepository.loadSongs()
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribeWith(new DisposableSingleObserver<List<Song>>() {
                    @Override
                    public void onSuccess(@NonNull List<Song> songList) {
                        if (!songList.isEmpty()) {
                            sortSongList(songList);
                            songView.displaySongLoadSuccessMsg(songList);
                            songView.displaySongs(songList);
                        } else {
                            songView.emptySongsNotFound();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        songView.emptySongsNotFound();
                    }
                })
        );
    }

    public void loadArtistSongs() {
        songView.displaySongs(this.songs);
    }

    public void scrollListener(){
        compositeDisposable.add(ReplayEventBus.getInstance().subscribe(o -> {
            if (o instanceof SongAdapterPositionEvent){
                int index = ((SongAdapterPositionEvent) o).getIndex();
                songView.scrollTo(index);
            }
        }));
    }

    private void sortSongList(List<Song> songList) {
        Collections.sort(songList, (lhs, rhs) -> {
            String s1=lhs.title;
            String s2=rhs.title;
            return s1.compareTo(s2);
        });
    }

    private void setSongs(List<Song> songs){
        this.songs=songs;
    }


    public void cleanup(){
        compositeDisposable.clear();
    }

}
