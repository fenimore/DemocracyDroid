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
package com.workingagenda.democracydroid.ui.feed.mvp.view

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.workingagenda.democracydroid.ui.feed.GridSpacingItemDecoration
import com.workingagenda.democracydroid.Network.Episode
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.ui.feed.FeedType
import com.workingagenda.democracydroid.util.DpToPixelHelper
import io.reactivex.Observable

class FeedView(context: Context?) : FrameLayout(context) {

    private var progress: ProgressBar
    private var storyAdapter: FeedAdapter? = null
    private var storySwipeRefreshLayout: SwipeRefreshLayout
    private var recyclerView: RecyclerView

    private var stories: ArrayList<Episode> = ArrayList()

    init {
        inflate(getContext(), R.layout.fragment_story, this)
        progress = findViewById(R.id.progress_icon)
        storySwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh)
        recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(1, DpToPixelHelper.dpToPx(4, resources.displayMetrics), true))
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    fun showEpisodes(episodes: List<Episode>?) {
        if (episodes == null)
            return
        stories.clear()
        stories.addAll(episodes)
        storyAdapter?.notifyDataSetChanged()
    }

    fun showProgress(show:Boolean){
        progress.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    fun observeSwipeRefresh(): Observable<Any> {
        return Observable.create { e->
            storySwipeRefreshLayout.setOnRefreshListener { e.onNext(Any()) } }
    }

    fun initAdapter(feedType: FeedType){
        storyAdapter = FeedAdapter(context, stories, feedType)
        recyclerView.adapter = storyAdapter
    }

    fun hidePullToRefresh() {
        storySwipeRefreshLayout.isRefreshing = false
    }
}