package com.xyl.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Network tool class for Android
 * Created by xyl on 2017/1/4 0004.
 */

public class NetworkTool {
    /**
     * check cellphone is online or not
     * @param ctx
     * @return true means cellphone has connected to network, false otherwise
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
