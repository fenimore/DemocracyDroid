package com.workingagenda.democracydroid.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by derrickrocha on 12/30/17.
 */
object SharedPreferenceExtensions {

    fun Application.getTabPreference():Int{
        return Integer.parseInt(getDefaultSharedPreferences(this).getString("tab_preference", "1"))
    }

    private fun getDefaultSharedPreferences(context: Context):SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

}