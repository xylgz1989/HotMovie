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

    public static void setString(Context ctx,String key,String value){
        getDefaultPref(ctx).edit().putString(key,value).commit();
    }

    public static int getInt(Context ctx,String key,int defValue){
        return getDefaultPref(ctx).getInt(key, defValue);
    }

    public static void setInt(Context ctx,String key,int value){
        getDefaultPref(ctx).edit().putInt(key,value).commit();
    }


    public static boolean getBoolean(Context ctx,String key,boolean defValue){
        return getDefaultPref(ctx).getBoolean(key,defValue);
    }

    public static void setBoolean(Context ctx,String key,boolean value){
        getDefaultPref(ctx).edit().putBoolean(key,value).commit();
    }

    private static SharedPreferences getDefaultPref(Context ctx){
        return ctx.getSharedPreferences(
                ctx.getPackageName()+"_preferences", Context.MODE_PRIVATE);
    }
}
