package com.workingagenda.democracydroid;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(MainApplication.this);
    }
}
