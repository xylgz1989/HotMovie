package com.example.xyl.hotmovie;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(getClass().getSimpleName(),"on create");
        FragmentManager fragMgr = getFragmentManager();
        if(fragMgr.findFragmentByTag("main") == null){
            fragMgr.beginTransaction().add(R.id.container_main,
                    new MainFragment(),"main").commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
