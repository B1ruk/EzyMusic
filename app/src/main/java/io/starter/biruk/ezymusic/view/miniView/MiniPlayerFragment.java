package io.starter.biruk.ezymusic.view.miniView;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.media.TogglePlayEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.MiniPlayerPresenter;
import io.starter.biruk.ezymusic.service.PlayBackService;
import io.starter.biruk.ezymusic.util.ImageTransform.CircleTransform;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;
import io.starter.biruk.ezymusic.view.nowplayingView.NowPlayingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiniPlayerFragment extends Fragment implements MiniView {

    private static final String TAG = "miniplayerfragment";

    private TextView artistNameView;
    private TextView titleView;
    private ImageView artworkView;

    //used for play/Pause
    private ImageButton playPauseBtn;


    private CardView miniPlayerCardView;

    private MiniPlayerPresenter miniPlayerPresenter;

    private SongFormatUtil songFormatUtil;

    //needed for playbackService
    private PlayBackService playBackService;

    //used for checking whether the service is bound or not
    private boolean serviceBound;

    /*
    * establishing service connection
    * */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayBackService.PlayBackBinder playBackBinder = (PlayBackService.PlayBackBinder) service;

            playBackService = playBackBinder.getPlayerService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public MiniPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songFormatUtil = new SongFormatUtil(getContext());

        miniPlayerPresenter = new MiniPlayerPresenter(this);

        if (!serviceBound) {
            Log.i(TAG, "servicebound process ---");
            Intent intent = new Intent(this.getActivity(), PlayBackService.class);
            getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        miniPlayerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miniPlayerPresenter.launchNowPlayingView();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        miniPlayerPresenter.playListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        playPauseToggle();

        miniPlayerCardView.setVisibility(View.GONE);

        miniPlayerPresenter.updateMiniPlayer();

        miniPlayerPresenter.playPauseUpdater();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        miniPlayerPresenter.cleanUp();
    }

    @Override
    public void updateUi(Song song) {

        if (miniPlayerCardView.getVisibility() == View.GONE) {
            miniPlayerCardView.setVisibility(View.VISIBLE);
        }
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
    public void play(int index, List<Song> songList) {
        if (serviceBound) {
            Log.i(TAG, "play");
            playBackService.setQueue(index, songList);
            playBackService.play();
        }
    }

    @Override
    public void launchNowPlaying() {
        miniPlayerPresenter.saveIndex();

        Intent intent = new Intent(getContext(), NowPlayingActivity.class);
        startActivity(intent);
    }

    @Override
    public void playPauseToggle() {
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxEventBus.getInstance().publish(new TogglePlayEvent());
            }
        });
    }

    @Override
    public void displayPauseIcon() {
        playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_24dp));
    }

    @Override
    public void displayPlayIcon() {
        playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24dp));
    }
}
