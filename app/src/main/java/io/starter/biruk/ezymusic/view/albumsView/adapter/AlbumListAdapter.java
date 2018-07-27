package io.starter.biruk.ezymusic.view.albumsView.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.view.AlbumSelectedEvent;
import io.starter.biruk.ezymusic.events.view.SelectedAlbumEvent;
import io.starter.biruk.ezymusic.events.adapterPosition.AlbumAdapterPositionEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.BubbleTextGetter;

/**
 * Created by Biruk on 10/9/2017.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder> implements BubbleTextGetter {

    private static final String TAG = "albumListAdapter";
    private List<List<Song>> albums;
    private Context appContext;

    private SongFormatUtil songFormatUtil;


    public AlbumListAdapter(List<List<Song>> albums, Context appContext) {
        this.albums = albums;
        this.appContext = appContext;

        songFormatUtil = new SongFormatUtil(appContext);
    }

    public void loadAlbums(List<List<Song>> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        List<Song> songList = albums.get(position);
        int count = songList.size();
        Song lastSong = songList.get(count - 1);

        String title = songFormatUtil.formatString(lastSong.albumTitle, 14);
        String artist = songFormatUtil.formatString(lastSong.artist, 10);

        holder.albumTitleView.setText(title);
        holder.artistView.setText(artist);
        holder.songCountView.setText(songFormatUtil.songCount(count));

        Picasso.with(appContext)
                .load(songFormatUtil.getAlbumArtWorkUri(lastSong.albumId))
                .centerCrop()
                .placeholder(R.drawable.album_cover_)
                .fit()
                .into(holder.albumCover);

    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (albums==null || albums.isEmpty())
            return "";

        return String.valueOf(albums.get(pos).get(0).albumTitle.charAt(0));
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView albumTitleView;
        TextView artistView;
        TextView songCountView;
        ImageView albumCover;
        ImageView albumDetailView;

        public AlbumViewHolder(View itemView) {
            super(itemView);

            albumTitleView = (TextView) itemView.findViewById(R.id.album_title);
            artistView = (TextView) itemView.findViewById(R.id.album_artist);
            songCountView = (TextView) itemView.findViewById(R.id.album_song_count);
            albumCover = (ImageView) itemView.findViewById(R.id.album_cover_Art);
            albumDetailView = (ImageView) itemView.findViewById(R.id.album_detail_btn);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            ReplayEventBus.getInstance().post(new AlbumAdapterPositionEvent(getAdapterPosition()));

            new ViewAnimatiorUtil(appContext).rotateY(v, 500)
                    .addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ReplayEventBus.getInstance().post(new SelectedAlbumEvent(albums.get(getAdapterPosition())));
                            RxEventBus.getInstance().publish(new AlbumSelectedEvent());
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
        }

    }
}
