package com.workingagenda.democracydroid.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by derrickrocha on 1/9/18.
 */
object ContextExtensions {

    fun Context.getDefaultPreferences():SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(this)
    }
}