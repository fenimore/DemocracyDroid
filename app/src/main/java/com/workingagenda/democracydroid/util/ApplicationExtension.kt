package com.workingagenda.democracydroid.util

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by derrickrocha on 3/10/18.
 */
object ApplicationExtension {

    fun Application.defaultSharedPreferences(): SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(this)
    }
}