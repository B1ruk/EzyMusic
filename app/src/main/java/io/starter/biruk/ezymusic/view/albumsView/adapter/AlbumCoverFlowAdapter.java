package io.starter.biruk.ezymusic.view.albumsView.adapter;

import android.animation.Animator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.starter.biruk.ezymusic.R;
import io.starter.biruk.ezymusic.bus.ReplayEventBus;
import io.starter.biruk.ezymusic.bus.RxEventBus;
import io.starter.biruk.ezymusic.events.AlbumSelectedEvent;
import io.starter.biruk.ezymusic.events.SelectedAlbumEvent;
import io.starter.biruk.ezymusic.events.adapterPosition.AlbumAdapterPositionEvent;
import io.starter.biruk.ezymusic.model.entity.Song;
import io.starter.biruk.ezymusic.util.SongFormatUtil;
import io.starter.biruk.ezymusic.util.ViewAnimatiorUtil;

/**
 * Created by Biruk on 10/11/2017.
 */
public class AlbumCoverFlowAdapter extends BaseAdapter {

    private static final String TAG = "albumCoverFlow";
    private Context appContext;
    private List<List<Song>> albums;

    private SongFormatUtil songFormatUtil;

    public AlbumCoverFlowAdapter(Context appContext, List<List<Song>> albums) {
        this.appContext = appContext;
        this.albums = albums;
        Log.i(TAG,""+albums.size());

        this.songFormatUtil = new SongFormatUtil(appContext);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public List<Song> getItem(int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View albumView = convertView;

        if (albumView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            albumView = layoutInflater.inflate(R.layout.cardview_album, parent, false);
        }

        TextView artistView = (TextView) convertView.findViewById(R.id.album_artist);
        TextView songCountView = (TextView) convertView.findViewById(R.id.album_song_count);
        TextView albumTitleView = (TextView) convertView.findViewById(R.id.album_title);
        ImageView albumCover = (ImageView) convertView.findViewById(R.id.album_cover_Art);

        List<Song> songs = getItem(position);
        Song lastSong = songs.get(songs.size() - 1);


        String title = songFormatUtil.formatString(lastSong.albumTitle, 14);
        String artist = songFormatUtil.formatString(lastSong.artist, 10);

        artistView.setText(artist);
        albumTitleView.setText(title);
        songCountView.setText(songFormatUtil.songCount(songs.size()));

        Picasso.with(appContext)
                .load(songFormatUtil.getAlbumArtWorkUri(lastSong.albumId))
                .centerCrop()
                .placeholder(R.drawable.art_2)
                .fit()
                .into(albumCover);

//        albumView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlbumClickListener(v, position);
//            }
//        });

        return albumView;
    }

    private void AlbumClickListener(View v, final int position) {
        ReplayEventBus.getInstance().post(position);

        new ViewAnimatiorUtil(appContext).rotateY(v, 500)
                .addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ReplayEventBus.getInstance().post(new SelectedAlbumEvent(albums.get(position)));
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
