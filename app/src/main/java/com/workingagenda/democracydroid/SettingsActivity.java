package com.workingagenda.democracydroid;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * Created by fen on 5/15/16.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference tab = (ListPreference) findPreference("tab_preference");
        if(tab.getValue() == null){
            tab.setValueIndex(2);
        }
    }
}
