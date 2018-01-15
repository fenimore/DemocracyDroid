package com.workingagenda.democracydroid.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object ContextExtensions {

    fun Context.getDefaultPreferences():SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(this)
    }
}