package io.starter.biruk.ezymusic.view.artistsView.BioView;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.starter.biruk.ezymusic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistBioFragment extends Fragment {


    public ArtistBioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_bio, container, false);
    }

}
