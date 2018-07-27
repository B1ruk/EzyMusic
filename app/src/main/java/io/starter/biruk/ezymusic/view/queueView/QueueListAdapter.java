package io.starter.biruk.ezymusic.view.queueView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.adapterPosition.SongAdapterPositionEvent;
import io.starter.biruk.ezymusic.events.media.playbackMode.QueueEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.SongFormatUtil;

/**
 * Created by biruk on 27/07/18.
 */

public class QueueListAdapter extends RecyclerView.Adapter<QueueListAdapter.QueueViewHolder> {

    private Context context;
    private List<Song> songs;
    private SongFormatUtil songFormatUtil;

    public QueueListAdapter(Context context,List<Song> songs) {
        this.context = context;
        this.songs = songs;
        songFormatUtil=new SongFormatUtil(context);
    }

    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_queue, parent, false);
        return new QueueViewHolder(view);

    }

    @Override
    public void onBindViewHolder(QueueViewHolder holder, int position) {
        final Song currentSong = songs.get(position);

        String title = songFormatUtil.formatString(currentSong.title, 26);
        String artist = songFormatUtil.formatString(currentSong.artist, 24);

        holder.songTitleTextView.setText(title);
        holder.artistTextView.setText(artist);

        Picasso.with(context)
                .load(songFormatUtil.getAlbumArtWorkUri(currentSong.albumId))
                .resize(69, 69)
                .placeholder(R.drawable.ic_album_black_24dp)
                .centerCrop()
                .into(holder.albumCover);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class QueueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songTitleTextView;
        TextView artistTextView;
        ImageView albumCover;
        ImageView songQueueDragger;

        public QueueViewHolder(View itemView) {
            super(itemView);

            songTitleTextView = (TextView) itemView.findViewById(R.id.queue_song_title);
            artistTextView = (TextView) itemView.findViewById(R.id.queue_song_artist);
            albumCover = (ImageView) itemView.findViewById(R.id.queue_song_cover_image);
            songQueueDragger= (ImageView) itemView.findViewById(R.id.queue_song_dragger);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RxEventBus.getInstance().publish(new QueueEvent(getAdapterPosition(), songs));
            ReplayEventBus.getInstance().post(new SongAdapterPositionEvent(getAdapterPosition()));
        }

    }
}
