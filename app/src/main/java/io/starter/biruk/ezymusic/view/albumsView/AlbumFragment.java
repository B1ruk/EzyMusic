package io.starter.biruk.ezymusic.view.albumsView;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.model.dao.albumDAO.AlbumDAO;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.AlbumPresenter;
import io.starter.biruk.ezymusic.service.ImageDownloaderService;
import io.starter.biruk.ezymusic.service.PlayBackService;
import io.starter.biruk.ezymusic.util.ViewTypeConstant;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.FastScroller;
import io.starter.biruk.ezymusic.view.albumsView.adapter.AlbumCoverFlowAdapter;
import io.starter.biruk.ezymusic.view.albumsView.adapter.AlbumListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment implements AlbumView {


    private static final String TAG = "AlbumFragment";
    private String ViewType;

    private AlbumPresenter albumPresenter;

    private RelativeLayout rootAlbumLayout;

    private RecyclerView albumRecycerView;
    private FastScroller albumFastScroller;

    private AlbumListAdapter albumListAdapter;
    private ImageView emptyAlbumListView;

    private ImageDownloaderService imgImageDownloaderService;

    private boolean isBound;

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            imgImageDownloaderService= ((ImageDownloaderService.ImgBinder) service).getImageDlService();
            isBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound=false;
        }
    };
    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment getInstance(String viewType) {

        Bundle args = new Bundle();
        args.putString(TAG, String.valueOf(viewType));

        AlbumFragment albumFragment = new AlbumFragment();
        albumFragment.setArguments(args);

        return albumFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewType = getArguments().getString(TAG);

        if (ViewType == null) {
            this.ViewType = ViewTypeConstant.DEFAULT_VIEW;
        }

        albumPresenter = new AlbumPresenter(this, new AlbumDAO(getContext()),
                Schedulers.io(), AndroidSchedulers.mainThread());

        bindToService();
    }

    private void bindToService() {
        Intent intent = new Intent(getActivity(), ImageDownloaderService.class);
        getActivity().bindService(intent,serviceConnection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        rootAlbumLayout = (RelativeLayout) view.findViewById(R.id.album_root_layout);
        albumRecycerView = (RecyclerView) view.findViewById(R.id.album_recyclerview);
        albumFastScroller= (FastScroller) view.findViewById(R.id.album_fast_Scroller);
        emptyAlbumListView = (ImageView) view.findViewById(R.id.empty_album_list_image);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ViewType.equals(ViewTypeConstant.ARTIST_VIEW)) {
            albumPresenter.init();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (ViewType.equals(ViewTypeConstant.ARTIST_VIEW)) {
            albumPresenter.loadArtistAlbums();
        } else {
            albumPresenter.loadAlbums();
        }
        albumPresenter.onAlbumSelected();

        if (isBound){
            Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void scrollTo(int index) {
        Log.i(TAG, "\tscrollTo\t" + index);

        albumRecycerView.scrollToPosition(index);
    }

    @Override
    public void displayAlbums(List<List<Song>> albumList) {

        displayAlbumsRecyclerView(albumList);
        albumPresenter.scrollToPosition();
    }

    /*
    * displays the album list using recycler view
    * */
    private void displayAlbumsRecyclerView(List<List<Song>> albumList) {
        emptyAlbumListView.setVisibility(View.GONE);

        albumRecycerView.setVisibility(View.VISIBLE);

        albumListAdapter = new AlbumListAdapter(albumList, getContext());
        albumRecycerView.setAdapter(albumListAdapter);
        albumRecycerView.setHasFixedSize(true);
        albumRecycerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        albumFastScroller.setRecyclerView(albumRecycerView);
        Log.i(TAG, "\tdisplay albums");
    }

    public void displayAlbumsCoverFlow(List<List<Song>> albums) {

    }

    @Override
    public void emptyAlbumList() {
        albumRecycerView.setVisibility(View.GONE);
        emptyAlbumListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void launchSelectedAlbumView() {
        Intent intent = new Intent(getContext(), SelectedAlbumActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        albumPresenter.cleanUp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!isBound){
            Log.i(TAG,"unbind imgservice ");
            getActivity().unbindService(serviceConnection);
        }
    }
}
