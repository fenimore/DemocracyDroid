package com.workingagenda.democracydroid;

import android.app.Activity;
import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.workingagenda.democracydroid.dagger.ApplicationComponent;
import com.workingagenda.democracydroid.dagger.ApplicationModule;
import com.workingagenda.democracydroid.dagger.DaggerApplicationComponent;


public class MainApplication extends Application {

    private ApplicationComponent component;

    public static MainApplication get(Activity activity) {
        return (MainApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(MainApplication.this);
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return component;
    }
}
