package io.starter.biruk.ezymusic.bus;


import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Biruk on 10/9/2017.
 */
public class ReplayEventBus {
    private static String TAG = "replayEventBus";

    private static ReplayEventBus replayEventBus;

    private static Subject<Object> subject;

    private ReplayEventBus() {
        subject = ReplaySubject.create();
    }

    public static ReplayEventBus getInstance() {
        if (replayEventBus == null) {
            synchronized (ReplayEventBus.class) {
                if (replayEventBus == null) {
                    replayEventBus = new ReplayEventBus();
                }
            }
        }
        return replayEventBus;
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
