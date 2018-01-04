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
package com.workingagenda.democracydroid.ui.main.mvp.view

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.FrameLayout
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.ui.main.mvp.view.adapter.SectionsPagerAdapter

class MainView(context: Context) : FrameLayout(context) {

    private val mFragmentManager = (context as AppCompatActivity).supportFragmentManager

    private lateinit var mViewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private var mToolbar: Toolbar

    init {
        inflate(getContext(), R.layout.activity_main, this)
        mToolbar = findViewById(R.id.toolbar)
        initTabLayout()
        initViewPager()
        initAdapter()
    }

    private fun initTabLayout(){
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_library_books_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_live_tv_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_file_download_white_24dp))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
    }

    private fun initViewPager(){
        mViewPager = findViewById(R.id.container)
        mViewPager.offscreenPageLimit = 2
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

    private fun initAdapter(){
        mViewPager.adapter = SectionsPagerAdapter(mFragmentManager)
        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    fun getToolbar(): Toolbar? = mToolbar

    fun moveTabPosition(position: Int) {
        mViewPager.currentItem = position
    }
}