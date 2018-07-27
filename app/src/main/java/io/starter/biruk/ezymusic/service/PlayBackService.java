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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.SeekToEvent;
import io.starter.biruk.ezymusic.events.media.MediaStatusEvent;
import io.starter.biruk.ezymusic.events.media.PlayerToggleEvent;
import io.starter.biruk.ezymusic.events.media.RequestMediaStatusEvent;
import io.starter.biruk.ezymusic.events.media.RequestQueueEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.PlayPauseStatusEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.QueueEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.RepeatStatusEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.ShuffleStatusEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.service.playbackMode.PlayState;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;
import io.starter.biruk.ezymusic.util.AlbumArtworkUtil;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.view.nowplayingView.NowPlayingActivity;

import static io.starter.biruk.ezymusic.service.playbackMode.PlayState.NEXT;
import static io.starter.biruk.ezymusic.service.playbackMode.PlayState.PLAY_PAUSE;
import static io.starter.biruk.ezymusic.service.playbackMode.PlayState.PREVIOUS;

/**
 * Created by Biruk on 10/20/2017.
 */
public class PlayBackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "playbackservice";

    public static final String CURRENT_POSITION = "io.starter.biruk.ezymusic.service";
    public static final String CURRENT_POSITION_INTENT_KEY = "io.starter.biruk.ezymusic.service.KEY";
    private static final int NOTIFICATION_ID = 3870;

    private LocalBroadcastManager localBroadcastManager;
    private Binder serviceBinder = new PlayBackBinder();

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    private SongFormatUtil songFormatUtil;

    private int index;
    private List<Song> songList;

    private int shuffledIndex;
    private List<Song> shuffledSongList;

    private Shuffle shuffle;

    private Repeat repeat;

    private NotificationManagerCompat songNotificationCompat;
    private RemoteViews mLargeContentView = null;

    private CompositeDisposable mediaServiceCompositeDisposable;
    private AlbumArtworkUtil albumArtworkUtil;


    public BroadcastReceiver becomingNoisyReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }
    };

    private final BroadcastReceiver mediaIntentReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMediaCommandIntent(intent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initComponents();
        initMedia();
        initPlayBackMode();
        registerBecomingNoisyReciever();
        initMediaReciever();
        initMediaEventListener();


    }

    private void initComponents() {
        this.songList = new LinkedList<>();
        this.shuffledSongList=new LinkedList<>();
        this.index = 0;

        mediaServiceCompositeDisposable = new CompositeDisposable();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        this.songFormatUtil = new SongFormatUtil(this);
        this.albumArtworkUtil = new AlbumArtworkUtil(this, songFormatUtil);

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

    private void initMediaReciever() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(PlayState.PREVIOUS);
        filter.addAction(NEXT);
        filter.addAction(PlayState.PLAY_PAUSE);

        registerReceiver(mediaIntentReciever, filter);
    }

    public void registerBecomingNoisyReciever() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReciever, intentFilter);
    }

    public void initMediaEventListener() {
        mediaServiceCompositeDisposable.add(
                RxEventBus.getInstance().subscribe(o -> {

                    if (o instanceof RequestMediaStatusEvent) {
                        postMediaStatus();
                    }
                    if (o instanceof RequestQueueEvent){
                        postQueueStatus();
                    }

                    if (o instanceof QueueEvent) {
                        int index = ((QueueEvent) o).getIndex();
                        List<Song> songs = ((QueueEvent) o).getSongs();

                        switch (shuffle) {
                            case ON:
                                this.shuffledIndex=index;
                                this.shuffledSongList=songs;
                                break;
                            case OFF:
                                setQueue(index, songs);
                                break;
                        }
                        play();
                    }

                    if (o instanceof PlayerToggleEvent) {
                        PlayerToggleEvent toggleEvent = (PlayerToggleEvent) o;
                        switch (toggleEvent.getMediaTrigger()) {
                            case PLAY_PAUSE:
                                togglePlayPause();
                                break;
                            case NEXT:
                                next();
                                break;
                            case PREVIOUS:
                                previous();
                                break;
                            case TOGGLE_SHUFFLE:
                                toggleShuffle();
                                break;
                            case TOGGLE_REPEAT:
                                toggleRepeat();
                                break;
                        }
                    }
                })
        );

        mediaServiceCompositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof SeekToEvent) {
                        int progress = ((SeekToEvent) o).getProgress();
                        seekTo(progress);
                    }
                })
        );
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            handleMediaCommandIntent(intent);
        }
        return START_NOT_STICKY;
    }

    public void togglePlayPause() {
        MediaRxEventBus.getInstance().publish(new PlayPauseStatusEvent(!isPlaying()));
        if (isPlaying()) {
            pause();
        } else {
            resume();
        }
        updateNotification();
    }

    public void toggleShuffle() {
        this.shuffle = Shuffle.toggleMode(this.shuffle);
        switch (shuffle) {
            case ON:
                shuffleSongs(songList);
                break;
            case OFF:
                unshuffleSongs(shuffledSongList);
                break;
        }
        postQueueStatus();
        MediaRxEventBus.getInstance().publish(new ShuffleStatusEvent(shuffle));
    }

    public void toggleRepeat() {
        this.repeat = Repeat.toggleMode(this.repeat);
        MediaRxEventBus.getInstance().publish(new RepeatStatusEvent(repeat));
    }

    public void updateNotification() {
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    public Notification buildNotification() {
        Intent nowPlayingIntent = new Intent(this, NowPlayingActivity.class);

        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_CANCEL_CURRENT);

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
        mLargeContentView.setOnClickPendingIntent(R.id.remote_previous, getPendingIntent(PlayState.PREVIOUS));
        mLargeContentView.setOnClickPendingIntent(R.id.remote_play_state, getPendingIntent(PlayState.PLAY_PAUSE));
        mLargeContentView.setOnClickPendingIntent(R.id.remote_next, getPendingIntent(NEXT));
    }

    private PendingIntent getPendingIntent(String playState) {
        final ComponentName serviceName = new ComponentName(this, PlayBackService.class);
        Intent intent = new Intent(playState);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, 0, intent, 0);
    }

    private void updateRemoteView(RemoteViews mLargeContentView) {
        Song song = null;

        switch (shuffle) {
            case ON:
                song = shuffledSongList.get(shuffledIndex);
                break;
            case OFF:
                song = songList.get(index);
                break;
        }

        String title = songFormatUtil.formatString(song.title, 24);
        String artist = songFormatUtil.formatString(song.artist, 16);

        int playPauseIcon = isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;

        mLargeContentView.setTextViewText(R.id.remote_song_title, title);
        mLargeContentView.setTextViewText(R.id.remote_song_artist, artist);
        mLargeContentView.setImageViewResource(R.id.remote_play_state, playPauseIcon);

        Bitmap bitMap = albumArtworkUtil.getBitMap(song.albumId);
        if (bitMap != null) {
            mLargeContentView.setImageViewBitmap(R.id.remote_song_cover_image, bitMap);
        }
    }

    /*
    *   Handles the intent that is sent by the notification buttons
    * */
    private void handleMediaCommandIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, String.format("in handleMediaCmdInt -->  %s", action));

        switch (action) {
            case PLAY_PAUSE:
                togglePlayPause();
//                updateNotification();
                break;
            case PREVIOUS:
                previous();
                break;
            case NEXT:
                next();
                break;
        }

    }

    private void initPlayBackMode() {
        this.shuffle = Shuffle.OFF;
        this.repeat = Repeat.NONE;
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
            return PlayBackService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    public void setQueue(int index, List<Song> songs) {
        this.index = index;
        this.songList = songs;
    }

    public void shuffleSongs(List<Song> songs) {
        Song currentSong = songs.get(index);

        shuffledSongList.clear();
        shuffledSongList.addAll(songs);

        Collections.shuffle(shuffledSongList);
        this.shuffledIndex = 0;

        //swap the first one with the current song
        for (int i = 0; i < shuffledSongList.size(); i++) {
            if (shuffledSongList.get(i) == currentSong) {
                Collections.swap(shuffledSongList, 0, i);
                break;
            }
        }
    }

    public void unshuffleSongs(List<Song> songs) {
        Song currentSong = songs.get(shuffledIndex);

        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).songId == currentSong.songId) {
                setIndex(i);
                break;
            }
        }
    }

    public void previous() {
        switch (shuffle) {
            case ON:
                playShufflePreviousTrack();
                break;
            case OFF:
                playPreviousTrack();
                break;
        }
    }

    private void playPreviousTrack() {
        if (index > 0) {
            index--;
        } else if (index == 0) {
            index = songList.size() - 1;
        }
        play();
    }

    private void playShufflePreviousTrack() {
        if (index > 0) {
            shuffledIndex--;
        } else if (shuffledIndex == 0) {
            shuffledIndex = shuffledSongList.size() - 1;
        }
        play();
    }

    public void next() {
        switch (shuffle) {
            case ON:
                playShuffleNextTrack();
                break;
            case OFF:
                playNextTrack();
                break;
        }

    }

    private void playNextTrack() {
        if (songList.size() == 1) {

        } else if (index < songList.size() - 1) {
            index++;
        } else if (index == songList.size() - 1) {
            index = 0;
        }

        play();
    }

    private void playShuffleNextTrack() {
        if (shuffledSongList.size() == 1) {
        } else if (shuffledIndex < shuffledSongList.size() - 1) {
            shuffledIndex++;
        } else if (shuffledIndex == shuffledSongList.size() - 1) {
            shuffledIndex = 0;
        }
        play();
    }

    public void play() {

        stop();
        mediaPlayer.reset();
        Song song = null;
        switch (shuffle) {
            case ON:
                song = shuffledSongList.get(shuffledIndex);
                break;
            case OFF:
                song = songList.get(index);
                break;
        }
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

    public void resume() {
        if (mediaPlayer == null) {
            return;
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

        }
    }

    public void pause() {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.isPlaying()) {
            Log.i(TAG, "pause");
            mediaPlayer.pause();

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

    @Override
    public void onAudioFocusChange(int focusChange) {

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

            Thread updateCurrentElapsedTime = new Thread(this::updateCurrentPosition);
            updateNotification();
            updateCurrentElapsedTime.start();

            postMediaStatus();
        }
    }

    private void updateCurrentPosition() {
        while (mediaPlayer != null) {
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, " onCompletion");

        switch (shuffle) {
            case ON:

                if (shuffledIndex < shuffledSongList.size() - 1) {
                    if (repeat == Repeat.NONE || repeat == Repeat.ALL) {
                        ++shuffledIndex;
                    }
                    play();
                    updateNotification();
                } else if (shuffledIndex == shuffledSongList.size() - 1) {
                    if (repeat == Repeat.ALL || repeat == Repeat.ONE) {
                        //start from the beginning
                        shuffledIndex = 0;
                        play();
                        updateNotification();
                    }
                }
                break;
            case OFF:

                if (index < songList.size() - 1) {
                    if (repeat == Repeat.NONE || repeat == Repeat.ALL) {
                        ++index;
                    }
                    play();
                    updateNotification();
                } else if (index == songList.size() - 1) {
                    if (repeat == Repeat.ALL || repeat == Repeat.ONE) {
                        //start from the beginning
                        index = 0;
                        play();
                        updateNotification();
                    }
                }
                break;
        }

    }

    public void postQueueStatus(){
        switch (shuffle){
            case ON:
                MediaRxEventBus.getInstance().publish(new QueueEvent(shuffledIndex,shuffledSongList));
                break;
            case OFF:
                MediaRxEventBus.getInstance().publish(new QueueEvent(index,songList));
                break;
        }
    }


    public void postMediaStatus() {
        switch (shuffle) {
            case ON:
                MediaRxEventBus.getInstance().publish(new MediaStatusEvent(
                        isPlaying(),
                        shuffle,
                        repeat,
                        new QueueEvent(shuffledIndex, shuffledSongList)
                ));
                break;
            case OFF:
                MediaRxEventBus.getInstance().publish(new MediaStatusEvent(
                        isPlaying(),
                        shuffle,
                        repeat,
                        new QueueEvent(getIndex(), songList)
                ));
                break;

        }

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mediaIntentReciever);
        unregisterReceiver(becomingNoisyReciever);
        mediaServiceCompositeDisposable.clear();
        super.onDestroy();
    }
}
