package com.example.xyl.hotmovie.detail.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.entity.CommentBean;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;


/**
 * Created by xyl on 2017/3/5 0005.
 */

public class CommentAdapter extends /*BaseAdapter */ RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private List<CommentBean> comments;
    private Context mCtx;
    private SparseBooleanArray mCollapsedStatus;
    private static final String TAG = "CommentAdapter";

    public CommentAdapter(List<CommentBean> comments/*, Context ctx*/) {
        this.comments = comments;
//        this.mCtx = ctx;
        mCollapsedStatus = new SparseBooleanArray();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ExpandableTextView expandableTextView;
        private TextView tv_author;

        public ViewHolder(View itemView) {
            super(itemView);
            expandableTextView = (ExpandableTextView) itemView.findViewById(R.id.etv_comment);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author_name);
        }
    }

//    @Override
//    public int getCount() {
//        return comments.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return comments.get(position);
//    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        CommentBean comment = comments.get(position);
        Log.i(TAG, "onBindViewHolder: comment ="+comment.toString());
        holder.expandableTextView.setText(comment.getContent(),mCollapsedStatus,position);
        holder.tv_author.setText(comment.getAuthor());
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        CommentBean comment = comments.get(position);
//        final ViewHolder viewHolder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_comment, parent, false);
//            viewHolder = new ViewHolder();
//            viewHolder.expandableTextView = (ExpandableTextView) convertView.findViewById(R.id.etv_comment);
//            viewHolder.tv_author = (TextView) convertView.findViewById(R.id.tv_author_name);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        if (comment != null) {
//            viewHolder.expandableTextView.setText(comment.getContent(), mCollapsedStatus, position);
//
//        }
//
//        return convertView;
//    }
//
//    class ViewHolder{
//        ExpandableTextView expandableTextView;
//        TextView tv_author;
//    }
}
