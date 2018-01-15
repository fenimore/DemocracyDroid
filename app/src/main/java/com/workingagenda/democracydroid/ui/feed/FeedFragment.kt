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
package com.workingagenda.democracydroid.ui.feed

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.workingagenda.democracydroid.MainApplication
import com.workingagenda.democracydroid.ui.FragmentRefreshListener
import com.workingagenda.democracydroid.ui.feed.dagger.DaggerFeedComponent
import com.workingagenda.democracydroid.ui.feed.dagger.FeedModule
import com.workingagenda.democracydroid.ui.feed.mvp.FeedPresenter
import com.workingagenda.democracydroid.ui.feed.mvp.view.FeedView
import javax.inject.Inject

class FeedFragment : Fragment(), FragmentRefreshListener {

    companion object {
        val FEED_TYPE = "feedType"
    }

    @Inject
    lateinit var presenter: FeedPresenter

    @Inject
    lateinit var view: FeedView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        DaggerFeedComponent
                .builder()
                .feedModule(FeedModule(this))
                .applicationComponent(MainApplication.get(activity).applicationComponent)
                .build()
                .injectFeedFragment(this)
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

    override fun refresh() {
        presenter.refresh()
    }
}