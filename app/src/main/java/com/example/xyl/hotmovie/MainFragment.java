package com.example.xyl.hotmovie;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.xyl.tool.NetworkTool;
import com.xyl.tool.PreferenceTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * the fragment display the movies
 * Created by xyl on 2017/1/4 0004.
 */

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private GridView gv_main;
    private static final String TAG = "MainFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(gv_main.getAdapter() == null)
        refreshMovieInfos();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
Intent itn = new Intent(getActivity(),SettingActivity.class);
                startActivity(itn);
                return true;
            case R.id.action_refresh:
                refreshMovieInfos();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMovieInfos() {
        if(NetworkTool.isOnline(getActivity())){
            GetMovieTask task = new GetMovieTask();
            task.execute("");
        }else{
            Toast.makeText(getActivity(), R.string.no_available_network,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_main,container,false);
        gv_main = (GridView) mView.findViewById(R.id.gv_main);
        gv_main.setOnItemClickListener(this);
        return mView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieBean movie = (MovieBean) gv_main.getItemAtPosition(position);

        Intent itn = new Intent(getActivity(),MovieDetailActivity.class);
        itn.putExtra(getString(R.string.movie_key),movie);
        startActivity(itn);
    }



    class GetMovieTask extends AsyncTask<String,Void,String>{
        private ProgressDialog pd;
        final String LANGUAGE_PARAM = "language";
        final String KEY_PARAM = "api_key";
        String queryType = null;
        String languageValue = null;
        private HttpURLConnection urlConnection;
        private String movieInfoJson;
        private BufferedReader reader;

        final String MOVIES_RESULT = "results";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String TITLE = "title";
        final String RATED_AVER = "vote_average";
        final String OVERVIEW = "overview";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            queryType = PreferenceTool.getString(getActivity(),getString(
                    R.string.pref_sort_key),getString(R.string.pref_sort_value_popular));
            languageValue = PreferenceTool.getString(getActivity(),getString(
                    R.string.pref_lang_key),getString(R.string.pref_lang_value_default));

            pd = new ProgressDialog(getActivity());
            pd.setMessage(getString(R.string.dialog_query_movie));
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Uri builtUri = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon().
                    appendPath(queryType).
                    appendQueryParameter(LANGUAGE_PARAM,languageValue).
                    appendQueryParameter(KEY_PARAM,BuildConfig.MOVIEDB_API_KEY).
                    build();

            try {
                URL url = new URL(builtUri.toString()/*.concat(apiKey)*/);
                Log.w(TAG,"build url="+builtUri.toString());
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
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            List<MovieBean> movies = null;
            try {
                movies = parseMovieInfoFromJson(s);
            } catch (JSONException e) {
                Log.e(TAG,"parse json failed",e);
            }
            if (movies != null) {
                BaseAdapter adapter = new MovieAdapter(getActivity(),movies);
                gv_main.setAdapter(adapter);
            }
        }

        private List<MovieBean> parseMovieInfoFromJson(String jsonStr) throws JSONException {
            List<MovieBean> movies = null;
            if(!TextUtils.isEmpty(jsonStr)){
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray movieArray = jsonObj.getJSONArray(MOVIES_RESULT);
                movies = new ArrayList<>();
                for (int i = 0; i < movieArray.length(); i++) {
                    MovieBean movie = new MovieBean();
                    JSONObject movieJson = movieArray.getJSONObject(i);
                    movie.setMovieName(movieJson.getString(TITLE));
                    movie.setScore((float) movieJson.getDouble(RATED_AVER));
                    movie.setFirstShowTime(movieJson.getString(RELEASE_DATE));
                    movie.setIntroduction(movieJson.getString(OVERVIEW));
                    movie.setPosterUrl(movieJson.getString(POSTER_PATH));
                    movies.add(movie);
                }
                return movies;
            }
            return null;
        }
    }
}
