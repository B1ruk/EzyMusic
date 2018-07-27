package io.starter.biruk.ezymusic.presenter;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaReplayEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.media.MediaStatusEvent;
import io.starter.biruk.ezymusic.events.media.RequestMediaStatusEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.PlayPauseStatusEvent;
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

        songList = new LinkedList<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void requestMediaStatus() {
        RxEventBus.getInstance().publish(new RequestMediaStatusEvent());
    }

    public void loadMediaStatus() {
        compositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof MediaStatusEvent) {
                        MediaStatusEvent state = (MediaStatusEvent) o;

                        updateQueue(state.getQueueEvent().getIndex(), state.getQueueEvent().getSongs());
                        updateView();

                        if (state.isPlaying()) {
                            miniView.displayPauseIcon();
                        } else {
                            miniView.displayPlayIcon();
                        }
                    }
                })
        );
    }

    public void updateView() {
        if (!songList.isEmpty()) {
            miniView.updateUi(songList.get(index));
        }
    }

    public void playPauseEventListener() {
        compositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof PlayPauseStatusEvent) {
                        boolean playing = ((PlayPauseStatusEvent) o).isPlaying();

                        if (playing)
                            miniView.displayPauseIcon();
                        else
                            miniView.displayPlayIcon();
                    }
                })
        );
    }

    private void updateQueue(int index, List<Song> songs) {
        this.index = index;
        this.songList = songs;
    }

    public void launchNowPlayingView() {
        miniView.launchNowPlaying();
    }

    public void cleanUp() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
