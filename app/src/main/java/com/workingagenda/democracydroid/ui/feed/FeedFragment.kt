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
import com.workingagenda.democracydroid.util.PreferenceUtility
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
    private var storyAdapter: RecyclerView.Adapter<BaseStoryViewHolder>? = null

    private lateinit var feedType: FeedType
    private lateinit var serverApi: ServerApi

    lateinit var feed: Observable<List<Episode>>

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
        feedType = (arguments?.getSerializable(Constants.FEED_TYPE) as FeedType)
        serverApi = MainApplication.getInstance().applicationComponent.serverApi()
        feed = when (feedType) {
            FeedType.STORY -> serverApi.storyFeed()
            FeedType.EPISODE -> {
                var feed = resources.getString(R.string.podcast_url)
                if (PreferenceUtility.spanish()) {
                    feed = resources.getString(R.string.spanish_podcast_url)
                }
                serverApi.videoFeed(feed)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_story, container, false)
        ButterKnife.bind(this,rootView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(1, DpToPixelHelper.dpToPx(4, resources.displayMetrics), true))
        recyclerView.itemAnimator = DefaultItemAnimator()
        storyAdapter = when(feedType){ FeedType.STORY -> StoryAdapter(context, stories) else -> context?.let { EpisodeAdapter(it, stories)}}
        recyclerView.adapter = storyAdapter
        storySwipeRefreshLayout.setOnRefreshListener(this)

        disposables.add(subscribeToFeed())

        return rootView
    }

    private fun subscribeToFeed(): Disposable {
        progress.visibility = View.VISIBLE
        return feed
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
        disposables.add(subscribeToFeed())
        storySwipeRefreshLayout.isRefreshing = false
    }

    override fun refresh() {
        disposables.add(subscribeToFeed())
    }
}