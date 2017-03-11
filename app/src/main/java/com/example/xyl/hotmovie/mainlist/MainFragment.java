package com.example.xyl.hotmovie.mainlist;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.GridView;
import android.widget.Toast;

import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.data.MovieContract;
import com.example.xyl.hotmovie.entity.MovieBean;
import com.example.xyl.hotmovie.setting.SettingActivity;
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

public class MainFragment extends Fragment implements
        /*AdapterView.OnItemClickListener,*/LoaderManager.LoaderCallbacks<Cursor> {
    private GridView gv_main;
    private static final String TAG = "MainFragment";
    private ProgressDialog pd;
    //    private List<MovieBean> movies;
    private MovieAdapter movieAdapter;
    public static final String DATA = "data";
    public static final int MOVIES_LOADER = 128;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getLoaderManager().initLoader(MOVIES_LOADER,null,this);
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean sortPrefChanged = PreferenceTool.getBoolean(getActivity(),
                getString(R.string.sort_pref_changed),false);
        boolean isFirstTime = PreferenceTool.getBoolean(getActivity(),
                getString(R.string.is_first),false);
        // sort order has changed or the movies list is empty
        if(sortPrefChanged || gv_main.getAdapter() == null || isFirstTime) {
            // fetch the movie list
            refreshMovieInfos();
            PreferenceTool.setBoolean(getActivity(),getString(
                    R.string.sort_pref_changed),false);
            PreferenceTool.setBoolean(getActivity(),getString(R.string.is_first),true);
        } else {
//            if(movies != null){
//                BaseAdapter adapter = new MovieAdapter(getActivity(),movies);
//                gv_main.setAdapter(adapter);
//            }
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
        String type = PreferenceTool.getString(getActivity(),
                getResources().getString(R.string.pref_sort_key),
                getResources().getString(R.string.pref_sort_value_popular));
        boolean isOnline = NetworkTool.isOnline(getActivity());
        boolean isLikeType = type.equals(getResources().getString(R.string.pref_sort_value_favourite));
        if(isOnline && !isLikeType){
            GetMovieTask task = new GetMovieTask(
                    getActivity(),new FetchMovieTaskCompleteListener());
            task.execute("");
            pd = new ProgressDialog(getActivity());
            pd.setMessage(getString(R.string.dialog_query_movie));
            pd.show();
        }else if(isLikeType || !isOnline){
            getLoaderManager().restartLoader(MOVIES_LOADER,null,this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String type = PreferenceTool.getString(getActivity(),
                getResources().getString(R.string.pref_sort_key),
                getResources().getString(R.string.pref_sort_value_popular));
        Uri typeUri = MovieContract.MovieEntry.buildQueryMovieUriByType();
        String[] projection = new String[]{
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH};
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        if(type.equals(getResources().getString(R.string.pref_sort_value_popular))){
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC ";
        }else if(type.equals(getResources().getString(R.string.pref_sort_value_toprated))){
            sortOrder = MovieContract.MovieEntry.COLUMN_RATED_AVER + " DESC ";
        }
        Cursor cur = getActivity().getContentResolver().query(typeUri,projection,selection,
                selectionArgs, sortOrder);
        movieAdapter = new MovieAdapter(getActivity(),cur,0);
        View mView = inflater.inflate(R.layout.fragment_main,container,false);
        gv_main = (GridView) mView.findViewById(R.id.gv_main);
        gv_main.setAdapter(movieAdapter);
        if(savedInstanceState != null &&
                savedInstanceState.getSerializable(DATA) != null){
        }
        return mView;
    }

    public void onPrefChanged(){
        refreshMovieInfos();
        getLoaderManager().restartLoader(MOVIES_LOADER,null,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(MOVIES_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String type = PreferenceTool.getString(getActivity(),
                getResources().getString(R.string.pref_sort_key),
                getResources().getString(R.string.pref_sort_value_popular));
        Uri typeUri = MovieContract.MovieEntry.buildQueryMovieUriByType();
        String[] projection = new String[]{
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH};
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        if(type.equals(getResources().getString(R.string.pref_sort_value_popular))){
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC ";
        }else if(type.equals(getResources().getString(R.string.pref_sort_value_toprated))){
            sortOrder = MovieContract.MovieEntry.COLUMN_RATED_AVER + " DESC ";
        }
        else if(type.equals(getResources().getString(R.string.pref_sort_value_favourite))){
            selection = MovieContract.MovieEntry.COLUMN_IS_LIKE + " = ? ";
            selectionArgs = new String[]{String.valueOf(MovieContract.MovieEntry.IS_LIKED)};
        }
        return new CursorLoader(getActivity(),typeUri,projection,selection,
                selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0){
            movieAdapter.swapCursor(data);
        }else{
            Toast.makeText(getActivity(),R.string.no_data,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    class FetchMovieTaskCompleteListener implements AsyncTaskCompleteListener{

        final String MOVIES_RESULT = "results";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String TITLE = "title";
        final String RATED_AVER = "vote_average";
        final String ID = "id";
        final String OVERVIEW = "overview";

        @Override
        public void onTaskComplete(Object result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            try {
                if(result instanceof String){
                    String jsonStr = (String) result;
                    parseMovieInfoFromJson(jsonStr);
                    getActivity().getLoaderManager().restartLoader(MOVIES_LOADER,null,
                            MainFragment.this);
                }
            } catch (JSONException e) {
                Log.e(TAG,"parse json failed",e);
            }
        }

        @Override
        public void onTaskFailed() {

        }

        private List<MovieBean> parseMovieInfoFromJson(String jsonStr) throws JSONException {
            List<MovieBean> movies = null;
            Log.i(TAG, "parseMovieInfoFromJson: start to parse");
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
                    movie.setMovieId(movieJson.getInt(ID));
                    movies.add(movie);
                }

                ContentValues[] insertDatas = new ContentValues[movies.size()];
                ContentValues insertData = null;
                for (int i = 0; i < insertDatas.length; i++) {
                    MovieBean movie = movies.get(i);
                    insertData = new ContentValues();
                    insertData.put(MovieContract.MovieEntry.COLUMN_TITLE,movie.getMovieName());
                    insertData.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie.getMovieId());
                    insertData.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,movie.getFirstShowTime());
                    insertData.put(MovieContract.MovieEntry.COLUMN_RATED_AVER,movie.getScore());
                    insertData.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,movie.getPosterUrl());
                    insertData.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,movie.getIntroduction());
                    insertData.put(MovieContract.MovieEntry.COLUMN_IS_LIKE, MovieContract.MovieEntry.NOT_LIKED);
                    insertDatas[i] = insertData;
                }
                getActivity().getContentResolver().bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI, insertDatas);
                return movies;
            }
            return null;
        }
    }


}
