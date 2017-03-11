package com.example.xyl.hotmovie.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by xyl on 2017/1/22 0022.
 */

public class MovieProvider extends ContentProvider{
    private MovieDBHelper mOpenHelper;

    //    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIE_DIR_BY_SORT_TYPE = 99;

    public static final int MOVIE_DIR = 100;

    public static final int MOVIE_ITEM = 101;

//    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
//
//    static{
//        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
//
//        //This is an inner join which looks like
//        //weather INNER JOIN location ON weather.location_id = location._id
//        sWeatherByLocationSettingQueryBuilder.setTables(
//                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
//                        WeatherContract.LocationEntry.TABLE_NAME +
//                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
//                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
//                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
//                        "." + WeatherContract.LocationEntry._ID);
//    }

    //selection strings
    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME + "."
                    + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        // 2) Use the addURI function to match each of the types.  Use the constants from

        // WeatherContract to help define the types to the UriMatcher.

        matcher.addURI(authority,MovieContract.PATH_MOVIE,MOVIE_DIR);
        matcher.addURI(authority,MovieContract.PATH_MOVIE + "/type",MOVIE_DIR);
        matcher.addURI(authority,MovieContract.PATH_MOVIE + "/#",MOVIE_ITEM);
        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        SQLiteDatabase readDB = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Log.v(TAG, "query() uri="+uri.toString()+",typeNo="+match);
        switch (match){
            case MOVIE_DIR_BY_SORT_TYPE:
                retCursor = readDB.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_DIR:
                retCursor = readDB.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_ITEM:
                String column_id = uri.getPathSegments().get(1);
                retCursor = readDB.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, MovieContract.MovieEntry._ID + " = ? ",
                        new String[]{column_id}, null,null,sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (retCursor != null && getContext().getContentResolver() != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE_DIR_BY_SORT_TYPE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_DIR:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("getType() exception occur:Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        long insertId = -1;
        switch (match){
            case MOVIE_DIR:
            case MOVIE_ITEM:
                insertId = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int deletedRows = 0;
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match){
            case MOVIE_ITEM:
                deletedRows = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return deletedRows;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.v(TAG, "update: uri="+uri.toString()+",typeNo="+match);
        int updateRows = 0;
        Uri returnUri = null;
        switch (match){
            case MOVIE_ITEM:
                String column_id = uri.getPathSegments().get(1);
                updateRows = db.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{column_id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return updateRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_DIR:
                db.beginTransaction();
                int returnCount = 0;
                Cursor queryCursor = null;
                Integer movieId = -1;
                try {
                    for (ContentValues value : values) {
//                        normalizeDate(value);
                        movieId = value.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                        long _id = 0;
                        queryCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                                new String[]{MovieContract.MovieEntry._ID},
                                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",new String[]{Integer.toString(movieId)},
                                null,null,null);
                        if(queryCursor == null || !queryCursor.moveToNext()){
                            db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        }
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @TargetApi(11)
    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
