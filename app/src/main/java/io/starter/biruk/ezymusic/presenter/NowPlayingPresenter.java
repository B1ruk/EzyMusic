package io.starter.biruk.ezymusic.presenter;

import android.util.Log;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaReplayEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.SelectedSongQueueEvent;
import io.starter.biruk.ezymusic.events.media.ChangePlayPauseEvent;
import io.starter.biruk.ezymusic.events.media.PlayTrackEvent;
import io.starter.biruk.ezymusic.events.media.SaveIndexEvent;
import io.starter.biruk.ezymusic.events.media.SeekBarDraggedEvent;
import io.starter.biruk.ezymusic.events.media.SeekToEvent;
import io.starter.biruk.ezymusic.events.media.TrackChangeEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.RepeatPostEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.ShufflePostEvent;
import io.starter.biruk.ezymusic.model.dao.FavoriteDao.FavoriteRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.service.playbackMode.Repeat;
import io.starter.biruk.ezymusic.service.playbackMode.Shuffle;
import io.starter.biruk.ezymusic.view.nowplayingView.NowPlayingView;

/**
 * Created by Biruk on 10/9/2017.
 */
public class NowPlayingPresenter {

    private NowPlayingView nowPlayingView;
    private FavoriteRepository favoriteRepository;
    private Scheduler schedulerIO;
    private Scheduler mainThread;

    private List<Song> songs;
    private int index;

    private CompositeDisposable compositeDisposable;

    public NowPlayingPresenter(NowPlayingView nowPlayingView, FavoriteRepository favoriteRepository,
                               Scheduler schedulerIO, Scheduler mainThread) {
        this.nowPlayingView = nowPlayingView;
        this.favoriteRepository = favoriteRepository;
        this.schedulerIO = schedulerIO;
        this.mainThread = mainThread;

        this.compositeDisposable = new CompositeDisposable();
    }

