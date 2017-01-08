package com.xyl.tool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * The tool class for application get preference value by key
 * Created by xyl on 2017/1/8 0008.
 */

public class PreferenceTool {

    public static String getString(Context ctx,String key,String defValue){
        return getDefaultPref(ctx).getString(key, defValue);
    }

//    public static void setString(Context ctx,String key,String value){
//getDefaultPref(ctx).
//    }

    public static int getInt(Context ctx,String key,int defValue){
        return getDefaultPref(ctx).getInt(key, defValue);
    }

    private static SharedPreferences getDefaultPref(Context ctx){
        return ctx.getSharedPreferences(
                ctx.getPackageName()+"_preferences", Context.MODE_PRIVATE);
    }
}
