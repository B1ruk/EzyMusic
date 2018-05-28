package io.starter.biruk.ezymusic.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaReplayEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.SelectedSongQueueEvent;
import io.starter.biruk.ezymusic.events.media.ChangePlayPauseEvent;
import io.starter.biruk.ezymusic.events.media.PlayTrackEvent;
import io.starter.biruk.ezymusic.events.media.SaveIndexEvent;
import io.starter.biruk.ezymusic.events.media.SeekToEvent;
import io.starter.biruk.ezymusic.events.media.TogglePlayEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.RepeatPostEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.RepeatToggleEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.ShufflePostEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.ShuffleToggleEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;

/**
 * Created by Biruk on 10/20/2017.
 */
public class PlayBackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "playbackservice";

    public static final String CURRENT_POSITION = "io.starter.biruk.ezymusic.service";
    public static final String CURRENT_POSITION_INTENT_KEY = "io.starter.biruk.ezymusic.service.KEY";

    /*
    * a broadcast manager for handling the CURRENT_POSITION
    * event
    * */
    private LocalBroadcastManager localBroadcastManager;
    private Binder serviceBinder = new PlayBackBinder();

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;


    /*
    * the current index & song list
    *
    * */
    private int index;
    private List<Song> songList;

    /*
    * for managing shuffle mode
    * */
    private Shuffle shuffle;

    /*
    * for managing repeat mode
    * */
    private Repeat repeat;


    private CompositeDisposable mediaServiceCompositeDisposable;

    @Override
    public void onCreate() {
        super.onCreate();

        initComponents();

        initMedia();

        initPlayBackMode();

        playBackModeListener();

        registerBecomingNoisyReciever();
    }

    private void playBackModeListener() {
        repeatModeListener();
        shuffleModeListener();
    }

    /*
    * listens for shuffle mode event,then toggles the shuffle state
    * when the event occurs and finally it will post the event ShufflePostEvent
    * */
    private void shuffleModeListener() {
        mediaServiceCompositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof ShuffleToggleEvent) {
                            //toggle the state
                            shuffle = Shuffle.toggleMode(shuffle);
                            MediaReplayEventBus.getInstance().post(new ShufflePostEvent(shuffle));
                        }
                    }
                })
        );

    }

    /*
    * listens for repeat mode event,when the event occurs it will toggle
    * the repeat mode state and finally it will post the event RepeatPostEvent
    * */
    private void repeatModeListener() {
        mediaServiceCompositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof RepeatToggleEvent) {
                            repeat = Repeat.toggleMode(repeat);
                            MediaReplayEventBus.getInstance().post(new RepeatPostEvent(repeat));
                        }
                    }
                })
        );
    }

    /*
    * sets the playback mode to their respective default value
    * */
    private void initPlayBackMode() {
        this.shuffle = Shuffle.OFF;
        this.repeat = Repeat.NONE;
    }

    /*
    * initialises the components
    * */
    private void initComponents() {
        this.songList = new ArrayList<>();
        this.index = 0;

        mediaServiceCompositeDisposable = new CompositeDisposable();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }

        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    public class PlayBackBinder extends Binder {
        public PlayBackService getPlayerService() {
            Log.i(TAG, " playbackbinder");
            return PlayBackService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    private void initMedia() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

        }
        mediaPlayer.setWakeMode(getBaseContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);

    }

    public void setQueue(int index, List<Song> songs) {
        this.index = index;
        this.songList = songs;
    }

    public void play() {

        stop();

        mediaPlayer.reset();

        if (mediaPlayer == null) {
            Toast.makeText(PlayBackService.this, "media player is null", Toast.LENGTH_SHORT).show();
        }

        Song song = songList.get(index);

        Log.i(TAG, " play " + song.title);

        try {
            mediaPlayer.setDataSource(song.data);
        } catch (IOException e) {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            Log.i(TAG, "prepare async error");
        }


    }

    public void stop() {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.isPlaying()) {
            Log.i(TAG, " stop");
            mediaPlayer.stop();
        }
    }

    /*
    * resumes the playback
    * */
    public void resume() {
        if (mediaPlayer == null) {
            return;
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

            /*
            * this event is useful for udpating the play/pause event
            * */
            MediaReplayEventBus.getInstance().post(new ChangePlayPauseEvent(mediaPlayer.isPlaying()));
        }
    }

    public void pause() {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.isPlaying()) {
            Log.i(TAG, "pause");
            mediaPlayer.pause();

            /*
            * this event is useful for udpating the play/pause event
            * */

            MediaReplayEventBus.getInstance().post(new ChangePlayPauseEvent(mediaPlayer.isPlaying()));
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    public long getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }


    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, " onCompletion");

        MediaReplayEventBus.getInstance().post(new ChangePlayPauseEvent(mediaPlayer.isPlaying()));

        if (index < songList.size() - 1) {
            if (repeat == Repeat.NONE || repeat == Repeat.ALL) {
                ++index;
            }
            if (repeat == Repeat.ONE) {
                //do not modify the index just play the current song
            }
            play();
            ReplayEventBus.getInstance().post(new SaveIndexEvent(index));
        }
        if (index == songList.size() - 1) {
            if (repeat == Repeat.ALL) {
                //start from the beginning
                index = 0;
                play();
                ReplayEventBus.getInstance().post(new SaveIndexEvent(index));
            }
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError");
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            Log.i(TAG, " onPrepared");

            MediaReplayEventBus.getInstance().post(new ChangePlayPauseEvent(mediaPlayer.isPlaying()));

            Thread updateCurrentElapsedTime = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateCurrentPosition();
                }
            });

            updateCurrentElapsedTime.start();
        }
    }

    private void updateCurrentPosition() {
        while (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            try {
                broadcastCurrentPosition();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastCurrentPosition() {
        Intent intent = new Intent(CURRENT_POSITION);
        if (mediaPlayer != null) {
            intent.putExtra(CURRENT_POSITION_INTENT_KEY, mediaPlayer.getCurrentPosition());
        }
        localBroadcastManager.sendBroadcast(intent);
    }

    /*
    * setting broadcast reciever for becomingNoisyEvent
    * */
    private BroadcastReceiver becomingNoisyReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }
    };

    public void registerBecomingNoisyReciever() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReciever, intentFilter);
    }


    /*
    *  subscriber that listenes for ToggleEvent and toggles the media playbac
    * */
    private Disposable playPauseToggle = RxEventBus.getInstance().subscribe(new Consumer<Object>() {
        @Override
        public void accept(Object o) throws Exception {
            if (o instanceof TogglePlayEvent) {
                if (isPlaying()) {
                    pause();
                } else {
                    resume();
                }
            }
        }
    });

    /*
    * subscriber that listenes for play event and plays the specified track by using
    * the index that it recieved from the event publisher
    * */
    private Disposable playTrack = RxEventBus.getInstance().subscribe(new Consumer<Object>() {
        @Override
        public void accept(Object o) throws Exception {
            if (o instanceof PlayTrackEvent) {
                int index = ((PlayTrackEvent) o).getIndex();
                setIndex(index);
                play();
            }
        }
    });

    private Disposable seekTo = MediaRxEventBus.getInstance().subscribe(new Consumer<Object>() {
        @Override
        public void accept(Object o) throws Exception {
            if (o instanceof SeekToEvent) {
                int index = ((SeekToEvent) o).getIndex();
                seekTo(index);
            }
        }
    });

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
