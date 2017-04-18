package com.example.xyl.hotmovie.detail.comment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.entity.CommentBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xyl.tool.DividerItemDecoration;
import com.xyl.tool.FullyLinearLayoutManager;
import com.xyl.tool.asyncInterface.AsyncTaskCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xyl on 2017/3/2 0002.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CommentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView rv_comments;
    //    private ListView lv_comments;
//    private BaseAdapter commentAdapter;
    private List<CommentBean> comments;
    private int currentPage = 1;
    private int totalPage;
    private int movieId;
    private static final String TAG = "CommentsFragment";
    private ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.frag_comments,container,false);
//        mSwipeRefreshWidget = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh_widget);
//        lv_comments = (ListView) mView.findViewById(lv_comments);
        rv_comments = (RecyclerView) mView.findViewById(R.id.rv_comments);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if(getArguments() != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    movieId = getArguments().getInt(getString(R.string.movie_id_key));
                }
            }
        }
//        mSwipeRefreshWidget.setOnRefreshListener(this);

        comments = new ArrayList<>();
//        commentAdapter = new CommentAdapter(comments,getActivity());
//        lv_comments.setAdapter(commentAdapter);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
//        mSwipeRefreshWidget.setProgressViewOffset(false, 0, (int) TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                        .getDisplayMetrics()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            pd = new ProgressDialog(getActivity());
        }
        pd.setMessage(getString(R.string.loading_comments));
        pd.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetMovieReviewsTask(new OnCommentsRefreshedListener()).
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            String.valueOf(movieId),String.valueOf(currentPage));
        }
    }

    @Override
    public void onRefresh() {

    }

    class OnCommentsRefreshedListener implements AsyncTaskCompleteListener{
        static final String RESULTS = "results";
        static final String TOTAL_PAGE = "total_pages";
        static final String RESULT_COUNT = "total_results";

        @Override
        public void onTaskComplete(Object result) {
            if(pd != null)pd.dismiss();
            if(result != null && result instanceof String){
                String respStr = (String) result;
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                try {
                    JSONObject mainJsonObj = new JSONObject(respStr);
                    JSONArray trailerArray = mainJsonObj.getJSONArray(RESULTS);
                    int resultCount = mainJsonObj.getInt(RESULT_COUNT);
                    totalPage = mainJsonObj.getInt(TOTAL_PAGE);
                    Log.i(TAG, "onTaskComplete: result count="+resultCount+",total page="+totalPage);
                    JsonArray newArray = jsonParser.parse(
                            trailerArray.toString()).getAsJsonArray();
                    List<CommentBean> newComments = new ArrayList<>();
                    Iterator itor = newArray.iterator();
                    JsonElement jsonElement = null;
                    String jsonBean = null;
                    while (itor.hasNext()){
                        jsonElement = (JsonElement) itor.next();
                        jsonBean = jsonElement.toString();
                        CommentBean comment = gson.fromJson(jsonBean,CommentBean.class);
                        newComments.add(comment);
                    }
                    Log.i(TAG, "onTaskComplete: new comments size="+newComments.size());
                    comments.addAll(newComments);
                    if(comments.size() == 0){
                        StringBuilder sbuf = new StringBuilder();
                        sbuf.append(getString(R.string.no)).append(getString(R.string.comments));
                        Toast.makeText(getActivity(), sbuf.toString(), Toast.LENGTH_SHORT).show();
                    }
                    CommentAdapter commentAdapter = new CommentAdapter(comments);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        rv_comments.setAdapter(commentAdapter);
                    }
                    LinearLayoutManager layoutManager = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        layoutManager = new FullyLinearLayoutManager(getActivity());
                    }
                    if (layoutManager == null) throw new AssertionError();
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        rv_comments.setLayoutManager(layoutManager);
                        DividerItemDecoration dividerLine = new DividerItemDecoration(
                                DividerItemDecoration.VERTICAL);
                        dividerLine.setSize(1);
                        dividerLine.setColor(0xFFDDDDDD);
                        rv_comments.addItemDecoration(dividerLine);
                    }

                }catch (JSONException jsonExc){
                    jsonExc.printStackTrace();
                }

            }
        }

        @Override
        public void onTaskFailed() {
            if(pd != null)pd.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Toast.makeText(getActivity(),R.string.cannot_connect_server,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
