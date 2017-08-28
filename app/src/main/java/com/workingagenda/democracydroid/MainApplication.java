package com.workingagenda.democracydroid;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.workingagenda.democracydroid.Feedreader.RssReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by derrickrocha on 7/16/17.
 */

public class MainApplication extends Application {



    public static MainApplication get(Activity activity){
        return (MainApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(MainApplication.this);
    }

}
