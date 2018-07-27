package io.starter.biruk.ezymusic.view.songsView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
import io.starter.biruk.ezymusic.util.animation.FadeAnimation;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.BubbleTextGetter;

/**
 * Created by Biruk on 10/7/2017.
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> implements BubbleTextGetter {

    private static final String TAG = "songListAdapter\t";
    private Context appContext;
    private List<Song> songList;

    private SongFormatUtil songFormatUtil;


    public SongListAdapter(Context appContext, List<Song> songList) {
        this.appContext = appContext;
        this.songList = songList;

        this.songFormatUtil = new SongFormatUtil(appContext);
    }

    public void loadSongs(List<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }


    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_song_view, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        final Song currentSong = songList.get(position);

        String title = songFormatUtil.formatString(currentSong.title, 26);
        String artist = songFormatUtil.formatString(currentSong.artist, 24);
        String songDuration = songFormatUtil.formatSongDuration(currentSong.duration);

        holder.songTitleTextView.setText(title);
        holder.artistTextView.setText(artist);
        holder.durationTextView.setText(songDuration);

        Picasso.with(appContext)
                .load(songFormatUtil.getAlbumArtWorkUri(currentSong.albumId))
                .resize(69, 69)
                .placeholder(R.drawable.ic_album_black_24dp)
                .centerCrop()
                .into(holder.albumCover);
    }

    @Override
    public int getItemCount() {
        return songList == null ? 0 : songList.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (songList==null || songList.isEmpty())
            return "";
        return String.valueOf(songList.get(pos).title.charAt(0));
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnLongClickListener{

        TextView songTitleTextView;
        TextView artistTextView;
        TextView durationTextView;
        ImageView albumCover;
        ImageButton songDetailsBtn;

        public SongViewHolder(View itemView) {
            super(itemView);

            songTitleTextView = (TextView) itemView.findViewById(R.id.song_title);
            artistTextView = (TextView) itemView.findViewById(R.id.song_artist);
            durationTextView = (TextView) itemView.findViewById(R.id.song_duration);
            albumCover = (ImageView) itemView.findViewById(R.id.song_cover_image);
            songDetailsBtn = (ImageButton) itemView.findViewById(R.id.song_details_btn);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FadeAnimation fadeAnimation=new FadeAnimation(v,600,0.0f,1.0f,
                    new DecelerateInterpolator(2.0f));
            fadeAnimation.animate();
            RxEventBus.getInstance().publish(new QueueEvent(getAdapterPosition(), songList));
            ReplayEventBus.getInstance().post(new SongAdapterPositionEvent(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            Log.i(TAG,"\t"+getAdapterPosition());
            return true;
        }
    }
}
