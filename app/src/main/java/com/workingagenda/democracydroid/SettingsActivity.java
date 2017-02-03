package com.workingagenda.democracydroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

/**
 * Created by fen on 5/15/16.
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private AppCompatDelegate mDelegate;
    public static final String KEY_WIFI = "wifi_preference";

    @Override
    public void onCreate(Bundle savedInstanceState){
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        ListPreference tab = (ListPreference) findPreference("tab_preference");
        if(tab.getValue() == null){
            tab.setValueIndex(1);
        }
        ListPreference stream = (ListPreference) findPreference("stream_preference");
        if(stream.getValue() == null){
            stream.setValueIndex(0);
        }

        ListPreference open = (ListPreference) findPreference("open_preference");
        if(open.getValue() == null){
            open.setValueIndex(0);
        }
        String versionName = BuildConfig.VERSION_NAME;
        Preference versionPref = findPreference("pref_static_field_key0");
        Preference newPref = findPreference("whats_new");
        versionPref.setSummary("Democracy Droid! " + versionName);

        newPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.whatsnew).setTitle("What's New").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId())
         {
             case android.R.id.home:
                 NavUtils.navigateUpFromSameTask(this);
                 return true;
         }
         return super.onOptionsItemSelected(item);
     }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }
    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }
    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }
    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
