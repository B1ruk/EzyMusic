package io.starter.biruk.ezymusic.view.artistsView.adapter;

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
import io.starter.biruk.ezymusic.events.view.ArtistSelectedEvent;
import io.starter.biruk.ezymusic.events.view.SelectedArtistEvent;
import io.starter.biruk.ezymusic.events.adapterPosition.ArtistAdapterPositionEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;
import io.starter.biruk.ezymusic.util.widgets.fastscroller.BubbleTextGetter;

/**
 * Created by Biruk on 10/9/2017.
 */
public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder> implements BubbleTextGetter{

    private static final String TAG = "ArtistListAdapter";
    private List<List<Song>> artistList;
    private Context appContext;

    private SongFormatUtil songFormatUtil;

    public ArtistListAdapter(Context appContext, List<List<Song>> artistList) {
        this.appContext = appContext;
        this.artistList = artistList;

        songFormatUtil = new SongFormatUtil(appContext);
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cardview_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        List<Song> songList = artistList.get(position);
        int songCount = songList.size();
        Song lastSong = songList.get(songCount - 1);

        String artist = songFormatUtil.formatString(lastSong.artist, 10);

        holder.artistNameView.setText(artist);
        holder.songCountView.setText(songFormatUtil.songCount(songCount));

        Picasso.with(appContext)
                .load(songFormatUtil.getAlbumArtWorkUri(lastSong.albumId))
                .placeholder(R.drawable.album_cover_)
                .fit()
                .into(holder.artistCoverArt);

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (artistList==null || artistList.isEmpty())
            return "";
        return String.valueOf(artistList.get(pos).get(0).artist.charAt(0));
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView artistCoverArt;
        TextView artistNameView;
        TextView songCountView;

        public ArtistViewHolder(View itemView) {
            super(itemView);

            artistCoverArt = (ImageView) itemView.findViewById(R.id.artist_image_cover);
            artistNameView = (TextView) itemView.findViewById(R.id.artist_name);
            songCountView = (TextView) itemView.findViewById(R.id.song_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            new ViewAnimatiorUtil(appContext).rotateY(v, 500);
            RxEventBus.getInstance().publish(new ArtistSelectedEvent());
            ReplayEventBus.getInstance().post(new ArtistAdapterPositionEvent(getAdapterPosition()));
            ReplayEventBus.getInstance().post(new SelectedArtistEvent(artistList.get(getAdapterPosition())));
        }
    }
}