    /*
    *
    * checks whether the song is favorite or not
    * */
    public void isSongFavorite() {
        int position = this.index;

        Song song = songs.get(position);
        favoriteRepository.isFavorite(song)
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(@NonNull Boolean favBoolean) {
                        if (favBoolean) {
                            nowPlayingView.songIsFavoriteView();
                        } else {
                            nowPlayingView.songIsNotFavoriteView();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
    }


    /*
    * updates the status of whether a song is favorite or not
    * by delegating the task to FavoriteRepository,and updates
    * the view.
    * */
    public void updateFavoriteView() {

        int position = this.index;

        Song song = songs.get(position);
        favoriteRepository.updateFavoriteStatus(song)
                .subscribeOn(schedulerIO)
                .observeOn(mainThread)
                .subscribeWith(new DisposableSingleObserver<Song>() {
                    @Override
                    public void onSuccess(@NonNull Song song) {
                        if (song.favorite) {
                            nowPlayingView.songIsFavoriteView();
                        } else {
                            nowPlayingView.songIsNotFavoriteView();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    /*
    * Loads the songlist from the Replay EventBus
    *
    * */
    public void loadList() {
        compositeDisposable.add(ReplayEventBus.getInstance().subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof SelectedSongQueueEvent) {
                            index = ((SelectedSongQueueEvent) o).getIndex();
                            songs = ((SelectedSongQueueEvent) o).getSongList();

                        }


                    }
                })
        );
    }


    /*
    *  An observer that is executed when the current track completes its playback
    * */
    public void refresh() {

        compositeDisposable.add(
                ReplayEventBus.getInstance().subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof SaveIndexEvent) {
                            int position = ((SaveIndexEvent) o).getIndex();

                            setIndex(position);
                            updateCurrentView();
                        }
                    }
                })
        );


    }

    /*
    * An observer that listens for a shuffle mode event and updates the
    * view when the event occurs
    * */
    public void shuffleModeToggleListener(){
        compositeDisposable.add(
                MediaReplayEventBus.getInstance().subscribe(o -> {
                    if (o instanceof ShufflePostEvent){
                        Shuffle shuffleMode = ((ShufflePostEvent) o).getShuffleMode();
                        System.out.println("from repeat mode"+shuffleMode.toString());
                        nowPlayingView.updateShuffleView(shuffleMode);
                    }
                })
        );
    }

    /*
    * An observer that listens for a repeat mode event and updates
    * the view when the event occurs
    * */
    public void repeatModeListener(){
        compositeDisposable.add(
                MediaReplayEventBus.getInstance().subscribe(o -> {
                    if (o instanceof RepeatPostEvent){
                        Repeat repeatMode = ((RepeatPostEvent) o).getRepeatMode();
                        System.out.println("from repeat mode"+repeatMode.toString());
                        nowPlayingView.updateRepeatView(repeatMode);
                    }
                })
        );
    }

    /*
    * An observer that listens for whether a Play/Pause event has occurred
    * and based on the result updates the view
    * */
    public void playPauseUpdater() {
        compositeDisposable.add(
                MediaReplayEventBus.getInstance().subscribe(o -> {
                    if (o instanceof ChangePlayPauseEvent) {
                        boolean playing = ((ChangePlayPauseEvent) o).isPlaying();

                        if (playing)
                            nowPlayingView.displayPauseIcon();
                        else
                            nowPlayingView.displayPlayIcon();
                    }
                })
        );
    }

    /*
    * updates the view when previous or next buttons
    * are triggered from the notification
    * */
    public void trackChangeListener(){
        compositeDisposable.add(
                MediaReplayEventBus.getInstance().subscribe(o->{
                    if (o instanceof TrackChangeEvent){
                        int index = ((TrackChangeEvent) o).getIndex();
                        List<Song> songs = ((TrackChangeEvent) o).getSongs();

                        updateQueue(index,songs);
                        updateCurrentView();
                    }
                })
        );
    }

    private void updateQueue(int index, List<Song> songs) {
        this.index=index;
        this.songs=songs;
    }

    /*
    *
    * calls the specific functions when there is a track change
    * */
    public void updateCurrentView() {
        isSongFavorite();
        nowPlayingView.updateView(songs.get(this.index));
    }

    /*
    *  Posts an event for playing the previous track
    *  updates the now playing view
    * */
    public void playPrevious() {
        if (index > 0) {
            --index;
            RxEventBus.getInstance().publish(new PlayTrackEvent(index));
        } else if (index == 0) {
            //restart the song
            RxEventBus.getInstance().publish(new PlayTrackEvent(index));
        }
        this.updateCurrentView();
    }

    /*
    * posts an event for playing the next track
    * updates the now playing view
    * */
    public void playNext() {
        if (index < songs.size() - 1) {
            ++index;
            RxEventBus.getInstance().publish(new PlayTrackEvent(index));
        }
        this.updateCurrentView();
    }

    /*
    *  posts an event for playing the specified event
    * */
    public void play(int index) {
        RxEventBus.getInstance().publish(new PlayTrackEvent(index));
    }


    /*
    * posts an event when the user releases the seekbar
    * */
    public void seekTo(int progress) {
        MediaRxEventBus.getInstance().publish(new SeekToEvent(progress));
    }

    /*
    * Posts an event to the MediaBus when the seekbar is dragged
    * */
    public void onSeekBarDragged(int progress) {
        MediaRxEventBus.getInstance().publish(new SeekBarDraggedEvent(progress));
    }

    /*
    * posts an saveIndexEvent
    * */
    public void saveState() {
        ReplayEventBus.getInstance().post(new SaveIndexEvent(index));
    }

    private void setIndex(int index) {
        if (index <= songs.size() - 1) {
            this.index = index;
        }
    }

    /*
    * Cleans the compositeDisposable so that it will not leak
    * */
    public void cleanUp() {
        compositeDisposable.clear();
    }

}
