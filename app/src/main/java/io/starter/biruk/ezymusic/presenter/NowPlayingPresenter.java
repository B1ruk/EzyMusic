package io.starter.biruk.ezymusic.presenter;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.starter.biruk.ezymusic.SeekBarDraggedEvent;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.SeekToEvent;
import io.starter.biruk.ezymusic.events.media.MediaStatusEvent;
import io.starter.biruk.ezymusic.events.media.PlayerToggleEvent;
import io.starter.biruk.ezymusic.events.media.RequestMediaStatusEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.PlayPauseStatusEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.RepeatStatusEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.ShuffleStatusEvent;
import io.starter.biruk.ezymusic.model.dao.FavoriteDao.FavoriteRepository;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.service.playbackMode.MediaTrigger;
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


    public void requestMediaStatus() {
        RxEventBus.getInstance().publish(new RequestMediaStatusEvent());
    }

    public void loadMediaStatus() {
        compositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof MediaStatusEvent){
                        MediaStatusEvent state = (MediaStatusEvent) o;

                        updateQueue(state.getQueueEvent().getIndex(), state.getQueueEvent().getSongs());

                        updateCurrentView();
                        nowPlayingView.updatePlayPause(state.isPlaying());
                        nowPlayingView.updateShuffleView(state.getShuffleState());
                        nowPlayingView.updateRepeatView(state.getRepeatState());
                    }
                })
        );
    }

    public void mediaCallbackListener() {
        compositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof ShuffleStatusEvent){
                        nowPlayingView.updateShuffleView(((ShuffleStatusEvent) o).getShuffle());
                    }
                    if (o instanceof RepeatStatusEvent){
                        nowPlayingView.updateRepeatView(((RepeatStatusEvent) o).getRepeat());
                    }
                    if (o instanceof PlayPauseStatusEvent){
                        nowPlayingView.updatePlayPause(((PlayPauseStatusEvent) o).isPlaying());
                    }
                })
        );
    }


    public void playNext() {
        RxEventBus.getInstance().publish(new PlayerToggleEvent(MediaTrigger.NEXT));
    }

    public void playPrevious() {
        RxEventBus.getInstance().publish(new PlayerToggleEvent(MediaTrigger.PREVIOUS));
    }

    public void updateQueue(int index, List<Song> songs) {
        this.index = index;
        this.songs = songs;
    }

    public void updateCurrentView() {
        isSongFavorite();
        nowPlayingView.updateView(songs.get(this.index));
    }


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

    public void seekTo(int progress) {
        MediaRxEventBus.getInstance().publish(new SeekToEvent(progress));
    }

    public void onSeekBarDragged(int progress) {
        MediaRxEventBus.getInstance().publish(new SeekBarDraggedEvent(progress));
    }

    public void cleanUp() {
        compositeDisposable.clear();
    }

}
