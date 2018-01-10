package com.workingagenda.democracydroid.ui.main.dagger

import com.workingagenda.democracydroid.ui.main.MainActivity
import com.workingagenda.democracydroid.ui.main.mvp.MainModel
import com.workingagenda.democracydroid.ui.main.mvp.MainPresenter
import com.workingagenda.democracydroid.ui.main.mvp.view.MainView
import dagger.Module
import dagger.Provides

/**
 * Created by derrickrocha on 1/9/18.
 */
@Module
class MainModule(private val activity:MainActivity) {

    @Provides
    @MainScope
    fun mainView():MainView{
        return MainView(activity)
    }

    @Provides
    @MainScope
    fun mainPresenter(mainModel: MainModel,mainView: MainView):MainPresenter{
        return MainPresenter(mainModel,mainView)
    }

    @Provides
    @MainScope
    fun mainModel():MainModel{
        return MainModel(activity)
    }
}