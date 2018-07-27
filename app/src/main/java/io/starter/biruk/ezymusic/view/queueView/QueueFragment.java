package io.starter.biruk.ezymusic.view.queueView;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.bus.media.MediaRxEventBus;
import io.starter.biruk.ezymusic.events.media.RequestQueueEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.QueueEvent;
import io.starter.biruk.ezymusic.model.entity.Song;

/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment {


    private RecyclerView queueRecyclerView;

    public QueueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        queueRecyclerView = (RecyclerView) view.findViewById(R.id.queue_recycler);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queueListener();
        RxEventBus.getInstance().publish(new RequestQueueEvent());
    }

    public void queueListener() {
        MediaRxEventBus.getInstance().subscribe(o -> {
            if (o instanceof QueueEvent) {
                int index = ((QueueEvent) o).getIndex();
                List<Song> songs = ((QueueEvent) o).getSongs();
                updateQueueRecycler(index, songs);
            }
        });
    }

    public void updateQueueRecycler(int index, List<Song> songs) {
        queueRecyclerView.setAdapter(new QueueListAdapter(getContext(), songs));
        queueRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        if (index < songs.size() - 1) {
            queueRecyclerView.scrollToPosition(index + 1);
        }

    }
}
