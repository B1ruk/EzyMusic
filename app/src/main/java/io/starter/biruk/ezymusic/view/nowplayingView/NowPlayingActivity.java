package io.starter.biruk.ezymusic.view.nowplayingView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaReplayEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.media.SeekToEvent;
import io.starter.biruk.ezymusic.events.media.TogglePlayEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.RepeatToggleEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.ShuffleToggleEvent;
import io.starter.biruk.ezymusic.events.view.CurrentSongEvent;
import io.starter.biruk.ezymusic.model.dao.FavoriteDao.FavoriteDao;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.presenter.NowPlayingPresenter;
import io.starter.biruk.ezymusic.service.PlayBackService;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;
import io.starter.biruk.ezymusic.util.ImageTransform.CircleTransform;
import io.starter.biruk.ezymusic.util.ImageTransform.blur.BlurTransform;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;
import io.starter.biruk.ezymusic.util.animation.FadeAnimation;
import io.starter.biruk.ezymusic.util.animation.TranslateAnimation;
import io.starter.biruk.ezymusic.util.view.Direction;
import io.starter.biruk.ezymusic.util.view.OnSwipeListener;
import io.starter.biruk.ezymusic.view.nowplayingView.nowPlayingViewFragment.NowPlayingFragment;

public class NowPlayingActivity extends AppCompatActivity implements NowPlayingView {

    private static final String TAG = "nowplayingActivity";

    public static final String CURRENT_SONG = "io.starter.biruk.ezymusic.view.nowplayingView.currentsong";

    private int index;

    private NowPlayingPresenter nowPlayingPresenter;
    private SongFormatUtil songFormatUtil;

    private ImageView currentSongCoverSmall;
    private TextView currentSongArtistView;
    private TextView currentSongTitleView;
    private ImageView playQueueView;

    private ImageButton favoriteBtn;

    private LinearLayout rootLayout;

    /*
    * views used for elapsed time
    * */
    private TextView currentDurationView;
    private TextView durationView;
    private SeekBar elapsedTimeSeekBar;

    /*
    * views for changing playback mode
    * */
    private ImageButton shuffleButton;
    private ImageButton repeatButton;

    /*
    *  container for nowplaying fragment
    * */
    private FrameLayout mainViewContainer;


    /*
    *   buttons used for media control
    * */
    private ImageButton playButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    private GestureDetector swipeDetector;

    private NowPlayingFragment nowPlayingFragment;


    //used for recieving the elapsed time update events
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

    private void initMainView() {
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

        mainViewContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, " onTouch");
                swipeDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void bindNowPlayingFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.now_playing_main_view_container, nowPlayingFragment)
                .commit();
    }

    /*
    * binds variable to the view
    *
    * */
    private void initWidgets() {
        currentSongCoverSmall = (ImageView) findViewById(R.id.current_song_mini_image_view);
        currentSongArtistView = (TextView) findViewById(R.id.current_song_mini_artist);
        currentSongTitleView = (TextView) findViewById(R.id.current_song_mini_title);
        playQueueView = (ImageView) findViewById(R.id.play_queue);

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
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        nowPlayingPresenter.loadList();
        nowPlayingPresenter.refresh();
        nowPlayingPresenter.updateCurrentView();

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewAnimatiorUtil(getBaseContext()).rotateY(favoriteBtn, 360);
                nowPlayingPresenter.updateFavoriteView();
            }
        });

        seekBarListener();
        initMediaControlBtns();

        nowPlayingPresenter.shuffleModeToggleListener();
        nowPlayingPresenter.repeatModeListener();
        nowPlayingPresenter.playPauseUpdater();

        LocalBroadcastManager.getInstance(this).registerReceiver(elapsedTimeReciever, new IntentFilter(PlayBackService.CURRENT_POSITION));

    }


    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(elapsedTimeReciever);


    }

    @Override
    public void updateBackground(Song song) {

        /*
        * updates the fragments artwork
        * */
        nowPlayingFragment.displayArtWork(song);
    }

    private void initMediaControlBtns() {
        playPauseToggle();
        nextSong();
        previousSong();

        shuffleToggle();
        repeatToggle();
    }

    private void shuffleToggle() {
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewAnimatiorUtil(getApplicationContext()).rotateY(shuffleButton, 485);
                MediaRxEventBus.getInstance().publish(new ShuffleToggleEvent());
            }
        });
    }

    private void repeatToggle() {
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ViewAnimatiorUtil(getApplicationContext()).rotateY(repeatButton, 485);
                MediaRxEventBus.getInstance().publish(new RepeatToggleEvent());
            }
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

    private int getIndex() {
        return index;
    }

    @Override
    public void displayPauseIcon() {
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
    }

    @Override
    public void displayPlayIcon() {
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
    }

    @Override
    public void playPauseToggle() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxEventBus.getInstance().publish(new TogglePlayEvent());
            }
        });
    }

    @Override
    public void nextSong() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPlayingPresenter.playNext();
            }
        });
    }

    @Override
    public void previousSong() {
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPlayingPresenter.playPrevious();
            }
        });
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

    private void seekBarListener() {
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
                nowPlayingPresenter.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        nowPlayingPresenter.saveState();
    }

    @Override
    protected void onDestroy() {
        nowPlayingPresenter.cleanUp();

        super.onDestroy();
    }

}
