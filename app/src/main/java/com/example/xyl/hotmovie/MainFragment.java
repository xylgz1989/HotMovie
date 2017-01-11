package com.example.xyl.hotmovie;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.xyl.tool.asyncInterface.AsyncTaskCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * the fragment display the movies
 * Created by xyl on 2017/1/4 0004.
 */

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private GridView gv_main;
    private static final String TAG = "MainFragment";
    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean sortPrefChanged = PreferenceTool.getBoolean(getActivity(),
                getString(R.string.sort_pref_changed),false);
        // sort order has changed or the movies list is empty
        if(sortPrefChanged || gv_main.getAdapter() == null) {
            // fetch the movie list
            refreshMovieInfos();
            PreferenceTool.setBoolean(getActivity(),getString(
                    R.string.sort_pref_changed),false);
        } else {
            // do nothing
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        if (menu != null) {
            menu.clear();
        }
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
            GetMovieTask task = new GetMovieTask(
                    getActivity(),new FetchMovieTaskCompleteListener());
            task.execute("");
            pd = new ProgressDialog(getActivity());
            pd.setMessage(getString(R.string.dialog_query_movie));
            pd.show();
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

    class FetchMovieTaskCompleteListener implements AsyncTaskCompleteListener{

        final String MOVIES_RESULT = "results";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String TITLE = "title";
        final String RATED_AVER = "vote_average";
        final String OVERVIEW = "overview";

        @Override
        public void onTaskComplete(Object result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            List<MovieBean> movies = null;
            try {
                if(result instanceof String){
                    String jsonStr = (String) result;
                    movies = parseMovieInfoFromJson(jsonStr);
                }
            } catch (JSONException e) {
                Log.e(TAG,"parse json failed",e);
            }
            if (movies != null) {
                BaseAdapter adapter = new MovieAdapter(getActivity(),movies);
                gv_main.setAdapter(adapter);
            }
        }

        @Override
        public void onTaskFailed() {

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
