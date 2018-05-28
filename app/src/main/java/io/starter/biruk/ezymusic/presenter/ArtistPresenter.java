package io.starter.biruk.ezymusic.presenter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.ArtistSelectedEvent;
import io.starter.biruk.ezymusic.events.adapterPosition.ArtistAdapterPositionEvent;
import io.starter.biruk.ezymusic.model.dao.artistsDAO.ArtistRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.view.artistsView.ArtistView;

/**
 * Created by Biruk on 10/9/2017.
 */
public class ArtistPresenter {

    private ArtistRepository artistRepository;
    private ArtistView artistView;
    private Scheduler schedulerIo;
    private Scheduler mainThread;

    private CompositeDisposable compositeDisposable;

    public ArtistPresenter(ArtistRepository artistRepository, ArtistView artistView,
                           Scheduler schedulerIo, Scheduler mainThread) {
        this.artistRepository = artistRepository;
        this.artistView = artistView;
        this.schedulerIo = schedulerIo;
        this.mainThread = mainThread;

        this.compositeDisposable=new CompositeDisposable();
    }

    public void loadArtists(){
        compositeDisposable.add(artistRepository.loadArtists()
                .subscribeOn(schedulerIo)
                .observeOn(mainThread)
                .subscribeWith(new DisposableSingleObserver<List<List<Song>>>() {
                    @Override
                    public void onSuccess(@NonNull List<List<Song>> artists) {
                        if (!artists.isEmpty()){
                            sortList(artists);
                            artistView.displayArtists(artists);
                        }else {
                            artistView.emptyArtistList();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                })
        );
    }


    public void artistSelectListener(){
        compositeDisposable.add(
                RxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof ArtistSelectedEvent){
                        artistView.displaySelectedArtistView();
                    }
                })
        );
    }

    private void sortList(List<List<Song>> artists) {
        Collections.sort(artists, (lhs, rhs) -> {
            String t1=lhs.get(0).artist;
            String t2=rhs.get(0).artist;
            return t1.compareTo(t2);
        });
    }

    public void scrollListener() {
        compositeDisposable.add(
                ReplayEventBus.getInstance().subscribe(o -> {
                    if (o instanceof ArtistAdapterPositionEvent){
                        int index = ((ArtistAdapterPositionEvent) o).getIndex();
                        artistView.scrollTo(index);
                    }
                })
        );
    }

    public void cleanUp(){
        compositeDisposable.clear();
    }

}
