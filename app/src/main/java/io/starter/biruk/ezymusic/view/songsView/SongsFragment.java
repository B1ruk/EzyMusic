package io.starter.biruk.ezymusic.view.songsView;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.model.dao.songDao.SongDao;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.SongPresenter;
import io.starter.biruk.ezymusic.util.ViewTypeConstant;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.FastScroller;
import io.starter.biruk.ezymusic.view.baseView.BaseFragment;
import io.starter.biruk.ezymusic.view.songsView.adapter.SongListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends BaseFragment implements SongView {

    private static final String TAG = "songsFragment";

    /*
    * a string used for determining the type of view to initiate
    * */
    private String viewType;

    RelativeLayout rootLayout;

    RecyclerView songRecyclerView;
    FastScroller fastScroller;

    ImageView emptyListImage;

    private SongPresenter songPresenter;


    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment getInstance(String viewType) {
        Bundle args = new Bundle();
        args.putString(TAG, viewType);

        SongsFragment songsFragment = new SongsFragment();
        songsFragment.setArguments(args);
        return songsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewType = getArguments().getString(TAG);

        if (viewType == null) {
            this.viewType = ViewTypeConstant.DEFAULT_VIEW;
        }

        songPresenter = new SongPresenter(new SongDao(this.getContext()), this, Schedulers.io(), AndroidSchedulers.mainThread());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);


        rootLayout = (RelativeLayout) view.findViewById(R.id.song_list_root);
        songRecyclerView = (RecyclerView) view.findViewById(R.id.song_list_recycler);
        fastScroller= (FastScroller) view.findViewById(R.id.fast_Scroller);
        emptyListImage = (ImageView) view.findViewById(R.id.empty_song_list);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (viewType.equals(ViewTypeConstant.ARTIST_VIEW)) {
            songPresenter.init();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (viewType.equals(ViewTypeConstant.ARTIST_VIEW)) {
            songPresenter.loadArtistSongs();
        }
    }

    @Override
    protected void init() {
        if (viewType.equals(ViewTypeConstant.DEFAULT_VIEW)) {
            songPresenter.loadSongs();
        }
    }

    @Override
    public void displaySongs(List<Song> songs) {

        Log.i(TAG, "song" + songs.size());
        emptyListImage.setVisibility(View.GONE);
        songRecyclerView.setVisibility(View.VISIBLE);

        SongListAdapter songListAdapter = new SongListAdapter(getContext(), songs);
        songRecyclerView.setAdapter(songListAdapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        songRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                getActivity().getMenuInflater()
                        .inflate(R.menu.context_list, menu);
            }


        });
        songListAdapter.loadSongs(songs);

        fastScroller.setRecyclerView(songRecyclerView);

        songPresenter.scrollListener();
    }

    @Override
    public void scrollTo(int index) {
        songRecyclerView.scrollToPosition(index);
    }

    @Override
    public void displaySongLoadSuccessMsg(List<Song> songList) {
    }

    @Override
    public void emptySongsNotFound() {
        songRecyclerView.setVisibility(View.GONE);
        emptyListImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSongSelected(Song song) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater()
                .inflate(R.menu.context_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        songPresenter.cleanup();
    }
}
