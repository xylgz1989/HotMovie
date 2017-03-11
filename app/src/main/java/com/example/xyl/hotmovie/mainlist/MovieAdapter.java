package com.example.xyl.hotmovie.mainlist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.xyl.hotmovie.BuildConfig;
import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.data.MovieContract;
import com.example.xyl.hotmovie.data.cache.ImageCacheTask;
import com.example.xyl.hotmovie.detail.MovieDetailActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.xyl.tool.FileUtil;

import java.io.File;

/**
 * the adapter show the movie poster
 * Created by xyl on 2017/1/8 0008.
 */

public class MovieAdapter extends CursorAdapter {
    private static final String TAG = "MovieAdapter";
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mainView = LayoutInflater.from(context).inflate(R.layout.item_main,parent,false);
        return mainView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
//        String posterUrl = movies.get(position).getPosterUrl();
        String posterUrl = cursor.getString(cursor.getColumnIndex(
                MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        final int movieId = cursor.getInt(cursor.getColumnIndex(
                MovieContract.MovieEntry.COLUMN_MOVIE_ID));
        final int insertId = cursor.getInt(cursor.getColumnIndex(
                MovieContract.MovieEntry._ID));
        Picasso mPo = Picasso.with(context);
        mPo.setIndicatorsEnabled(true);
        RequestCreator requestCreator = null;
        ImageView iv_poster = (ImageView) view/*.findViewById(R.id.iv_movie)*/;
        Log.v(TAG, "bindView: iv_poster is"+iv_poster.toString());
        final String picName = posterUrl.substring(1);
        File cachedImg = new File(FileUtil.getAvailableCacheDir(context),picName);
        if(cachedImg.exists() && cachedImg.canRead()){
            //if cache image file exists,load local file
            requestCreator = mPo.load(cachedImg);
            Log.v("MovieAdapter","load poster from cached file");
        }else{
            //or load from server
            StringBuilder sbuf = new StringBuilder();
            sbuf.append(BuildConfig.MOVIEDB_POSTER_BASE_URL).append(context.getString(R.string.screen_width));
            sbuf.append(posterUrl);
            Log.v("MovieAdapter","poster url="+sbuf.toString());
            requestCreator = mPo.load(sbuf.toString());

            //need a better way to cache the poster file
            requestCreator.into(new ImageCacheTask(picName,context));
        }

        requestCreator.fit().placeholder(R.mipmap.ic_launcher)
                .into(iv_poster, new Callback() {
                    @Override
                    public void onSuccess() {
//                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
//                        Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });

        iv_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itn = new Intent(context,MovieDetailActivity.class);
                Log.v(TAG, "onClick: send movieId="+movieId);
                itn.putExtra(context.getString(R.string.movie_id_key),movieId);
                itn.putExtra(context.getString(R.string.insert_id_key),insertId);
                context.startActivity(itn);
            }
        });
    }

    class MainViewHolder{
        ImageView iv_poster;
    }
}
