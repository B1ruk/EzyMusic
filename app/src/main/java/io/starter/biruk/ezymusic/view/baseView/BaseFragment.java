package io.starter.biruk.ezymusic.view.baseView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.events.view.SongLoadCompletedEvent;

/**
 * Created by Biruk on 10/13/2017.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "basefragment";
    private ProgressDialog progressBar;

    private CompositeDisposable compositeDisposable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onStart() {
        super.onStart();

        showProgress();

        compositeDisposable.add(ReplayEventBus.getInstance().subscribe(o -> {
            if (o instanceof SongLoadCompletedEvent) {
                Log.i(TAG, "song load completed");
                hideProgress();
                init();
            }
        })
        );


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected abstract void init();


    private void showProgress() {
        progressBar = new ProgressDialog(getContext());
        progressBar.setMessage("loading");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

    }

    private void hideProgress() {
        progressBar.hide();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();

    }
}
