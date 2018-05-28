package io.starter.biruk.ezymusic.bus.media;


import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Biruk on 10/9/2017.
 */
public class MediaReplayEventBus {
    private static String TAG = "mediaReplayEventBus";

    private static MediaReplayEventBus mediaReplayEventBus;

    private static Subject<Object> subject;

    private MediaReplayEventBus() {
        subject = ReplaySubject.create();
    }

    public static MediaReplayEventBus getInstance() {
        if (mediaReplayEventBus == null) {
            synchronized (MediaReplayEventBus.class) {
                if (mediaReplayEventBus == null) {
                    mediaReplayEventBus = new MediaReplayEventBus();
                }
            }
        }
        return mediaReplayEventBus;
    }

    public void post(@NonNull Object event) {
        Log.i(TAG, event.getClass().getSimpleName());
        subject.onNext(event);
    }

    public Disposable subscribe(@NonNull final Consumer<Object> subscriber) {
        return subject
                .subscribe(subscriber);
    }

}
