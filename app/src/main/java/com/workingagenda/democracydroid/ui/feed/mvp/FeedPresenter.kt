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
package com.workingagenda.democracydroid.ui.feed.mvp

import com.workingagenda.democracydroid.Network.Episode
import com.workingagenda.democracydroid.ui.feed.FeedFragment
import com.workingagenda.democracydroid.ui.feed.FeedType
import com.workingagenda.democracydroid.ui.feed.mvp.view.FeedView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class FeedPresenter(private val model: FeedModel,private val view: FeedView) {

    private val disposables = CompositeDisposable()

    private lateinit var feedType: FeedType

    fun onCreate(){
        val bundle = model.getArgs() ?: return
        if (bundle.containsKey(FeedFragment.FEED_TYPE)){
            feedType = bundle.getSerializable(FeedFragment.FEED_TYPE) as FeedType
            view.initAdapter(feedType)
            loadContent()
            disposables.add(observePullToRefresh())
        }
    }

    fun onDestroy(){
        disposables.clear()
    }

    private fun loadContent() {
        when(feedType){
            FeedType.STORY -> disposables.add(getStoryFeed())
            FeedType.VIDEO -> disposables.add(getVideoFeed())
        }
    }

    private fun getStoryFeed():Disposable{
        view.showProgress(true)
        return model.getStoryFeed()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Episode>>() {
                    override fun onNext(t: List<Episode>) {
                        view.showEpisodes(t)
                        view.showProgress(false)
                    }

                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.showProgress(false)
                    }

                })
    }

    private fun getVideoFeed():Disposable{
        view.showProgress(true)
        return model.getVideoFeed()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Episode>>() {
                    override fun onNext(t: List<Episode>) {
                        view.showEpisodes(t)
                        view.showProgress(false)
                    }

                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        view.showProgress(false)
                    }

                })
    }

    private fun observePullToRefresh(): Disposable {
        return view.observeSwipeRefresh()
                .subscribeWith(object : DisposableObserver<Any>() {
                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onNext(t: Any) {
                        when(feedType){
                            FeedType.STORY -> disposables.add(getStoryFeed())
                            FeedType.VIDEO -> disposables.add(getVideoFeed())
                        }
                        view.hidePullToRefresh()
                    }

                })
    }
}