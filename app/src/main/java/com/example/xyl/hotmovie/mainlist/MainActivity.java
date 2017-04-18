package com.example.xyl.hotmovie.mainlist;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.sync.MovieSyncAdapter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(getClass().getSimpleName(),"on create");
        FragmentManager fragMgr = getFragmentManager();
        if(findViewById(R.id.container_main) != null){
            if(savedInstanceState == null){
//                fragMgr.findFragmentByTag("main") == null)
                fragMgr.beginTransaction().replace(R.id.container_main,
                        new MainFragment(),"main").commit();
            }
            MovieSyncAdapter.initializeSyncAdapter(this);
        }

        }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
