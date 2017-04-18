package com.example.xyl.hotmovie.setting;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import com.example.xyl.hotmovie.R;
import com.xyl.tool.PreferenceTool;

/**
 * Setting Activity for hot movie app
 * Created by xyl on 2017/1/4 0004.
 */

public class SettingActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
       // bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
        ListPreference sortPref = (ListPreference) findPreference(getString(R.string.pref_sort_key));
        sortPref.setSummary(sortPref.getEntry());
        sortPref.setOnPreferenceChangeListener(this);

        ListPreference syncPref = (ListPreference) findPreference(getString(R.string.pref_sync_rate_key));
        syncPref.setSummary(syncPref.getEntry());
        syncPref.setOnPreferenceChangeListener(this);

        SwitchPreference movieRankNotifyPref = (SwitchPreference) findPreference(getString(R.string.pref_movie_rank_notification_switch_key));
        movieRankNotifyPref.setOnPreferenceChangeListener(this);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        String prefKey = preference.getKey();
        if(prefKey.equals(getString(R.string.pref_sort_key))){
            PreferenceTool.setBoolean(this,getString(R.string.sort_pref_changed),true);
            preference.setSummary(stringValue);
        }else if(prefKey.equals(getString(R.string.pref_sync_rate_key))){
            preference.setSummary(stringValue);
        }else if(prefKey.equals(getString(R.string.pref_movie_rank_notification_switch_key))){

        }
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        } else {
            // For other preferences, set the summary to the value's simple string representation.

        }
        return true;
    }
}
