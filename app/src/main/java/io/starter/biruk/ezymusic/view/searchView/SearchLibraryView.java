package io.starter.biruk.ezymusic.view.searchView;

import java.util.List;

import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * Created by biruk on 11/07/18.
 */

public interface SearchLibraryView {
    void displaySearchResults(List<Song> songs);

    void displayEmptySearchResult();

    void hideSearchInfo();

    void hideSearchResult();
}
