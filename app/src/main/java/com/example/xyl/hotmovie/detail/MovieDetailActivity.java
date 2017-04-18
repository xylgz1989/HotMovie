package com.example.xyl.hotmovie.detail;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.xyl.hotmovie.R;

/**
 * the container activity to display the detail of the movie
 * Created by xyl on 2017/1/7 0007.
 */

public class MovieDetailActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(getIntent() != null){
            int insertId = getIntent().getIntExtra(getString(R.string.insert_id_key),0);
            int movieId = getIntent().getIntExtra(getString(R.string.movie_id_key),0);
            Bundle args = new Bundle();
            args.putInt(getString(R.string.insert_id_key),insertId);
            args.putInt(getString(R.string.movie_id_key),movieId);
            Fragment detailFrag = new MovieDetailFragment();
            detailFrag.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_detail_container,detailFrag).commit();
        }
    }




}
