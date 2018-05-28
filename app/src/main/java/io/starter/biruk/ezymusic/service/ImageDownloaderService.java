package io.starter.biruk.ezymusic.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Biruk on 10/20/2017.
 */
public class ImageDownloaderService extends Service {

    private static final String TAG = "imagedownloaderservice";
    private Binder imgBinder=new ImgBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG," onCreate");
    }

    public class ImgBinder extends Binder{
        public ImageDownloaderService getImageDlService(){
            return ImageDownloaderService.this;
        }
    }

    public void msg(){
        Log.i(TAG,"msg!!!!!!!!!!!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return imgBinder;
    }
}
