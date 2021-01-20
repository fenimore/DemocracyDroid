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
package com.workingagenda.democracydroid.Network

import com.workingagenda.democracydroid.Network.Feedreader.RssReader

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe


class ServerApi {

    private val LIVE_TIME = 8
    private val mFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MMdd", Locale.US)

    fun videoFeed(feed: String): Observable<List<Episode>> {
        return Observable.create { emitter ->
            val rssReader = RssReader("https://www.democracynow.org/podcast-video.xml")
            val items = rssReader.items
            val size = items.size
            val epis = ArrayList<Episode>(size)
            for (i in 0 until size) {
                val episode = Episode()
                val item = items[i]
                val title = item.title
                episode.title = title
                episode.videoUrl = item.videoUrl
                val description = item.description
                episode.description = description
                episode.imageUrl = item.imageUrl
                episode.url = item.link
                epis.add(episode)
            }
            val episodes = checkLiveStream(epis)
            val audio = getAudio(feed)
            if (audio.size < 1) {
                emitter.onError(Throwable("Network error"))
                return@create
            } else {

                val timeZone = TimeZone.getTimeZone("America/New_York")
                val c = Calendar.getInstance(timeZone)
                val formattedDate = mFormat.format(c.time)
                val today_audio = "dn" + formattedDate
                val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
                val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
                val onSchedule = dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && hourOfDay > LIVE_TIME - 1
                if (onSchedule && hourOfDay == LIVE_TIME) {
                    audio.add(0, "http://democracynow.videocdn.scaleengine.net/" + "democracynow-iphone/play/democracynow/playlist.m3u8")
                } else if (onSchedule && audio.size > 0 &&
                        !audio.get(0).contains(today_audio)) {
                    audio.add(0, "http://traffic.libsyn.com/democracynow/$today_audio.mp3")
                }
                val audioSize = Math.min(episodes.size, audio.size)
                for (i in 0 until audioSize) {
                    episodes[i].audioUrl = audio[i]
                }
            }
            emitter.onNext(episodes)
        }
    }

    private fun getAudio(feed: String): ArrayList<String> {
        val rssReader = RssReader(feed)

        val audio =  ArrayList<String>()
        try {
            rssReader.items.mapTo(audio) { it.videoUrl }
        } catch ( e: Exception) {
            e.printStackTrace()
        }

        return audio
    }

    // Headlines are last in Feed, sort by Headlines
    fun storyFeed(): Observable<List<Episode>> {
        return Observable.create(ObservableOnSubscribe { emitter ->
            val stories = ArrayList<Episode>()
            val todaysStories = ArrayList<Episode>(32)
            try {
                val rssReader = RssReader("https://www.democracynow.org/podcast.xml")
                for (item in rssReader.items) {
                    val b = Episode()
                    b.title = item.title
                    b.description = item.description
                    b.pubDate = item.pubDate
                    b.imageUrl = item.contentEnc
                    b.url = item.link
                    todaysStories.add(0, b)
                    if (b.title.contains("Headlines")) {
                        stories.addAll(todaysStories)
                        todaysStories.clear()
                    }
                }
                if (!todaysStories.isEmpty()) {
                    stories.addAll(todaysStories)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(Throwable(e.message))
                return@ObservableOnSubscribe
            }

            emitter.onNext(stories)
        })
    }

    private fun checkLiveStream(epis: ArrayList<Episode>): ArrayList<Episode> {
        // Make it Pretty, and NY eastern Time
        val timeZone = TimeZone.getTimeZone("America/New_York")
        val c = Calendar.getInstance(timeZone)
        val formattedDate = mFormat.format(c.time)
        val todayVid1 = ("http://hot.dvlabs.com/democracynow/video-podcast/dn"
                + formattedDate + ".mp4")
        val todayVid2 = ("http://publish.dvlabs.com/democracynow/video-podcast/dn"
                + formattedDate + ".mp4")
        val todayAudio = ("http://traffic.libsyn.com/democracynow/dn"
                + formattedDate + "-1.mp3")
        val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return epis
        if (todayVid1 == epis[0].videoUrl) return epis
        if (todayVid2 == epis[0].videoUrl) return epis
        if (hourOfDay < LIVE_TIME) return epis
        val episode = getUnlistedStream(hourOfDay, todayAudio, todayVid2)
        epis.add(0, episode)
        return epis
    }

    private fun getUnlistedStream(hour: Int, audio: String, vid: String): Episode {
        // Live Stream
        val todaysEpisode = Episode()
        todaysEpisode.description = "Stream Live between 8 and 9 weekdays Eastern time, " + "the War and Peace Report"
        todaysEpisode.imageUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/0/01/" + "Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png"
        todaysEpisode.url = "http://m.democracynow.org/"
        if (LIVE_TIME == hour) {
            todaysEpisode.title = "Stream Live"//"Stream Live");
            todaysEpisode.videoUrl = "http://democracynow.videocdn.scaleengine.net/democracynow-iphone/" + "play/democracynow/playlist.m3u8"
            todaysEpisode.audioUrl = "http://democracynow.videocdn.scaleengine.net/democracynow-iphone/" + "play/democracynow/playlist.m3u8"
        } else if (hour > 8) {
            // Add Todays Broadcast even if RSS feed isn't updated yet
            todaysEpisode.title = "Today's Broadcast"
            todaysEpisode.description = "Democracy Now! The War and Peace Report"
            todaysEpisode.videoUrl = vid
            todaysEpisode.audioUrl = audio
        }
        return todaysEpisode
    }
}
