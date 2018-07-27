package io.starter.biruk.ezymusic.view.albumsView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.events.view.SelectedAlbumEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.view.songsView.adapter.SongListAdapter;

public class SelectedAlbumActivity extends AppCompatActivity {

    private static final String TAG="selectedAlbumActivity";

    Toolbar selectedAlbumToolBar;
    RecyclerView songListRecycler;
    ImageView selectedAlbumCover;

    private CompositeDisposable compositeDisposable;
    private SongFormatUtil songFormatUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_album);

        compositeDisposable = new CompositeDisposable();

        selectedAlbumCover = (ImageView) findViewById(R.id.selected_album_cover);
        selectedAlbumToolBar = (Toolbar) findViewById(R.id.selected_album_toolbar);
        songListRecycler = (RecyclerView) findViewById(R.id.song_list_recycler);

        setSupportActionBar(selectedAlbumToolBar);

        songFormatUtil=new SongFormatUtil(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        compositeDisposable.add(ReplayEventBus.getInstance().subscribe(o -> {
            if (o instanceof SelectedAlbumEvent) {
                Log.i(TAG,"\tinitViews");
                List<Song> album = ((SelectedAlbumEvent) o).getAlbum();
                initRecyclerView(album);
                initToolBar(album);
            }
        })
        );
    }

    private void initToolBar(List<Song> album) {
        Song song = album.get(0);
        String title=songFormatUtil.formatString(song.albumTitle,15);
        getSupportActionBar().setTitle(title);

        Picasso.with(this)
                .load(songFormatUtil.getAlbumArtWorkUri(song.albumId))
                .fit()
                .placeholder(R.drawable.art_2)
                .into(selectedAlbumCover);

    }

    private void initRecyclerView(List<Song> album) {
        sortAlbum(album);
        SongListAdapter songListAdapter=new SongListAdapter(this,album);

        songListRecycler.setAdapter(songListAdapter);
        songListRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sortAlbum(List<Song> album) {
        Collections.sort(album, (lhs, rhs) -> {
            int t1 = lhs.trackNumber;
            int t2 = rhs.trackNumber;
            return t1-t2;
        });
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        Log.i(TAG,"\tonStop");
        super.onStop();
    }
}
