package io.starter.biruk.ezymusic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by biruk on 11/07/18.
 */

public class AlbumArtworkUtil {

    private Context appContext;
    private SongFormatUtil songFormatUtil;


    public AlbumArtworkUtil(Context appContext,SongFormatUtil songFormatUtil) {
        this.appContext = appContext;
        this.songFormatUtil = songFormatUtil;
    }

    public Bitmap getBitMap(long albumId){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(appContext.getContentResolver(),songFormatUtil.getAlbumArtWorkUri(albumId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
