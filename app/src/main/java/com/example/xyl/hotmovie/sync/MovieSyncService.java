package com.example.xyl.hotmovie.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by xyl on 2017/3/26 0026.
 */

public class MovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter sMovieSyncAdapter = null;
    private static final String TAG = "MovieSyncService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: start");
        if(sMovieSyncAdapter == null){
            sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(),true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: start");
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
