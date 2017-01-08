package com.example.xyl.hotmovie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

/**
 * the activity display the detail of the movie
 * Created by xyl on 2017/1/7 0007.
 */

public class MovieDetailActivity extends Activity {
    private ImageView iv_poster;
    private TextView tv_score;
    private RatingBar rb_score;
    private TextView tv_showTime;
    private TextView tv_actors;
    private TextView tv_introduce;
    private TextView tv_title;

    private static final String TAG = "MovieDetailActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        initViews();
    }

    private void initViews() {
        tv_actors = (TextView) findViewById(R.id.tv_actors);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_showTime = (TextView) findViewById(R.id.tv_show_time);
        iv_poster = (ImageView) findViewById(R.id.iv_movie_poster);
        rb_score = (RatingBar) findViewById(R.id.rb_score);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showMovieDetail();
    }

    private void showMovieDetail() {
        if(getIntent() != null &&
                getIntent().getSerializableExtra(getString(R.string.movie_key)) != null){
            Serializable ser = getIntent().getSerializableExtra(getString(R.string.movie_key));
            if(ser instanceof MovieBean){
                MovieBean movie = (MovieBean)ser;
                Log.i(TAG,"movie info="+movie.toString());
                StringBuilder scoreBuf = new StringBuilder();
                scoreBuf.append(String.valueOf(movie.getScore()));
                scoreBuf.append("/10");
                tv_score.setText(scoreBuf);
                tv_introduce.setText(movie.getIntroduction());
                tv_showTime.setText(movie.getFirstShowTime());
                tv_title.setText(movie.getMovieName());
                String posterUrl = movie.getPosterUrl();
                StringBuilder sbuf = new StringBuilder();
                //the screen width should not use the same value,it should depend on cell phone
                sbuf.append(BuildConfig.MOVIEDB_POSTER_BASE_URL).append(getString(R.string.screen_width));
                sbuf.append(posterUrl);
                Picasso.with(this)
                        .load(sbuf.toString())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(iv_poster);
            }
        }
    }
}
