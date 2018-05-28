package io.starter.biruk.ezymusic.bus;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Biruk on 10/9/2017.
 */
public class RxEventBus {
    private static String TAG = "rxbus";

    private static RxEventBus rxEventBus;

    private static Subject<Object> subject;

    private RxEventBus() {
        subject = PublishSubject.create();
    }

    public static RxEventBus getInstance() {
        if (rxEventBus == null) {
            synchronized (RxEventBus.class) {
                if (rxEventBus == null) {
                    rxEventBus = new RxEventBus();
                }
            }
        }
        return rxEventBus;
    }

    public void publish(@NonNull Object event) {
        Log.i(TAG, event.getClass().getSimpleName());
        subject.onNext(event);

    }

    public Disposable subscribe(@NonNull Consumer<Object> subscriber) {
        return subject.subscribe(subscriber);
    }

}
