package com.example.xyl.hotmovie.mainlist;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.xyl.hotmovie.BuildConfig;
import com.example.xyl.hotmovie.R;
import com.xyl.tool.PreferenceTool;
import com.xyl.tool.asyncInterface.AsyncTaskCompleteListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * the AsyncTask to get movie infos
 * Created by xyl on 2017/1/9 0009.
 */

public class GetMovieTask extends AsyncTask<String,Void,String>{

    public final String LANGUAGE_PARAM = "language";
    public final String KEY_PARAM = "api_key";
    public static final String PAGE = "page";
    String queryType = null;
    String languageValue = null;
    private HttpURLConnection urlConnection;
    private String movieInfoJson;
    private BufferedReader reader;
    private Context mCtx;
    private AsyncTaskCompleteListener atcLis;

    public GetMovieTask(Context ctx, AsyncTaskCompleteListener atcLis) {
        this.mCtx = ctx;
        this.atcLis = atcLis;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        queryType = PreferenceTool.getString(mCtx,mCtx.getString(
                R.string.pref_sort_key),mCtx.getString(R.string.pref_sort_value_popular));
        languageValue = PreferenceTool.getString(mCtx,mCtx.getString(
                R.string.pref_lang_key),mCtx.getString(R.string.pref_lang_value_default));
    }


    //    https://api.themoviedb.org/3/movie/top_rated?api_key=<<api_key>>&language=en-US&page=1
    @Override
    protected String doInBackground(String... params) {
        Uri.Builder uriBuilder = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon().
                appendPath(queryType).
                appendQueryParameter(LANGUAGE_PARAM,languageValue).
                appendQueryParameter(KEY_PARAM,BuildConfig.MOVIEDB_API_KEY);
        if (params != null && !TextUtils.isEmpty(params[0])) {
            uriBuilder.appendQueryParameter(PAGE,params[0]);
        }
        Uri builtUri = uriBuilder.build();

        try {
            URL url = new URL(builtUri.toString()/*.concat(apiKey)*/);
//            Log.w(TAG,"build url="+builtUri.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                movieInfoJson = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                movieInfoJson = null;
            }
            movieInfoJson = buffer.toString();
            Log.v(TAG,"movie info json="+ movieInfoJson);
            return movieInfoJson;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(!TextUtils.isEmpty(s)){
            if (atcLis != null) {
                atcLis.onTaskComplete(s);
            }
        }else{
            if (atcLis != null) {
                atcLis.onTaskFailed();
            }
        }
    }
}

