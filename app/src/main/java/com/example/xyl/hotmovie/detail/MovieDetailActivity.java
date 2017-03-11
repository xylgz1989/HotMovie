package com.example.xyl.hotmovie.detail;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.xyl.hotmovie.BuildConfig;
import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.data.MovieContract;
import com.example.xyl.hotmovie.detail.comment.CommentsFragment;
import com.example.xyl.hotmovie.entity.TrailerBean;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.xyl.tool.FileUtil;
import com.xyl.tool.NetworkTool;
import com.xyl.tool.asyncInterface.AsyncTaskCompleteListener;

import java.io.File;
import java.util.List;

/**
 * the activity display the detail of the movie
 * Created by xyl on 2017/1/7 0007.
 */

public class MovieDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationTabStrip.OnTabStripSelectedIndexListener {
    private ImageView iv_poster;
    private TextView tv_score;
    private RatingBar rb_score;
    private TextView tv_showTime;
    private TextView tv_actors;
    private TextView tv_introduce;
    private TextView tv_title;
    private TextView tv_runtime;
    private RecyclerView rv_trailer;
    private LikeButton btn_like;
    private static final int MOVIE_DETAIL_LOADER = 158;
    private int insertId;
    private int movieId;
    private int likeStatus;
    private NavigationTabStrip tabStrip;
    private static final String TAG = "MovieDetailActivity";
    public static final String OVERVIEW_FRAGMENT = "over_view";
    public static final String REVIEWS_FRAGMENT = "reviews";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        initViews();
        showMovieDetail();
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER,null,this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fragMgr = getFragmentManager();
        FragmentTransaction transaction = fragMgr.beginTransaction();
        Bundle args = new Bundle();
        args.putInt(getString(R.string.movie_id_key),movieId);
        args.putInt(getString(R.string.insert_id_key),insertId);
        Fragment overviewFrag = null;
        if(fragMgr.findFragmentByTag(OVERVIEW_FRAGMENT) == null){
            overviewFrag = new OverviewFragment();
            overviewFrag.setArguments(args);
            transaction.add(R.id.container_detail,overviewFrag,OVERVIEW_FRAGMENT).commit();
        }else{
            overviewFrag = fragMgr.findFragmentByTag(OVERVIEW_FRAGMENT);
            transaction.show(overviewFrag).commit();
        }
        tabStrip.setOnTabStripSelectedIndexListener(this);
//        Fragment commentsFragment = new CommentsFragment();
//        commentsFragment.setArguments(args);
//        transaction.add(R.id.container_detail,commentsFragment,REVIEWS_FRAGMENT).commit();
    }

    private void initViews() {
        tv_actors = (TextView) findViewById(R.id.tv_actors);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);

        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_showTime = (TextView) findViewById(R.id.tv_show_time);
        iv_poster = (ImageView) findViewById(R.id.iv_movie_poster);
        rb_score = (RatingBar) findViewById(R.id.rb_score);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_runtime = (TextView) findViewById(R.id.tv_run_time);
        rv_trailer = (RecyclerView) findViewById(R.id.rv_trailer);
        btn_like = (LikeButton) findViewById(R.id.btn_like);
        btn_like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                setLikedStatus();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                setLikedStatus();
            }
        });
        tabStrip = (NavigationTabStrip) findViewById(R.id.tab_frags);
        tabStrip.setTitles(R.string.overview,R.string.comments);
        tabStrip.setTabIndex(0, true);
        tabStrip.setTitleSize(20);
        tabStrip.setStripColor(Color.RED);
        tabStrip.setStripWeight(6);
        tabStrip.setStripFactor(2);
        tabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        tabStrip.setStripGravity(NavigationTabStrip.StripGravity.BOTTOM);
        tabStrip.setTypeface("fonts/typeface.ttf");
        tabStrip.setCornersRadius(3);
        tabStrip.setAnimationDuration(300);
        tabStrip.setInactiveColor(Color.GRAY);
        tabStrip.setActiveColor(Color.RED);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_trailer.setLayoutManager(layoutManager);
    }

    private void showMovieDetail() {
        if(getIntent() != null){
            movieId = getIntent().getIntExtra(getString(R.string.movie_id_key),0);
            insertId = getIntent().getIntExtra(getString(R.string.insert_id_key),0);
            if(NetworkTool.isOnline(this)){
                new GetMovieDetailsTask(GetMovieDetailsTask.PREVIEW_VIDEO, new TrailerTaskListener()).
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(movieId));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(MOVIE_DETAIL_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = MovieContract.MovieEntry.buildMovieItemUri(insertId);
        Log.v(TAG, "onCreateLoader: movieId="+movieId+",insertId="+insertId+",queryUri="+movieUri.toString());
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        return new CursorLoader(this,movieUri,null,selection,
                new String[]{String.valueOf(movieId)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0) {
            data.moveToFirst();
            likeStatus = data.getInt(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_IS_LIKE));
            if(likeStatus == MovieContract.MovieEntry.IS_LIKED){
                btn_like.setLiked(true);
            }else{
                btn_like.setLiked(false);
            }

            StringBuilder scoreBuf = new StringBuilder();
            float score = data.getFloat(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_RATED_AVER));
            scoreBuf.append(String.valueOf(score));
            scoreBuf.append("/10");
            tv_score.setText(scoreBuf);

            String firstShowTime = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            tv_showTime.setText(firstShowTime);

            String title = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_TITLE));
            tv_title.setText(title);

//            String introduction = data.getString(data.getColumnIndex(
//                    MovieContract.MovieEntry.COLUMN_OVERVIEW));
//            tv_introduce.setText(introduction);

            int runTime = data.getInt(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_RUN_TIME));
            Log.v(TAG, "onLoadFinished: runtime=" + runTime);
            if (runTime == 0) {
                tv_runtime.setText("--" + getString(R.string.unit_minute));
                if (NetworkTool.isOnline(this)) {
                    new GetMovieDetailsTask(GetMovieDetailsTask.MOVIE_DURATION,new MovieRuntimeTaskListener()).
                            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,String.valueOf(movieId));
                }
            } else {
                tv_runtime.setText(String.valueOf(runTime) + getString(R.string.unit_minute));
            }

            String posterUrl = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            Picasso mPo = Picasso.with(this);
            RequestCreator requestCreator = null;
            String picName = posterUrl.substring(1);
            Context mCtx = this;
            File cachedImg = new File(FileUtil.getAvailableCacheDir(mCtx), picName);
            if (cachedImg.exists() && cachedImg.canRead()) {
                //if cache image file exists,load local file
                requestCreator = mPo.load(cachedImg);
                Log.v(TAG, "load poster from cached file");
            } else {
                //or load from server
                StringBuilder sbuf = new StringBuilder();
                sbuf.append(BuildConfig.MOVIEDB_POSTER_BASE_URL).append(mCtx.getString(R.string.screen_width));
                sbuf.append(posterUrl);
                Log.v(TAG, "poster url=" + sbuf.toString());
                requestCreator = mPo.load(sbuf.toString());
            }
            requestCreator.placeholder(R.mipmap.ic_launcher).into(iv_poster);
        }else{
            //should display something if no movie detail

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onStartTabSelected(String title, int index) {

    }

    @Override
    public void onEndTabSelected(String title, int index) {
        FragmentManager fragMgr = getFragmentManager();
        FragmentTransaction transaction = fragMgr.beginTransaction();
        Bundle args = new Bundle();
        args.putInt(getString(R.string.movie_id_key),movieId);
        args.putInt(getString(R.string.insert_id_key),insertId);
        Fragment frag = null;
        switch (index){
            case 0:
                if (fragMgr.findFragmentByTag(OVERVIEW_FRAGMENT) != null){
                    frag = fragMgr.findFragmentByTag(OVERVIEW_FRAGMENT);
                }else{
                    frag = new OverviewFragment();
                    frag.setArguments(args);
                }
                break;
            case 1:
                Fragment overviewFrag = null;
                if (fragMgr.findFragmentByTag(OVERVIEW_FRAGMENT) != null){
                    overviewFrag = fragMgr.findFragmentByTag(OVERVIEW_FRAGMENT);
                    transaction.hide(overviewFrag);
                }
                if (fragMgr.findFragmentByTag(REVIEWS_FRAGMENT) != null){
                    frag = fragMgr.findFragmentByTag(REVIEWS_FRAGMENT);
                }else{
                    frag = new CommentsFragment();
                    frag.setArguments(args);
                    transaction.add(R.id.container_detail,frag);
                }
                break;
        }
        transaction.show(/*R.id.container_detail,*/frag).commit();
    }

    class MovieRuntimeTaskListener implements AsyncTaskCompleteListener{

        @Override
        public void onTaskComplete(Object result) {
            if (result != null && result instanceof Integer) {
                Uri movieUri = MovieContract.MovieEntry.buildMovieItemUri(insertId);
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
                ContentValues cv = new ContentValues();
                int runTime = (int) result;
                cv.put(MovieContract.MovieEntry.COLUMN_RUN_TIME,runTime);
                getContentResolver().update(movieUri, cv, selection,
                        new String[]{String.valueOf(movieId)});
                tv_runtime.setText(result.toString()+getString(R.string.unit_minute));
            }
        }

        @Override
        public void onTaskFailed() {

        }
    }

    class TrailerTaskListener implements AsyncTaskCompleteListener{

        @Override
        public void onTaskComplete(Object result) {
            if (result != null && result instanceof List) {
                List<TrailerBean> trailers = (List<TrailerBean>) result;
                TrailerAdapter adapter = new TrailerAdapter(trailers);
                rv_trailer.setAdapter(adapter);
            }
        }

        @Override
        public void onTaskFailed() {

        }
    }

    private void setLikedStatus(){
        int newStatus = -1;
        switch (likeStatus){
            case MovieContract.MovieEntry.IS_LIKED:
                newStatus = MovieContract.MovieEntry.NOT_LIKED;
                break;
            case MovieContract.MovieEntry.NOT_LIKED:
                newStatus = MovieContract.MovieEntry.IS_LIKED;
                break;
        }
        Uri movieUri = MovieContract.MovieEntry.buildMovieItemUri(insertId);
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_IS_LIKE,newStatus);
        getContentResolver().update(movieUri, cv, selection,
                new String[]{String.valueOf(movieId)});
        likeStatus = newStatus;
        getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER,null,this);
    }
}
