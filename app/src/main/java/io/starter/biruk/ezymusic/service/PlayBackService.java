package io.starter.biruk.ezymusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaReplayEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
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
import io.starter.biruk.ezymusic.service.playbackMode.PlayState;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;
import io.starter.biruk.ezymusic.util.AlbumArtworkUtil;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.view.mainView.MainActivity;

/**
 * Created by Biruk on 10/20/2017.
 */
public class PlayBackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "playbackservice";

    public static final String CURRENT_POSITION = "io.starter.biruk.ezymusic.service";
    public static final String CURRENT_POSITION_INTENT_KEY = "io.starter.biruk.ezymusic.service.KEY";
    private static final int NOTIFICATION_ID = 3870;

    /*
    * a broadcast manager for handling the CURRENT_POSITION
    * event
    * */
    private LocalBroadcastManager localBroadcastManager;
    private Binder serviceBinder = new PlayBackBinder();

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    private SongFormatUtil songFormatUtil;

    private int index;
    private List<Song> songList;

    private Shuffle shuffle;

    private Repeat repeat;

    private NotificationManagerCompat songNotificationCompat;
    private RemoteViews mLargeContentView = null;


    private CompositeDisposable mediaServiceCompositeDisposable;

    private AlbumArtworkUtil albumArtworkUtil;


    /*
    *  subscriber that listenes for ToggleEvent and toggles the media playbac
    * */
    private Disposable playPauseToggle = RxEventBus.getInstance().subscribe(o -> {
        if (o instanceof TogglePlayEvent) {
            if (isPlaying()) {
                pause();
            } else {
                resume();
            }
            updateNotification();
        }
    });

    /*
    * subscriber that listens for play event and plays the specified track by using
    * the index that it received from the event publisher
    * */
    private Disposable playTrack = RxEventBus.getInstance().subscribe(o -> {
        if (o instanceof PlayTrackEvent) {
            int index1 = ((PlayTrackEvent) o).getIndex();
            setIndex(index1);
            play();
            updateNotification();
        }
    });

    private Disposable seekTo = MediaRxEventBus.getInstance().subscribe(o -> {
        if (o instanceof SeekToEvent) {
            int index1 = ((SeekToEvent) o).getIndex();
            seekTo(index1);
        }
    });


    @Override
    public void onCreate() {
        super.onCreate();

        initComponents();

        initMedia();

        initPlayBackMode();

        playBackModeListener();

        registerBecomingNoisyReciever();

        this.songFormatUtil = new SongFormatUtil(this);
        this.albumArtworkUtil=new AlbumArtworkUtil(this,songFormatUtil);
    }

    private void playBackModeListener() {
        repeatModeListener();
        shuffleModeListener();
    }

    public void updateNotification() {
//        songNotificationCompat.notify(NOTIFICATION_ID, buildNotification());

        startForeground(NOTIFICATION_ID,buildNotification());
    }

    public Notification buildNotification() {
        Intent nowPlayingIntent = new Intent(this, MainActivity.class);

        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentIntent(clickIntent)
                .setCustomBigContentView(setUpBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build();

        return notification;
    }

    private RemoteViews setUpBigContentView() {
        if (this.mLargeContentView == null) {
            this.mLargeContentView = new RemoteViews(getPackageName(), R.layout.remote_view_large);
            initRemoteView(mLargeContentView);
        }
        updateRemoteView(mLargeContentView);
        return mLargeContentView;
    }

    private void initRemoteView(RemoteViews mLargeContentView) {
//        mLargeContentView.setImageViewResource(R.id.remote_previous,android.R.drawable.ic_media_previous);
//        mLargeContentView.setImageViewResource(R.id.remote_play_state,android.R.drawable.ic_media_pause);
//        mLargeContentView.setImageViewResource(R.id.remote_next,android.R.drawable.ic_media_next);

        mLargeContentView.setOnClickPendingIntent(R.id.remote_previous, getPendingIntent(PlayState.PREVIOUS));
        mLargeContentView.setOnClickPendingIntent(R.id.remote_play_state, getPendingIntent(PlayState.PLAY_PAUSE));
        mLargeContentView.setOnClickPendingIntent(R.id.remote_next, getPendingIntent(PlayState.NEXT));
    }

    private void updateRemoteView(RemoteViews mLargeContentView) {
        Song song = songList.get(index);
        String title = songFormatUtil.formatString(song.title, 24);
        String artist = songFormatUtil.formatString(song.artist, 16);

        int playPauseIcon = isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;

        mLargeContentView.setTextViewText(R.id.remote_song_title, title);
        mLargeContentView.setTextViewText(R.id.remote_song_artist, artist);
        mLargeContentView.setImageViewResource(R.id.remote_play_state, playPauseIcon);

        Bitmap bitMap = albumArtworkUtil.getBitMap(song.albumId);
        if (bitMap!=null){
            mLargeContentView.setImageViewBitmap(R.id.remote_song_cover_image,bitMap);
        }
    }

    public PendingIntent getPendingIntent(PlayState playState) {
        final ComponentName serviceName = new ComponentName(this, PlayBackService.class);
        Intent intent = new Intent(playState.toString());

        return PendingIntent.getService(this, 0, intent, 0);
    }

    /*
    * listens for shuffle mode event,then toggles the shuffle state
    * when the event occurs and finally it will post the event ShufflePostEvent
    * */
    private void shuffleModeListener() {
        mediaServiceCompositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof ShuffleToggleEvent) {
                        //toggle the state
                        shuffle = Shuffle.toggleMode(shuffle);
                        MediaReplayEventBus.getInstance().post(new ShufflePostEvent(shuffle));
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
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof RepeatToggleEvent) {
                        repeat = Repeat.toggleMode(repeat);
                        MediaReplayEventBus.getInstance().post(new RepeatPostEvent(repeat));
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
            updateNotification();
            ReplayEventBus.getInstance().post(new SaveIndexEvent(index));
        }
        if (index == songList.size() - 1) {
            if (repeat == Repeat.ALL) {
                //start from the beginning
                index = 0;
                play();
                updateNotification();
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

            Thread updateCurrentElapsedTime = new Thread(() -> {
                updateCurrentPosition();
            });
            updateNotification();
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


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
