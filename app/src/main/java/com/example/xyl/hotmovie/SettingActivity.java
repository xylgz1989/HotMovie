package com.example.xyl.hotmovie;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

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
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            PreferenceTool.setBoolean(this,getString(R.string.sort_pref_changed),true);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }
}
