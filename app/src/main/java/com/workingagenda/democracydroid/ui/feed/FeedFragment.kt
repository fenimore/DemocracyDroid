/*
 *  Copyright (C) 2014-2015 Derrick Rocha
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
package com.workingagenda.democracydroid.ui.feed

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.workingagenda.democracydroid.Network.ServerApi
import com.workingagenda.democracydroid.ui.feed.mvp.FeedModel
import com.workingagenda.democracydroid.ui.feed.mvp.FeedPresenter
import com.workingagenda.democracydroid.ui.feed.mvp.view.FeedView

/**
 * Created by derrickrocha on 1/3/18.
 */
class FeedFragment : Fragment() {

    companion object {
        val FEED_TYPE = "feedType"
    }

    private lateinit var presenter: FeedPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = FeedView(context)
        val serverApi = ServerApi()
        val model = FeedModel(this,serverApi)
        presenter = FeedPresenter(model,view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onCreate()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}