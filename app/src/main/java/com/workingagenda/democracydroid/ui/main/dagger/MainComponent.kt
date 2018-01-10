package com.workingagenda.democracydroid.ui.main.dagger

import com.workingagenda.democracydroid.ui.main.mvp.MainModel
import com.workingagenda.democracydroid.ui.main.mvp.MainPresenter
import com.workingagenda.democracydroid.ui.main.mvp.view.MainView
import dagger.Component

/**
 * Created by derrickrocha on 1/9/18.
 */
@MainScope
@Component(modules = arrayOf(MainModule::class))
interface MainComponent {

    fun mainView():MainView

    fun mainPresenter():MainPresenter

    fun mainModel():MainModel

}