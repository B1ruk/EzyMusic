package io.starter.biruk.ezymusic.bus.media;

import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Biruk on 10/21/2017.
 */
public class MediaRxEventBus {
    private static String TAG = "mediaRxEventbus";

    private static MediaRxEventBus mediaRxEventBus;

    private static int counter=0;

    private static Subject<Object> subject;

    private MediaRxEventBus() {
        subject = PublishSubject.create();
    }

    public static MediaRxEventBus getInstance() {
        if (mediaRxEventBus == null) {
            synchronized (MediaRxEventBus.class) {
                if (mediaRxEventBus == null) {
                    mediaRxEventBus = new MediaRxEventBus();
                }
            }
        }
        return mediaRxEventBus;
    }

    public void publish(@NonNull Object event) {
        counter++;

        Log.i(TAG, event.getClass().getSimpleName()+"\t"+counter);

        subject.onNext(event);


    }

    public Disposable subscribe(@NonNull Consumer<Object> subscriber) {
        return subject.subscribe(subscriber);
    }

}
