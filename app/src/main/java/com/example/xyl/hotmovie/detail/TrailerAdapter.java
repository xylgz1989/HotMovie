package com.example.xyl.hotmovie.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.entity.TrailerBean;
import com.xyl.tool.AppTool;

import java.util.List;

/**
 * Created by xyl on 2017/1/31 0031.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private List<TrailerBean> mTrailers;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_video_name);
        }
    }

    public TrailerAdapter(List<TrailerBean> trailers) {
        this.mTrailers = trailers;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_trailer,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrailerBean trailer = mTrailers.get(holder.getAdapterPosition());
                startToWatchTrailer(parent.getContext(),trailer.getKey());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrailerBean trailer = mTrailers.get(position);
        holder.tv_name.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    private void startToWatchTrailer(Context ctx,String videoKey){
        String youTubePackageName = "com.google.android.youtube";
        Uri trailerUri;

        if(AppTool.isAppInstalled(ctx,youTubePackageName)){
            trailerUri = Uri.parse("vnd.youtube:" + videoKey);
        }else{
            trailerUri = Uri.parse("http://www.youtube.com/watch?v=" + videoKey);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
        ctx.startActivity(intent);
    }
}
