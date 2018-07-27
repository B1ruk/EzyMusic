package io.starter.biruk.ezymusic.view.miniView;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.media.PlayerToggleEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.MiniPlayerPresenter;
import io.starter.biruk.ezymusic.service.playbackMode.MediaTrigger;
import io.starter.biruk.ezymusic.util.ImageTransform.CircleTransform;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.view.nowplayingView.NowPlayingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiniPlayerFragment extends Fragment implements MiniView {

    private static final String TAG = "miniplayerfragment";

    private TextView artistNameView;
    private TextView titleView;
    private ImageView artworkView;

    private ImageButton playPauseBtn;

    private CardView miniPlayerCardView;
    private MiniPlayerPresenter miniPlayerPresenter;
    private SongFormatUtil songFormatUtil;

    public MiniPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songFormatUtil = new SongFormatUtil(getContext());
        miniPlayerPresenter = new MiniPlayerPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);

        artworkView = (ImageView) view.findViewById(R.id.mini_song_cover_image);
        titleView = (TextView) view.findViewById(R.id.mini_song_title);
        artistNameView = (TextView) view.findViewById(R.id.mini_song_artist);
        playPauseBtn = (ImageButton) view.findViewById(R.id.mini_play_state);
        miniPlayerCardView = (CardView) view.findViewById(R.id.mini_player_root);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        miniPlayerCardView.setOnClickListener(v -> miniPlayerPresenter.launchNowPlayingView());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        playPauseBtn.setOnClickListener(v -> RxEventBus.getInstance().publish(new PlayerToggleEvent(MediaTrigger.PLAY_PAUSE)));

        miniPlayerPresenter.loadMediaStatus();
        miniPlayerPresenter.requestMediaStatus();
        miniPlayerPresenter.playPauseEventListener();
    }

    @Override
    public void updateUi(Song song) {

        String title = songFormatUtil.formatString(song.title, 29);
        String artist = songFormatUtil.formatString(song.artist, 29);

        titleView.setText(title);
        artistNameView.setText(artist);
        Picasso.with(getContext())
                .load(songFormatUtil.getAlbumArtWorkUri(song.albumId))
                .resize(79, 79)
                .transform(new CircleTransform())
                .placeholder(R.drawable.music_list_)
                .into(artworkView);

    }

    @Override
    public void launchNowPlaying() {
        Intent intent = new Intent(getContext(), NowPlayingActivity.class);
        startActivity(intent);
    }

    @Override
    public void displayPauseIcon() {
        playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_24dp));
    }

    @Override
    public void displayPlayIcon() {
        playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24dp));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        miniPlayerPresenter.cleanUp();
    }

}
