package com.example.xyl.hotmovie.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by xyl on 2017/3/29 0029.
 */

public class MovieAuthenticatorService extends Service {
    private MovieAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new MovieAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
