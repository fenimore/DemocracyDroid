/*
 * Copyright (C) 2017 Democracy Droid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workingagenda.democracydroid.ui.main.dagger

import com.workingagenda.democracydroid.ui.main.MainActivity
import com.workingagenda.democracydroid.ui.main.mvp.MainModel
import com.workingagenda.democracydroid.ui.main.mvp.MainPresenter
import com.workingagenda.democracydroid.ui.main.mvp.view.MainView
import com.workingagenda.democracydroid.util.SharedPreferenceManager
import dagger.Module
import dagger.Provides

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
    fun mainModel(preferenceManager:SharedPreferenceManager):MainModel{
        return MainModel(activity,preferenceManager)
    }
}