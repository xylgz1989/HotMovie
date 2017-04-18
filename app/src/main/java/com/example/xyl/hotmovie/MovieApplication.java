package com.example.xyl.hotmovie;

import android.app.Application;

import com.xyl.tool.CrashHandler;

/**
 * Created by xyl on 2017/2/22 0022.
 */

public class MovieApplication extends Application {
    private int hourInMs = 60 * 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        //负责更新的定时器代码
       /* int syncRateInHour = PreferenceTool.getInt(this,
                getString(R.string.pref_sync_rate_key),24);
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC,System.currentTimeMillis() + 1000,
                syncRateInHour * hourInMs, pendingIntent);
*/
    }
}
