package com.example.xyl.hotmovie.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.example.xyl.hotmovie.BuildConfig;
import com.example.xyl.hotmovie.R;
import com.example.xyl.hotmovie.data.MovieContract;
import com.example.xyl.hotmovie.entity.MovieBean;
import com.example.xyl.hotmovie.mainlist.MainActivity;
import com.xyl.tool.PreferenceTool;

import org.json.JSONArray;
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
 * Created by xyl on 2017/3/26 0026.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "MovieSyncAdapter";
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
//    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_INTERVAL = 30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    public final String LANGUAGE_PARAM = "language";
    public final String KEY_PARAM = "api_key";
    public static final String PAGE = "page";
    private ContentResolver resolver;
    private Context mCtx;
    String queryType = null;
    String languageValue = null;

    public static final int MOVIE_NOTIFICATION_ID = 3005;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mCtx = context;
        resolver = context.getContentResolver();
    }

    public MovieSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.mCtx = context;
        resolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        //此处会被安排到后台线程
        Log.i(TAG, "onPerformSync: start");

        //把GetMovieTask的相关方法放到这里
        Uri.Builder uriBuilder = Uri.parse(BuildConfig.MOVIEDB_BASE_URL).buildUpon().
                appendPath(queryType).
                appendQueryParameter(LANGUAGE_PARAM,languageValue).
                appendQueryParameter(KEY_PARAM,BuildConfig.MOVIEDB_API_KEY);
        int pageNum = 1;
        if(extras.getInt(getContext().getString(R.string.page_num),-1) != -1){
            pageNum = extras.getInt(getContext().getString(R.string.page_num));
            uriBuilder.appendQueryParameter(PAGE,String.valueOf(pageNum));
        }
        Uri builtUri = uriBuilder.build();

        // Read the input stream into a String
        InputStream inputStream = null;
        StringBuilder buffer = new StringBuilder();
        String movieInfoJson;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(builtUri.toString()/*.concat(apiKey)*/);
//            Log.w(TAG,"build url="+builtUri.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
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
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                movieInfoJson = null;
            }
            movieInfoJson = buffer.toString();
            //把解析JSON的方法也放到这里
            if (!TextUtils.isEmpty(movieInfoJson)){
                Log.i(TAG, "onPerformSync: start to parse json");
                parseMovieJsonString(movieInfoJson);
                if(pageNum == 1){
                    notifyMovie();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }



    private void parseMovieJsonString(String jsonStr){
        final String MOVIES_RESULT = "results";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String TITLE = "title";
        final String RATED_AVER = "vote_average";
        final String ID = "id";
        final String OVERVIEW = "overview";

        JSONObject jsonObj = null;
        List<MovieBean> movies = null;
        try{
            jsonObj = new JSONObject(jsonStr);
            JSONArray movieArray = jsonObj.getJSONArray(MOVIES_RESULT);
            movies = new ArrayList<>();
            MovieBean movie = null;
            for (int i = 0; i < movieArray.length(); i++) {
                movie = new MovieBean();
                JSONObject movieJson = movieArray.getJSONObject(i);
                movie.setMovieName(movieJson.getString(TITLE));
                movie.setScore((float) movieJson.getDouble(RATED_AVER));
                movie.setFirstShowTime(movieJson.getString(RELEASE_DATE));
                movie.setIntroduction(movieJson.getString(OVERVIEW));
                movie.setPosterUrl(movieJson.getString(POSTER_PATH));
                movie.setMovieId(movieJson.getInt(ID));
                movies.add(movie);
            }
            //批量insert
            ContentValues[] insertDatas = new ContentValues[movies.size()];
            ContentValues insertData = null;
            for (int i = 0; i < insertDatas.length; i++) {
                movie = movies.get(i);
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
            if(insertDatas.length > 0){
                resolver.bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI, insertDatas);
            }

        }catch (Exception e){

        }
    };

    private void notifyMovie() {
        String enableNotificationKey = getContext().getString(R.string.pref_movie_rank_notification_switch_key);
        boolean needNotify = PreferenceTool.getBoolean(getContext(),enableNotificationKey,false);
        if(needNotify){
            String lastSyncTimeKey = getContext().getString(R.string.pref_last_sync_time);
            long lastNotifyTime = PreferenceTool.getLong(getContext(),lastSyncTimeKey,0);
            if(System.currentTimeMillis() - lastNotifyTime > DAY_IN_MILLIS){
                //send notification if the most popular movie_id has changed
                int lastHottestMovieId = PreferenceTool.getInt(getContext(),
                        getContext().getString(R.string.pref_last_hot_movie_id),0);
                //query the most popular movie after sync
                Uri typeUri = MovieContract.MovieEntry.buildQueryMovieUriByType();
                String[] projection = new String[]{
                        MovieContract.MovieEntry._ID,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID};
                String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC ";
                Cursor cursor = getContext().getContentResolver().query(typeUri,projection,
                        null, null,sortOrder);
                int currentHottestMovieId = 0;
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    currentHottestMovieId = cursor.getInt(cursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                }

                //if the most popular movie has changed,send notification
                if(currentHottestMovieId != lastHottestMovieId){
                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications.  Just throw in some data.
                    String title = getContext().getString(R.string.app_name);
                    int iconId = R.mipmap.ic_launcher;
                    String contentText = null;
                    Context context = getContext();

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
//                                .setColor(resources.getColor(R.color.sunshine_light_blue))
                                    .setSmallIcon(iconId)
//                                .setLargeIcon(largeIcon)
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(MOVIE_NOTIFICATION_ID, mBuilder.build());
                    PreferenceTool.setLong(getContext(),lastSyncTimeKey,System.currentTimeMillis());
                }


            }
        }
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        Log.i(TAG, "configurePeriodicSync....");
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context,int pageNum) {
        Account account = getSyncAccount(context);
        if (ContentResolver.isSyncPending(account, context.getString(R.string.content_authority))
                || ContentResolver.isSyncActive(account, context.getString(R.string.content_authority))) {

            Log.i("ContentResolver", "SyncPending, canceling");
            ContentResolver.cancelSync(account, context.getString(R.string.content_authority));
        }
        Log.i(TAG, "syncImmediately: start");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(context.getString(R.string.page_num),pageNum);
        Log.i(TAG, "syncImmediately: account="+account.toString());
        ContentResolver.requestSync(account,
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.i(TAG, "onAccountCreated: start");
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context,1);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
