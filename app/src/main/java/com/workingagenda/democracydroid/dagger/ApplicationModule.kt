package com.workingagenda.democracydroid.dagger

import android.app.Application
import com.workingagenda.democracydroid.Network.ServerApi
import dagger.Module
import dagger.Provides

/**
 * Created by derrickrocha on 1/9/18.
 */
@Module
class ApplicationModule(private val application:Application) {

    @Provides
    @ApplicationScope
    fun serverApi():ServerApi{
        return ServerApi()
    }
}