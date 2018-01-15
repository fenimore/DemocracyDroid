/*
 * Copyright (C) 2017 Democracy Droid
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
package com.workingagenda.democracydroid.ui.main.mvp.view

import android.content.Context
import android.support.design.widget.AppBarLayout
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

    private var appbarLayout: AppBarLayout

    init {
        inflate(getContext(), R.layout.activity_main, this)
        appbarLayout = findViewById(R.id.appbar_layout)
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
                appbarLayout.setExpanded(true,true)
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