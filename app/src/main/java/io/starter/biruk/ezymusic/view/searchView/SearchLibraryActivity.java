package io.starter.biruk.ezymusic.view.searchView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.FastScroller;

public class SearchLibraryActivity extends AppCompatActivity implements SearchLibraryView {

    private static final String TAG="SearchLibraryActivity";

    private View searchSongsView;
    private View searchInfoView;

    private RecyclerView searchRecyclerView;
    private FastScroller searchFastScroller;

    private Toolbar searchToolbar;

    private TextView searchInfoText;
    private ImageView searchInfoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setUpToolbar();
        hideSearchResult();
    }


    private void initViews() {
        searchSongsView = findViewById(R.id.search_reults);
        searchInfoView = findViewById(R.id.search_info);
        searchInfoText = (TextView) findViewById(R.id.search_info_text);
        searchFastScroller = (FastScroller) findViewById(R.id.search_fast_Scroller);
        searchRecyclerView = (RecyclerView) findViewById(R.id.song_search_list_recycler);
        searchInfoImage = (ImageView) findViewById(R.id.search_info_img);
        searchToolbar= (Toolbar) findViewById(R.id.main_search_toolbar);
    }


    private void setUpToolbar() {
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchBar = menu.findItem(R.id.song_library_search);
        SearchView searchView = (SearchView) searchBar.getActionView();

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.d(TAG,query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void displaySearchResults(List<Song> songs) {

    }

    @Override
    public void displayEmptySearchResult() {

    }

    @Override
    public void hideSearchInfo() {
        searchInfoText.setVisibility(View.GONE);
        searchSongsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchResult() {
        searchSongsView.setVisibility(View.GONE);
        searchInfoText.setVisibility(View.VISIBLE);
    }
}
