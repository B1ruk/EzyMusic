package io.starter.biruk.ezymusic.presenter;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.AlbumSelectedEvent;
import io.starter.biruk.ezymusic.events.SelectedAlbumEvent;
import io.starter.biruk.ezymusic.events.SelectedArtistEvent;
import io.starter.biruk.ezymusic.events.adapterPosition.AlbumAdapterPositionEvent;
import io.starter.biruk.ezymusic.model.dao.albumDAO.AlbumRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.view.albumsView.AlbumView;

/**
 * Created by Biruk on 10/9/2017.
 */
public class AlbumPresenter {
    private static final String TAG = "AlbumPresenter";

    private AlbumRepository albumRepository;
    private AlbumView albumView;
    private Scheduler schedulerIO;
    private Scheduler mainThread;

    private List<Song> artistSongs;

    private CompositeDisposable compositeDisposable;

    public AlbumPresenter(AlbumView albumView, AlbumRepository albumRepository, Scheduler schedulerIO,
                          Scheduler mainThread) {
        this.albumRepository = albumRepository;
        this.albumView = albumView;
        this.schedulerIO = schedulerIO;
        this.mainThread = mainThread;

        compositeDisposable = new CompositeDisposable();
    }


    public void init() {
        ReplayEventBus.getInstance().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof SelectedArtistEvent) {
                    List<Song> songList = ((SelectedArtistEvent) o).getArtistSongs();
                    setArtistSongs(songList);
                }
            }
        });
    }


    public void loadArtistAlbums() {
        compositeDisposable.add(albumRepository.loadAlbums(this.artistSongs)
                        .subscribeOn(schedulerIO)
                        .observeOn(mainThread)
                        .subscribeWith(new DisposableSingleObserver<List<List<Song>>>() {
                            @Override
                            public void onSuccess(@NonNull List<List<Song>> albums) {
                                sortAlbum(albums);
                                albumView.displayAlbums(albums);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }
                        })
        );
    }


    public void loadAlbums() {
        compositeDisposable.add(albumRepository.loadAlbums()
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribeWith(new DisposableSingleObserver<List<List<Song>>>() {
                    @Override
                    public void onSuccess(@NonNull List<List<Song>> albumList) {
                        if (!albumList.isEmpty()) {
                            sortAlbum(albumList);
                            albumView.displayAlbums(albumList);
                        } else {
                            albumView.emptyAlbumList();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                })
        );
    }

    private void sortAlbum(List<List<Song>> albumList) {
        Collections.sort(albumList, new Comparator<List<Song>>() {
            @Override
            public int compare(List<Song> lhs, List<Song> rhs) {
                String album1 = lhs.get(0).albumTitle;
                String album2 = rhs.get(0).albumTitle;
                return album1.compareTo(album2);
            }
        });
    }

    public void onAlbumSelected() {
        compositeDisposable.add(RxEventBus.getInstance().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof AlbumSelectedEvent) {
                    albumView.launchSelectedAlbumView();
                    Log.i(TAG, "albumSelected");
                }
            }
        }));
    }

    public void cleanUp() {
        compositeDisposable.clear();
    }

    public void scrollToPosition() {
        compositeDisposable.add(ReplayEventBus.getInstance().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof AlbumAdapterPositionEvent) {
                    int index = ((AlbumAdapterPositionEvent) o).getIndex();
                    albumView.scrollTo(index);
                }
            }
        }));
    }

    private void setArtistSongs(List<Song> artistSongs) {
        this.artistSongs = artistSongs;
    }
}
