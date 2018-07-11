package io.starter.biruk.ezymusic.view.searchView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.FastScroller;

public class SearchActivity extends AppCompatActivity implements SearchView{

    private View searchSongsView;
    private View searchInfoView;

    private RecyclerView searchRecyclerView;
    private FastScroller searchFastScroller;

    private TextView searchInfoText;
    private ImageView searchInfoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
    }

    private void initViews() {
        searchSongsView=findViewById(R.id.search_reults);
        searchInfoView=findViewById(R.id.search_info);
        searchInfoText= (TextView) findViewById(R.id.search_info_text);
        searchFastScroller= (FastScroller) findViewById(R.id.search_fast_Scroller);
        searchRecyclerView= (RecyclerView) findViewById(R.id.song_search_list_recycler);
        searchInfoImage= (ImageView) findViewById(R.id.search_info_img);
    }

    @Override
    public void displaySearchResults(List<Song> songs) {

    }

    @Override
    public void displayEmptySearchResult() {

    }

    @Override
    public void hideSearchInfo() {

    }

    @Override
    public void hideSearchResult() {

    }
}
