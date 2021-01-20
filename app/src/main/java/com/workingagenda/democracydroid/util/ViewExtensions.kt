package com.workingagenda.democracydroid.util

import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by derrickrocha on 3/9/18.
 */
object ViewExtensions {

    inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG){
        Snackbar.make(this,message,length)
    }
}