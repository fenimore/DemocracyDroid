package com.workingagenda.democracydroid

import android.app.Application

import com.facebook.drawee.backends.pipeline.Fresco
import com.workingagenda.democracydroid.dagger.ApplicationComponent
import com.workingagenda.democracydroid.dagger.ApplicationModule
import com.workingagenda.democracydroid.dagger.DaggerApplicationComponent


class MainApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Fresco.initialize(this)
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule())
                .build()
    }

    companion object {

        private lateinit var mInstance:MainApplication

        @Synchronized
        fun getInstance(): MainApplication {
            return mInstance
        }

    }

}
