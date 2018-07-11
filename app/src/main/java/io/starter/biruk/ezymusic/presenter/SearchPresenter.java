package io.starter.biruk.ezymusic.presenter;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.model.dao.searchDAO.SearchRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.view.searchView.SearchLibraryView;

/**
 * Created by biruk on 11/07/18.
 */

public class SearchPresenter {
    private SearchLibraryView searchLibraryView;
    private SearchRepository searchRepository;
    private Scheduler mainThread;

    public SearchPresenter(SearchLibraryView searchLibraryView, SearchRepository searchRepository, Scheduler mainThread) {
        this.searchLibraryView = searchLibraryView;
        this.searchRepository = searchRepository;
        this.mainThread = mainThread;
    }


    public void loadSearchResults(String query){
        searchRepository.searchLibrary(query)
                .subscribeOn(Schedulers.io())
                .observeOn(mainThread)
                .subscribeWith(new DisposableObserver<List<Song>>() {
                    @Override
                    public void onNext(@NonNull List<Song> songs) {
                        if (!songs.isEmpty()){
                            searchLibraryView.displaySearchResults(songs);
                        }else {
                            searchLibraryView.displayEmptySearchResult();
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
