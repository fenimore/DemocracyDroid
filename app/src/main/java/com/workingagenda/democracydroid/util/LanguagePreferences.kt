package com.workingagenda.democracydroid.util

import android.content.SharedPreferences

/**
 * Created by derrickrocha on 12/10/17.
 */
class LanguagePreferences(private var mSharedPreferences: SharedPreferences) {

    fun getSpanishPreference():Boolean = mSharedPreferences.getBoolean("spanish_preference", false)

    fun setSpanishPreference(){

    }
}