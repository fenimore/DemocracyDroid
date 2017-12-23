package com.workingagenda.democracydroid.screens.main.mvp.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.workingagenda.democracydroid.screens.download.DownloadFragment
import com.workingagenda.democracydroid.screens.podcast.PodcastFragment
import com.workingagenda.democracydroid.screens.story.StoryFragment

/**
 * Created by derrickrocha on 12/10/17.
 */
 class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> StoryFragment()
        1 -> PodcastFragment()
        2 -> DownloadFragment()
        else -> PodcastFragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "SECTION 1"
            1 -> return "SECTION 2"
            2 -> return "SECTION 3"
        }
        return null
    }

}