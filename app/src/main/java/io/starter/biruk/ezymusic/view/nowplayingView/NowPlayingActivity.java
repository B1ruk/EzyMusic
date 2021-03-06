package io.starter.biruk.ezymusic.view.nowplayingView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wunderlist.slidinglayer.SlidingLayer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.media.PlayerToggleEvent;
import io.starter.biruk.ezymusic.model.dao.FavoriteDao.FavoriteDao;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.NowPlayingPresenter;
import io.starter.biruk.ezymusic.service.PlayBackService;
import io.starter.biruk.ezymusic.service.playbackMode.MediaTrigger;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;
import io.starter.biruk.ezymusic.util.ImageTransform.CircleTransform;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;
import io.starter.biruk.ezymusic.util.ViewTypeConstant;
import io.starter.biruk.ezymusic.util.animation.FadeAnimation;
import io.starter.biruk.ezymusic.util.view.Direction;
import io.starter.biruk.ezymusic.util.view.OnSwipeListener;
import io.starter.biruk.ezymusic.view.nowplayingView.nowPlayingViewFragment.NowPlayingFragment;
import io.starter.biruk.ezymusic.view.queueView.QueueFragment;
import io.starter.biruk.ezymusic.view.songsView.SongsFragment;

public class NowPlayingActivity extends AppCompatActivity implements NowPlayingView {

    private static final String TAG = "nowplayingActivity";

    public static final String CURRENT_SONG = "io.starter.biruk.ezymusic.view.nowplayingView.currentsong";

    private int index;

    private NowPlayingPresenter nowPlayingPresenter;
    private SongFormatUtil songFormatUtil;

    private ImageView currentSongCoverSmall;
    private TextView currentSongArtistView;
    private TextView currentSongTitleView;
    private ImageView queueBtnView;

    private ImageButton favoriteBtn;

    private LinearLayout rootLayout;

    private TextView currentDurationView;
    private TextView durationView;
    private SeekBar elapsedTimeSeekBar;

    private ImageButton shuffleButton;
    private ImageButton repeatButton;

    private FrameLayout mainViewContainer;
    private FrameLayout queueContainer;
    private SlidingLayer queueSlider;

    private ImageButton playButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    private GestureDetector swipeDetector;

    private NowPlayingFragment nowPlayingFragment;


    private BroadcastReceiver elapsedTimeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentDuration = intent.getIntExtra(PlayBackService.CURRENT_POSITION_INTENT_KEY, 0);

            String songDuration = songFormatUtil.formatSongDuration(currentDuration);

            currentDurationView.setText(songDuration);
            elapsedTimeSeekBar.setProgress(currentDuration);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        initWidgets();
        nowPlayingFragment = new NowPlayingFragment();
        bindNowPlayingFragment();
        songFormatUtil = new SongFormatUtil(this);
        nowPlayingPresenter = new NowPlayingPresenter(this, new FavoriteDao(this),
                Schedulers.io(), AndroidSchedulers.mainThread());

