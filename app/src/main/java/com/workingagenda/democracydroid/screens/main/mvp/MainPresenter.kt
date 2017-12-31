package com.workingagenda.democracydroid.screens.main.mvp

import com.workingagenda.democracydroid.screens.main.mvp.view.MainView

/**
 * Created by derrickrocha on 12/30/17.
 */
class MainPresenter(private val model: MainModel,private val view:MainView) {

    fun onCreate(){
        view.moveTabPosition(model.getTabPreference())
    }
}