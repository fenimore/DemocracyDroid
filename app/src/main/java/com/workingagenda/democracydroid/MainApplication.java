package com.workingagenda.democracydroid;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.drawee.backends.pipeline.Fresco;


public class MainApplication extends Application {

    public static MainApplication get(Activity activity){
        return (MainApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(MainApplication.this);
    }

    public boolean getSpanishPreference(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("spanish_preference", false);
    }
}
