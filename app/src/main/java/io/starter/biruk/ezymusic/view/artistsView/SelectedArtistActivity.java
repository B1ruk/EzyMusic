package io.starter.biruk.ezymusic.view.artistsView;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewTypeConstant;
import io.starter.biruk.ezymusic.view.albumsView.AlbumFragment;
import io.starter.biruk.ezymusic.view.artistsView.BioView.ArtistBioFragment;
import io.starter.biruk.ezymusic.view.artistsView.adapter.ArtistViewPagerAdapter;
import io.starter.biruk.ezymusic.view.songsView.SongsFragment;
import io.starter.biruk.ezymusic.view.songsView.adapter.SongListAdapter;

public class SelectedArtistActivity extends AppCompatActivity implements SelectedArtistView {

    private Toolbar selectedArtistToolbar;
    private ImageView selectedArtistCoverArt;

    private TabLayout artistTabLayout;
    private ViewPager artistViewPager;

    private SongFormatUtil songFormatUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_artist);

        selectedArtistToolbar = (Toolbar) findViewById(R.id.selected_artist_toolbar);
        selectedArtistCoverArt = (ImageView) findViewById(R.id.selected_artist_cover);

        artistViewPager = (ViewPager) findViewById(R.id.selected_artist_viewpager);
        artistTabLayout = (TabLayout) findViewById(R.id.selected_artist_tab_layout);

        setSupportActionBar(selectedArtistToolbar);

        songFormatUtil = new SongFormatUtil(this);
  }
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        initToolBar();

        initView();
    }

    private void initToolBar() {
        getSupportActionBar().setTitle("EZY_!!");

        Picasso.with(this)
                .load(R.drawable.art_1)
                .fit()
                .into(selectedArtistCoverArt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initView() {
        Fragment[] fragments = {
                AlbumFragment.getInstance(ViewTypeConstant.ARTIST_VIEW),
                SongsFragment.getInstance(ViewTypeConstant.ARTIST_VIEW)
                , new ArtistBioFragment()
        };

        String[] titles = {"ALBUMS", "SONGS", "BIO"};

        ArtistViewPagerAdapter artistViewPagerAdapter = new ArtistViewPagerAdapter(getSupportFragmentManager(), fragments, titles);

        artistViewPager.setAdapter(artistViewPagerAdapter);
        artistTabLayout.setupWithViewPager(artistViewPager);
    }

    @Override
    public void initView(List<Song> songs) {

    }
}
