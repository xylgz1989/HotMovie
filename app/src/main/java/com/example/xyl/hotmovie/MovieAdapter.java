package com.example.xyl.hotmovie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * the adapter show the movie poster
 * Created by xyl on 2017/1/8 0008.
 */

public class MovieAdapter extends BaseAdapter {
    private Context mCtx;
    private List<MovieBean> movies;

    public MovieAdapter(Context mCtx, List<MovieBean> movies) {
        this.mCtx = mCtx;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainViewHolder holder = null;
        if(convertView == null){
            holder = new MainViewHolder();
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_main,parent,false);
            holder.iv_poster = (ImageView) convertView.findViewById(R.id.iv_movie);
            convertView.setTag(holder);
        }else{
            holder = (MainViewHolder) convertView.getTag();
        }
        String posterUrl = movies.get(position).getPosterUrl();
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(BuildConfig.MOVIEDB_POSTER_BASE_URL).append(mCtx.getString(R.string.screen_width));
        sbuf.append(posterUrl);
        Log.v("MovieAdapter","poster url="+sbuf.toString());
        Picasso.with(mCtx)
                .load(sbuf.toString())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.iv_poster, new Callback() {
                    @Override
                    public void onSuccess() {
//                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
//                        Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        return convertView;
    }

    class MainViewHolder{
        ImageView iv_poster;
    }
}
