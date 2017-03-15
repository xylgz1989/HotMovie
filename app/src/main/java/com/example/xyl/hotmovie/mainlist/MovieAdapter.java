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
import com.example.xyl.hotmovie.detail.MovieDetailActivity;
import com.squareup.picasso.Picasso;

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
        return LayoutInflater.from(context).inflate(R.layout.item_main,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String posterUrl = cursor.getString(cursor.getColumnIndex(
                MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        final int movieId = cursor.getInt(cursor.getColumnIndex(
                MovieContract.MovieEntry.COLUMN_MOVIE_ID));
        final int insertId = cursor.getInt(cursor.getColumnIndex(
                MovieContract.MovieEntry._ID));

        ImageView iv_poster = (ImageView) view/*.findViewById(R.id.iv_movie)*/;
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(BuildConfig.MOVIEDB_POSTER_BASE_URL).append(context.getString(R.string.screen_width));
        sbuf.append(posterUrl);
        Picasso.with(context).load(sbuf.toString()).placeholder(R.mipmap.ic_launcher).into(iv_poster);

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
