package com.workingagenda.democracydroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        final Preference versionPref = findPreference("pref_about_app_version");
        versionPref.setSummary("Version " + BuildConfig.VERSION_NAME);

        final ListPreference streamPref = findPreference("pref_default_stream");
        final SwitchPreferenceCompat spanishPref = findPreference("pref_spanish");
        spanishPref.setOnPreferenceChangeListener((preference, newValue) -> {
            if (spanishPref.isChecked()) {
                spanishPref.setChecked(false);
            } else {
                streamPref.setValueIndex(1);
                spanishPref.setChecked(true);
            }
            return false;
        });

        final Preference whatsNewPref = findPreference("pref_whats_new");
        whatsNewPref.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.whats_new)
                    .setMessage(R.string.whatsnew)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
                    .show();
            return false;
        });

        final Preference publisher = findPreference("pref_about_publisher");
        publisher.setOnPreferenceClickListener(preference -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.democracynow.org/"));
            startActivity(i);
            return true;
        });

        final Preference contactDm = findPreference("pref_about_contact_dm");
        contactDm.setOnPreferenceClickListener(preference -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.democracynow.org/contact"));
            startActivity(i);
            return true;
        });

        final Preference sourceCode = findPreference("pref_about_source_code");
        sourceCode.setOnPreferenceClickListener(preference -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fenimore/DemocracyDroid"));
            startActivity(i);
            return true;
        });
    }
}
