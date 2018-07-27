package io.starter.biruk.ezymusic.view.nowplayingView.nowPlayingViewFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.SeekBarDraggedEvent;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.SeekToEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.ImageTransform.CircleTransform;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;
import io.starter.biruk.ezymusic.util.animation.FadeAnimation;
import io.starter.biruk.ezymusic.util.view.Direction;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment {

    private static final String TAG = "nowplayingfragment";
    private Song currentSong;

    private ImageView currentSongCover;

    private SongFormatUtil songFormatUtil;
    private ViewAnimatiorUtil viewAnimatiorUtil;

    private FrameLayout seekBarViewContainer;
    private TextView seekBarProgressView;

    private FadeAnimation fadeAnimation;

    private CompositeDisposable compositeDisposable;

    public NowPlayingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songFormatUtil = new SongFormatUtil(getContext());
        viewAnimatiorUtil = new ViewAnimatiorUtil(getContext());
        compositeDisposable = new CompositeDisposable();

        fadeAnimation = new FadeAnimation(currentSongCover, 680, 0.0f, 1.0f, new DecelerateInterpolator(1.0f));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentSongCover = (ImageView) view.findViewById(R.id.current_song_large_image_view);

        seekBarViewContainer = (FrameLayout) view.findViewById(R.id.current_time_view_container);
        seekBarProgressView = (TextView) view.findViewById(R.id.elapsed_time_on_drag_tv);

    }

    @Override
    public void onResume() {
        super.onResume();

        currentDurationView();
    }

    public void displayArtWork(Song song) {
        fadeAnimation.animate();

        Picasso.with(getContext())
                .load(songFormatUtil.getAlbumArtWorkUri(song.albumId))
                .transform(new CircleTransform())
                .placeholder(R.drawable.art_2)
                .fit()
                .into(currentSongCover);

    }


    public void animate(Direction direction) {
        switch (direction) {
            case DOWN:
                viewAnimatiorUtil.slideDown(currentSongCover,660);
                break;
            case UP:
                viewAnimatiorUtil.slideUp(currentSongCover,660);
                break;
        }
    }


    /*
    *
    *   listens for events of SeekBarDraggedEvent and
    *   updates the view
    * */
    private void currentDurationView() {
        compositeDisposable.add(
                MediaRxEventBus.getInstance().subscribe(o -> {
                    if (o instanceof SeekBarDraggedEvent) {

                        seekBarViewContainer.setVisibility(View.VISIBLE);
                        seekBarProgressView.setVisibility(View.VISIBLE);

                        int index = ((SeekBarDraggedEvent) o).getProgress();
                        String s = songFormatUtil.formatSongDuration(index);
                        seekBarProgressView.setText(s);

                    } else if (o instanceof SeekToEvent) {
                        seekBarViewContainer.setVisibility(View.GONE);
                        seekBarProgressView.setVisibility(View.GONE);
                    }
                })
        );
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
