package com.example.xyl.hotmovie;

import android.app.Application;

import com.xyl.tool.CrashHandler;

/**
 * Created by xyl on 2017/2/22 0022.
 */

public class MovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
