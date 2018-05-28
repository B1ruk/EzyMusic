package io.starter.biruk.ezymusic.view.artistsView;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.model.dao.artistsDAO.ArtistDAO;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.ArtistPresenter;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.FastScroller;
import io.starter.biruk.ezymusic.view.artistsView.adapter.ArtistListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment implements ArtistView {


    private static final String TAG = "artistFragment";
    private ArtistPresenter artistPresenter;

    private RecyclerView artistRecyclerView;
    private FastScroller artistFastScroller;

    private ImageView emptyArtistsView;

    private ArtistListAdapter artistListAdapter;


    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artistPresenter = new ArtistPresenter(new ArtistDAO(getContext()), this,
                Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        artistRecyclerView = (RecyclerView) view.findViewById(R.id.artist_recyclerview);
        emptyArtistsView = (ImageView) view.findViewById(R.id.empty_artist_list_image);
        artistFastScroller= (FastScroller) view.findViewById(R.id.artist_fast_scroller);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        artistPresenter.artistSelectListener();
        artistPresenter.loadArtists();
    }

    @Override
    public void displayArtists(List<List<Song>> artists) {
        artistRecyclerView.setVisibility(View.VISIBLE);
        emptyArtistsView.setVisibility(View.GONE);

        artistListAdapter=new ArtistListAdapter(getContext(),artists);

        artistRecyclerView.setAdapter(artistListAdapter);
        artistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        artistRecyclerView.setHasFixedSize(true);

        artistFastScroller.setRecyclerView(artistRecyclerView);

        artistPresenter.scrollListener();


    }

    @Override
    public void scrollTo(int position) {
        artistRecyclerView.scrollToPosition(position);
    }

    @Override
    public void emptyArtistList() {
        artistRecyclerView.setVisibility(View.GONE);
        emptyArtistsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displaySelectedArtistView() {
        Log.i(TAG,"launch selected artist view");
        Intent intent=new Intent(getContext(),SelectedArtistActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();

        artistPresenter.cleanUp();
    }
}
