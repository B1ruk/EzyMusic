package io.starter.biruk.ezymusic.presenter;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaReplayEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.SelectedSongQueueEvent;
import io.starter.biruk.ezymusic.events.media.ChangePlayPauseEvent;
import io.starter.biruk.ezymusic.events.media.PlayEvent;
import io.starter.biruk.ezymusic.events.media.SaveIndexEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.view.miniView.MiniView;

/**
 * Created by Biruk on 10/9/2017.
 */
public class MiniPlayerPresenter {
    private MiniView miniView;
    List<Song> songList;
    int index;

    private CompositeDisposable compositeDisposable;

    public MiniPlayerPresenter(MiniView miniView) {
        this.miniView = miniView;

        songList=new LinkedList<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void updateMiniPlayer() {
        ReplayEventBus.getInstance().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof SelectedSongQueueEvent) {
                    index = ((SelectedSongQueueEvent) o).getIndex();
                    songList = ((SelectedSongQueueEvent) o).getSongList();


                }

                if (o instanceof SaveIndexEvent) {
                    index = ((SaveIndexEvent) o).getIndex();
                }

                if (!songList.isEmpty()) {

                    Song song = songList.get(index);
                    miniView.updateUi(song);
                }

            }
        });

    }

    /*
    * listenes for play event and calls the appropraiate view
    * */
    public void playListener() {
        RxEventBus.getInstance().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof PlayEvent) {
                    miniView.play(index, songList);
                }
            }
        });
    }

    /*
    * by subscribing to the MediaRxEventBus
    * updates the play/pause icons
    * */
    public void playPauseUpdater() {
        compositeDisposable.add(
                MediaReplayEventBus.getInstance().subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof ChangePlayPauseEvent) {
                            boolean playing = ((ChangePlayPauseEvent) o).isPlaying();

                            if (playing)
                                miniView.displayPauseIcon();
                            else
                                miniView.displayPlayIcon();
                        }
                    }
                })
        );
    }

    public void saveIndex(){
        ReplayEventBus.getInstance().post(new SaveIndexEvent(index));
    }

    /*
    * opens the nowplaying view
    * */
    public void launchNowPlayingView() {
        miniView.launchNowPlaying();
    }


    /*
    * cleans the compositeDisposable
    * */
    public void cleanUp() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
