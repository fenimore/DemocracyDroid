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
package com.workingagenda.democracydroid.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.ui.FragmentRefreshListener
import com.workingagenda.democracydroid.ui.about.AboutActivity
import com.workingagenda.democracydroid.ui.download.DownloadFragment
import com.workingagenda.democracydroid.ui.feed.FeedFragment
import com.workingagenda.democracydroid.ui.feed.FeedType
import com.workingagenda.democracydroid.ui.settings.SettingsActivity
import com.workingagenda.democracydroid.util.SharedPreferenceManager

class MainActivity : AppCompatActivity() {

    @BindView(R.id.appbar_layout) lateinit var appbarLayout: AppBarLayout
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.tab_layout) lateinit var tabLayout: TabLayout
    @BindView(R.id.container) lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setupTabLayout()
        setupViewPager()
        setupAdapter()
        setSupportActionBar(toolbar)
    }

    private fun setupTabLayout(){
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_library_books_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_live_tv_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_file_download_white_24dp))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
    }

    private fun setupViewPager(){
        viewPager.offscreenPageLimit = 2
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

    private fun setupAdapter(){
        viewPager.adapter = SectionsPagerAdapter(supportFragmentManager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                appbarLayout.setExpanded(true,true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
        viewPager.currentItem = SharedPreferenceManager.getTabPreference()
    }

    private fun actionViewIntent(url:String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        val id = item.itemId
        when(id){
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_refresh -> {
                item.isEnabled = false
                for (x in supportFragmentManager.fragments) {
                    (x as? FragmentRefreshListener)?.refresh()
                }
                item.isEnabled = true
                return true
            }
            R.id.action_donate -> actionViewIntent("https://www.democracynow.org/donate")
            R.id.action_exclusives -> actionViewIntent("https://www.democracynow.org/categories/web_exclusive")
            R.id.action_site -> actionViewIntent("http://www.democracynow.org/")
            R.id.action_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivityForResult(intent, 0)
            }
        }
        return true
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> FeedFragment.newInstance(FeedType.STORY)
            1 -> FeedFragment.newInstance(FeedType.EPISODE)
            2 -> DownloadFragment()
            else -> FeedFragment.newInstance(FeedType.EPISODE)
        }

        override fun getCount(): Int = 3
    }
}

