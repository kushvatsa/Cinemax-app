package com.example.kush.cinemax.network_sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class Movie_SyncAdapterService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static Movie_SyncAdapter sMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MovieSyncService", "onCreate - MovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMovieSyncAdapter == null) {
                sMovieSyncAdapter = new Movie_SyncAdapter(getApplicationContext(), true);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
