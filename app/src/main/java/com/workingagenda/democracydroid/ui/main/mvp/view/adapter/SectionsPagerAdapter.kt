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
package com.workingagenda.democracydroid.ui.main.mvp.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.workingagenda.democracydroid.ui.download.DownloadFragment
import android.os.Bundle
import com.workingagenda.democracydroid.ui.feed.FeedFragment
import com.workingagenda.democracydroid.ui.feed.FeedType


class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> getFeedFragment(FeedType.STORY)
        1 -> getFeedFragment(FeedType.VIDEO)
        2 -> DownloadFragment()
        else -> getFeedFragment(FeedType.VIDEO)
    }

    private fun getFeedFragment(type: FeedType): Fragment {
        val fragment = FeedFragment()
        val extras1 = Bundle()
        extras1.putSerializable(FeedFragment.FEED_TYPE, type)
        fragment.arguments = extras1
        return fragment
    }

    override fun getCount(): Int = 3
}