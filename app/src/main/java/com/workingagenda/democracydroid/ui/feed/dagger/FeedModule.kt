/*
 *  Copyright (C) 2014-2015 Democracy Droid
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.workingagenda.democracydroid.ui.feed.dagger

import com.workingagenda.democracydroid.Network.ServerApi
import com.workingagenda.democracydroid.ui.feed.FeedFragment
import com.workingagenda.democracydroid.ui.feed.mvp.FeedModel
import com.workingagenda.democracydroid.ui.feed.mvp.FeedPresenter
import com.workingagenda.democracydroid.ui.feed.mvp.view.FeedView
import dagger.Module
import dagger.Provides

@Module
class FeedModule(private val fragment: FeedFragment) {

    @Provides
    @FeedScope
    fun feedView():FeedView{
        return FeedView(fragment.activity)
    }

    @Provides
    @FeedScope
    fun feedModel(serverApi:ServerApi):FeedModel{
        return FeedModel(fragment,serverApi)
    }

    @Provides
    @FeedScope
    fun feedPresenter(model: FeedModel,view: FeedView):FeedPresenter{
        return FeedPresenter(model,view)
    }
}