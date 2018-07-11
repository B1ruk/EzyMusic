package io.starter.biruk.ezymusic.presenter;

import io.reactivex.Scheduler;
import io.starter.biruk.ezymusic.model.dao.searchDAO.SearchRepository;
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


}
