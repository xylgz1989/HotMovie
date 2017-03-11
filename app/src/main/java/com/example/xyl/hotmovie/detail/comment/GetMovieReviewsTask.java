package com.example.xyl.hotmovie.detail.comment;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.xyl.hotmovie.BuildConfig;
import com.xyl.tool.asyncInterface.AsyncTaskCompleteListener;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by xyl on 2017/1/24 0024.
 */

public class GetMovieReviewsTask extends AsyncTask<String,Void,String> {
    public final String LANGUAGE_PARAM = "language";
    public final String KEY_PARAM = "api_key";
    public static final String REVIEWS = "reviews";
    private static final String TAG = "GetMovieReviewsTask";
    private final AsyncTaskCompleteListener mCompleteListener;

    public GetMovieReviewsTask(AsyncTaskCompleteListener completeListener) {
        this.mCompleteListener = completeListener;
    }
    //    reviews
// https://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key=<<api_key>>&language=en-US&page=1
    @Override
    protected String doInBackground(String... params) {
        StringBuilder sbuf = new StringBuilder(BuildConfig.MOVIEDB_BASE_URL);
        if (!TextUtils.isEmpty(params[0])){
            sbuf.append(params[0]).append("/");
        }
        sbuf.append(REVIEWS).append("?");
        sbuf.append(KEY_PARAM).append("=").append(BuildConfig.MOVIEDB_API_KEY).append("&").
                append(LANGUAGE_PARAM).append("=").append("en-US").append("&").
                append("page=");
        if (TextUtils.isEmpty(params[1])) {
            sbuf.append(String.valueOf(1));
        }else{
            sbuf.append(params[1]);
        }
        Log.i(TAG,"reviews url="+sbuf.toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(sbuf.toString()).build();
        okhttp3.Response response = null;
        String respStr = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                respStr = response.body().string();
                Log.v(TAG, "success resp="+respStr);
            } else {
                Log.v(TAG, "okHttp is request error");
            }
            return respStr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String respStr) {
        super.onPostExecute(respStr);
        if(!TextUtils.isEmpty(respStr) && mCompleteListener != null){
            mCompleteListener.onTaskComplete(respStr);
        }else {
            mCompleteListener.onTaskFailed();
        }
    }
}
