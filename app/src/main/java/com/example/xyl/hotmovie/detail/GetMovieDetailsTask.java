package com.example.xyl.hotmovie.detail;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.xyl.hotmovie.BuildConfig;
import com.example.xyl.hotmovie.entity.TrailerBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xyl.tool.asyncInterface.AsyncTaskCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Get different types of movie detail,depend on the value that init the constructor
 * Created by xyl on 2017/1/19 0019.
 */

public class GetMovieDetailsTask extends AsyncTask<String,Void,String> {
    public static final String MOVIE_DURATION = "duration";
    public static final String PREVIEW_VIDEO = "videos";
    public static final String RESULTS = "results";

    private String mDataType;
    private static final String TAG = "GetMovieDetailsTask";
    public final String LANGUAGE_PARAM = "language";
    public final String KEY_PARAM = "api_key";
    public static final String RUNTIME = "runtime";
    private AsyncTaskCompleteListener mCompleteListener;

    public GetMovieDetailsTask(String dataType,AsyncTaskCompleteListener completeListener) {
        this.mDataType = dataType;
        this.mCompleteListener = completeListener;
    }

    //    videos
//    https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=<<api_key>>&language=en-US
//
//    reviews
//    https://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key=<<api_key>>&language=en-US&page=1
//
//    detail
//    https://api.themoviedb.org/3/movie/{movie_id}?api_key=<<api_key>>&language=en-US
    @Override
    protected String doInBackground(String... params) {
        StringBuilder sbuf = new StringBuilder(BuildConfig.MOVIEDB_BASE_URL);
        if (!TextUtils.isEmpty(params[0])){
            sbuf.append(params[0]);
            if (!mDataType.equals(MOVIE_DURATION)){
                sbuf.append("/").append(PREVIEW_VIDEO).append("?");
            }else{
                sbuf.append("?");
            }
        }
        sbuf.append(KEY_PARAM).append("=").append(BuildConfig.MOVIEDB_API_KEY).append("&").
                append(LANGUAGE_PARAM).append("=").append("en-US");
        Log.i(TAG,"detail url="+sbuf.toString());
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return respStr;
    }

    @Override
    protected void onPostExecute(String respStr) {
        super.onPostExecute(respStr);
        if(!TextUtils.isEmpty(respStr) && mCompleteListener != null){
            Gson gson = new Gson();
            if(mDataType.equals(PREVIEW_VIDEO)){
                JsonParser jsonParser = new JsonParser();
                try {
                    JSONObject mainJsonObj = new JSONObject(respStr);
                    JSONArray trailerArray = mainJsonObj.getJSONArray(RESULTS);
                    JsonArray newArray = jsonParser.parse(
                            trailerArray.toString()).getAsJsonArray();
                    List<TrailerBean> trailers = new ArrayList<>();
                    Iterator itor = newArray.iterator();
                    JsonElement jsonElement = null;
                    String jsonBean = null;
                    while (itor.hasNext()){
                        jsonElement = (JsonElement) itor.next();
                        jsonBean = jsonElement.toString();
                        TrailerBean trailer = gson.fromJson(jsonBean,TrailerBean.class);
                        trailers.add(trailer);
                    }

                    mCompleteListener.onTaskComplete(trailers);
                    Log.v(TAG,"parse preview video info ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(mDataType.equals(MOVIE_DURATION)){
                //just get movie run time only
                try {
                    JSONObject jsonObj = new JSONObject(respStr);
                    int runTime = jsonObj.getInt(RUNTIME);
                    mCompleteListener.onTaskComplete(runTime);
                    Log.v(TAG,"parse movie run time ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{
            mCompleteListener.onTaskFailed();
        }
    }
}
