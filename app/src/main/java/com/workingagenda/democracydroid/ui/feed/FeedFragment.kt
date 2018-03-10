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
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import com.workingagenda.democracydroid.MainApplication
import com.workingagenda.democracydroid.Network.Episode
import com.workingagenda.democracydroid.Network.ServerApi
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.ui.FragmentRefreshListener
import com.workingagenda.democracydroid.util.Constants
import com.workingagenda.democracydroid.util.DpToPixelHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import com.workingagenda.democracydroid.util.ViewExtensions.snack

class FeedFragment : Fragment(), FragmentRefreshListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.swiperefresh) lateinit var storySwipeRefreshLayout: SwipeRefreshLayout
    @BindView(R.id.progress_icon) lateinit var progress: ProgressBar

    private var stories: ArrayList<Episode> = ArrayList()
    private var storyAdapter: FeedAdapter? = null

    private lateinit var feedType: FeedType
    private lateinit var serverApi: ServerApi

    private val disposables = CompositeDisposable()

    companion object {

        fun newInstance(type: FeedType): Fragment {
            val fragment = FeedFragment()
            val extras1 = Bundle()
            extras1.putSerializable(Constants.FEED_TYPE, type)
            fragment.arguments = extras1
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments ?: return
        if (bundle.containsKey(Constants.FEED_TYPE)) {
            feedType = bundle.getSerializable(Constants.FEED_TYPE) as FeedType
        }
        serverApi = MainApplication.get(activity).applicationComponent.serverApi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_story, container, false)
        ButterKnife.bind(this,rootView)
        setupRecycler(feedType)
        storySwipeRefreshLayout.setOnRefreshListener(this)
        loadContent()
        return rootView
    }

    private fun setupRecycler(feedType: FeedType){
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(1, DpToPixelHelper.dpToPx(4, resources.displayMetrics), true))
        recyclerView.itemAnimator = DefaultItemAnimator()
        storyAdapter = FeedAdapter(context, stories, feedType)
        recyclerView.adapter = storyAdapter
    }

    private fun loadContent() {
        val feed: Observable<List<Episode>> = when (feedType) {
            FeedType.STORY -> serverApi.storyFeed()
            FeedType.EPISODE -> {
                val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                val mHasSpanish = preferences.getBoolean("spanish_preference", false)
                var feed = "https://www.democracynow.org/podcast.xml"
                if (mHasSpanish) {
                    feed = "https://www.democracynow.org/podcast-es.xml"
                }
                serverApi.videoFeed(feed)
            }
        }
        disposables.add(subscribeToFeed(feed))
    }

    private fun subscribeToFeed(observable: Observable<List<Episode>>): Disposable {
        progress.visibility = View.VISIBLE
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Episode>>() {
                    override fun onNext(episodes: List<Episode>) {
                        progress.visibility = View.INVISIBLE
                        stories.clear()
                        stories.addAll(episodes)
                        storyAdapter?.notifyDataSetChanged()                    }

                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        progress.visibility = View.INVISIBLE
                        recyclerView.snack("Network error")
                    }

                })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onRefresh() {
        loadContent()
        storySwipeRefreshLayout.isRefreshing = false
    }

    override fun refresh() {
        loadContent()
    }
}