        initMainView();
    }

    public void initMainView() {
        final FadeAnimation fadeAnimation = new FadeAnimation(mainViewContainer, 1250, 0.0f, 1.0f, new DecelerateInterpolator(2.0f));

        swipeDetector = new GestureDetector(this, new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                nowPlayingFragment.animate(direction);
                switch (direction) {

                    case DOWN:
                        nowPlayingPresenter.playNext();
                        break;
                    case UP:
                        fadeAnimation.animate();
                        nowPlayingPresenter.playPrevious();
                }
                return super.onSwipe(direction);
            }
        });

        mainViewContainer.setOnTouchListener((v, event) -> {
            swipeDetector.onTouchEvent(event);
            return true;
        });
    }

    public void bindNowPlayingFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.now_playing_main_view_container, nowPlayingFragment)
                .commit();
    }

    public void initWidgets() {
        currentSongCoverSmall = (ImageView) findViewById(R.id.current_song_mini_image_view);
        currentSongArtistView = (TextView) findViewById(R.id.current_song_mini_artist);
        currentSongTitleView = (TextView) findViewById(R.id.current_song_mini_title);

        currentDurationView = (TextView) findViewById(R.id.elapsedTimeSeekbarProgress);
        durationView = (TextView) findViewById(R.id.song_duration_text_view);
        elapsedTimeSeekBar = (SeekBar) findViewById(R.id.seek_bar);

        playButton = (ImageButton) findViewById(R.id.play_song_btn);
        previousButton = (ImageButton) findViewById(R.id.previous_song_btn);
        nextButton = (ImageButton) findViewById(R.id.next_song_btn);

        mainViewContainer = (FrameLayout) findViewById(R.id.now_playing_main_view_container);

        favoriteBtn = (ImageButton) findViewById(R.id.favorites_btn);

        rootLayout = (LinearLayout) findViewById(R.id.now_playing_root_view);

        shuffleButton = (ImageButton) findViewById(R.id.shuffle);
        repeatButton = (ImageButton) findViewById(R.id.repeat);

        queueContainer= (FrameLayout) findViewById(R.id.queue_container);
        queueSlider= (SlidingLayer) findViewById(R.id.queue_sliding_layout);
        queueBtnView = (ImageView) findViewById(R.id.queue_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        favoriteBtn.setOnClickListener(v -> {
            new ViewAnimatiorUtil(getBaseContext()).rotateY(favoriteBtn, 360);
            nowPlayingPresenter.updateFavoriteView();
        });

        seekBarListener();
        initMediaControlBtns();
        LocalBroadcastManager.getInstance(this).registerReceiver(elapsedTimeReciever, new IntentFilter(PlayBackService.CURRENT_POSITION));

        nowPlayingPresenter.loadMediaStatus();
        nowPlayingPresenter.requestMediaStatus();
        nowPlayingPresenter.mediaCallbackListener();
        initQueueView();
    }

    public void initQueueView(){
        queueBtnView.setOnClickListener(v->{
            if (queueSlider.isClosed()){
                queueSlider.openLayer(true);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.queue_container,new QueueFragment())
                        .commit();

            }else {
                queueSlider.closeLayer(true);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(elapsedTimeReciever);
    }

    @Override
    public void updateBackground(Song song) {
        nowPlayingFragment.displayArtWork(song);
    }

    public void initMediaControlBtns() {
        playPauseToggle();
        nextSong();
        previousSong();

        shuffleToggle();
        repeatToggle();
    }


    @Override
    public void playPauseToggle() {
        playButton.setOnClickListener(v -> RxEventBus.getInstance().publish(new PlayerToggleEvent(MediaTrigger.PLAY_PAUSE)));
    }

    @Override
    public void nextSong() {
        nextButton.setOnClickListener(v -> nowPlayingPresenter.playNext());
    }

    @Override
    public void previousSong() {
        previousButton.setOnClickListener(v -> nowPlayingPresenter.playPrevious());
    }

    public void shuffleToggle() {
        shuffleButton.setOnClickListener(v -> {
            new ViewAnimatiorUtil(getApplicationContext()).rotateY(shuffleButton, 485);
            RxEventBus.getInstance().publish(new PlayerToggleEvent(MediaTrigger.TOGGLE_SHUFFLE));
        });
    }

    public void repeatToggle() {
        repeatButton.setOnClickListener(v -> {
            new ViewAnimatiorUtil(getApplicationContext()).rotateY(repeatButton, 485);
            RxEventBus.getInstance().publish(new PlayerToggleEvent(MediaTrigger.TOGGLE_REPEAT));
        });
    }

    @Override
    public void updateView(Song song) {
        this.updateTopView(song);
        this.updateBackground(song);
        this.updateSeekBar(song.duration);
    }

    @Override
    public void updateTopView(Song currentSong) {
        String title = songFormatUtil.formatString(currentSong.title, 26);
        String artist = songFormatUtil.formatString(currentSong.artist, 24);

        currentSongArtistView.setText(artist);
        currentSongTitleView.setText(title);

        Picasso.with(this)
                .load(songFormatUtil.getAlbumArtWorkUri(currentSong.albumId))
                .resize(69, 69)
                .transform(new CircleTransform())
                .centerCrop()
                .placeholder(R.drawable.music_list_)
                .into(currentSongCoverSmall);

    }


    @Override
    public void songIsFavoriteView() {
        favoriteBtn.setColorFilter(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void songIsNotFavoriteView() {
        favoriteBtn.setColorFilter(getResources().getColor(R.color.bottomBarInActive));
    }

    public void updatePlayPause(boolean isPlaying) {
        if (isPlaying) {
            LocalBroadcastManager.getInstance(this).registerReceiver(elapsedTimeReciever, new IntentFilter(PlayBackService.CURRENT_POSITION));
        } else {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(elapsedTimeReciever);
        }
        updatePlayPauseIcon(isPlaying);
    }


    public void updatePlayPauseIcon(boolean isPlaying) {
        if (isPlaying) {
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
        } else {
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));

        }
    }

    @Override
    public void updateSeekBar(long duration) {
        String songDuration = songFormatUtil.formatSongDuration(duration);

        elapsedTimeSeekBar.setMax((int) duration);
        durationView.setText(songDuration);

    }

    @Override
    public void updateShuffleView(Shuffle shuffleMode) {
        switch (shuffleMode) {
            case ON:
                shuffleButton.setColorFilter(getResources().getColor(R.color.colorAccent));
                break;
            case OFF:
                shuffleButton.setColorFilter(getResources().getColor(R.color.bottomBarInActive));
                break;
        }
    }

    @Override
    public void updateRepeatView(Repeat repeatMode) {
        switch (repeatMode) {
            case NONE:
                repeatButton.setColorFilter(getResources().getColor(R.color.bottomBarInActive));
                break;
            case ONE:
                repeatButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));
                repeatButton.setColorFilter(getResources().getColor(R.color.colorAccent));
                break;
            case ALL:
                repeatButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                repeatButton.setColorFilter(getResources().getColor(R.color.colorAccent));
                break;
        }
    }

    public void seekBarListener() {
        elapsedTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    nowPlayingPresenter.onSeekBarDragged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentDurationView.setText(songFormatUtil.formatSongDuration(seekBar.getProgress()));
                nowPlayingPresenter.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        nowPlayingPresenter.cleanUp();

        super.onDestroy();
    }

}
