/*
 * Copyright (C) 2017 Derrick Rocha <drocha616@gmail.com>
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
package com.workingagenda.democracydroid.ui.feed.mvp

import android.os.Bundle
import android.support.v4.app.Fragment
import com.workingagenda.democracydroid.Network.Episode
import com.workingagenda.democracydroid.Network.ServerApi
import io.reactivex.Observable

class FeedModel(private val fragment: Fragment, private val serverApi: ServerApi) {

    fun getArgs(): Bundle? = fragment.arguments

    fun getStoryFeed(): Observable<List<Episode>> = Observable.create { e -> e.onNext(serverApi.storyFeed) }


    fun getVideoFeed():Observable<List<Episode>> = Observable.create { e -> e.onNext(serverApi.videoFeed) }

}