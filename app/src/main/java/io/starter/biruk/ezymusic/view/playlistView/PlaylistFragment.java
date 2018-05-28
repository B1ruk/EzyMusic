package io.starter.biruk.ezymusic.view.playlistView;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.model.dao.FavoriteDao.FavoriteDao;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.PlaylistPresenter;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;
import io.starter.biruk.ezymusic.util.ViewTypeConstant;
import io.starter.biruk.ezymusic.util.animation.FadeAnimation;
import io.starter.biruk.ezymusic.util.animation.TranslateAnimation;
import io.starter.biruk.ezymusic.view.songsView.SongsFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements PlaylistView {


    private static final String TAG = "playlist/favorite";

    private RecyclerView favoriteRecyclerView;

    private PlaylistPresenter playlistPresenter;

    private SongFormatUtil songFormatUtil;
    ViewAnimatiorUtil viewAnimatiorUtil;


    private ImageView favoriteArtwork1;
    private ImageView favoriteArtwork2;
    private ImageView favoriteArtwork3;
    private ImageView favoriteArtwork4;

    private TextView favoriteSongCountView;
    private TextView favoriteSongDurationView;

    private CardView favoriteContainer;

    private FrameLayout favoriteListContainer;

    private LinearLayout rootPlaylistView;

    FadeAnimation fadeAnimation;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        songFormatUtil = new SongFormatUtil(getContext());

        viewAnimatiorUtil = new ViewAnimatiorUtil(getContext());

        playlistPresenter = new PlaylistPresenter(this, new FavoriteDao(getContext()),
                Schedulers.io(), AndroidSchedulers.mainThread());
    }

    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        favoriteArtwork1 = (ImageView) view.findViewById(R.id.favorite_cover_1);
        favoriteArtwork2 = (ImageView) view.findViewById(R.id.favorite_cover_2);
        favoriteArtwork3 = (ImageView) view.findViewById(R.id.favorite_cover_3);
        favoriteArtwork4 = (ImageView) view.findViewById(R.id.favorite_cover_4);

        rootPlaylistView = (LinearLayout) view.findViewById(R.id.root_playlist_view);

        favoriteSongCountView = (TextView) view.findViewById(R.id.favorite_songs_count);
        favoriteSongDurationView = (TextView) view.findViewById(R.id.favorite_songs_duration);

        favoriteContainer = (CardView) view.findViewById(R.id.favorite_container);

        favoriteListContainer = (FrameLayout) view.findViewById(R.id.favorite_song_list_container);


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fadeAnimation = new FadeAnimation(favoriteContainer, 900, 0.0f, 1.0f,
                new DecelerateInterpolator(2.0f));

        rootPlaylistView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateAnimation animation = viewAnimatiorUtil.slideDownAnimation(favoriteListContainer, 960);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        favoriteListContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        favoriteListContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        playlistPresenter.loadFavorites();
        playlistPresenter.loadFavoritesImage();

        favoriteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeAnimation.animate();
                playlistPresenter.launchFavoriteView();
            }
        });

    }

    @Override
    public void displayFavoritesInfo(List<Song> songs) {
        favoriteSongCountView.setText(songFormatUtil.songCount(songs.size()));

        long sum = 0;
        for (Song song : songs) {
            sum += song.duration;
        }

        favoriteSongDurationView.setText(songFormatUtil.formatSongDuration(sum));
    }

    @Override
    public void displayFavoriteArtWorks(List<Song> artworks) {

        if (artworks.size() >= 4) {
            Picasso.with(getContext())
                    .load(songFormatUtil.getAlbumArtWorkUri(artworks.get(0).albumId))
                    .fit()
                    .into(favoriteArtwork1);
            Picasso.with(getContext())
                    .load(songFormatUtil.getAlbumArtWorkUri(artworks.get(1).albumId))
                    .fit()
                    .into(favoriteArtwork2);
            Picasso.with(getContext())
                    .load(songFormatUtil.getAlbumArtWorkUri(artworks.get(2).albumId))
                    .fit()
                    .into(favoriteArtwork3);
            Picasso.with(getContext())
                    .load(songFormatUtil.getAlbumArtWorkUri(artworks.get(3).albumId))
                    .fit()
                    .into(favoriteArtwork4);
        }
    }

    @Override
    public void launchFavoriteView() {
        favoriteListContainer.setVisibility(View.VISIBLE);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.favorite_song_list_container, SongsFragment.getInstance(ViewTypeConstant.ARTIST_VIEW))
                .commit();

        viewAnimatiorUtil.slideUp(favoriteListContainer, 960);
    }


    @Override
    public void updateFavorites() {

    }

    @Override
    public void displayEmptyFavoritesView() {
    }
}
