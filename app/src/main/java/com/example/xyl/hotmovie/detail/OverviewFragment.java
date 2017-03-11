package com.example.xyl.hotmovie.detail;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.data.MovieContract;

/**
 * Created by xyl on 2017/3/2 0002.
 */

public class OverviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int OVERVIEW_LOADER = 189;
    private TextView tv_overview;
    private long insertId;
    private int movieId;
    private static final String TAG = "OverviewFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.frag_overview,container,false);
        tv_overview = (TextView) mView.findViewById(R.id.tv_overview);
//        Log.i(TAG, "onCreateView: finished");
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments() != null){
            movieId = getArguments().getInt(getString(R.string.movie_id_key),0);
            insertId = getArguments().getInt(getString(R.string.insert_id_key),0);
        }
        getLoaderManager().initLoader(OVERVIEW_LOADER,null,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(OVERVIEW_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = MovieContract.MovieEntry.buildMovieItemUri(insertId);
        Log.v(TAG, "onCreateLoader: movieId="+movieId+",insertId="+insertId+",queryUri="+movieUri.toString());
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        return new CursorLoader(getActivity(),movieUri,null,selection,
                new String[]{String.valueOf(movieId)}, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.i(TAG, "onLoadFinished: data count="+data.getCount());
        if(data != null && data.getCount() > 0){
            data.moveToFirst();
            String introduction = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_OVERVIEW));
            tv_overview.setText(introduction);
        }
    }
}
