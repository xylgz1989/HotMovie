package com.example.xyl.hotmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by xyl on 2017/1/22 0022.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT　NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT　NULL, " +
                MovieContract.MovieEntry.COLUMN_RATED_AVER + " REAL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT　NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT　NULL, " +
                MovieContract.MovieEntry.COLUMN_RUN_TIME + " INTEGER , " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " INTEGER , " +
                MovieContract.MovieEntry.COLUMN_IS_LIKE + " INTEGER" +//应加上初始值--初始值应为不喜欢
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        //Question:
        //1.should we create a table to save movie reviews?
        //2.should we create a table to save movie related videos json or entity?
        //3.?
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
