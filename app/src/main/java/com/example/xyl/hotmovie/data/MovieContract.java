package com.example.xyl.hotmovie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by xyl on 2017/1/22 0022.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.xyl.hotmovie";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String TOP_RATED = "top_rated";

    public static final String POPULAR = "popular";

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final int IS_LIKED = 0;

        public static final int NOT_LIKED = 1;

        public static final String TABLE_NAME = "movie";

        public static Uri buildMovieItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static final Uri buildQueryMovieUriByType() {
            Uri newUri = CONTENT_URI.buildUpon().appendPath("type").build();
            return newUri;
        }

        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUN_TIME = "run_time";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATED_AVER = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_IS_LIKE = "is_like";

    }

}
