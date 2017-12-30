package com.workingagenda.democracydroid.screens.main.mvp.view

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.FrameLayout
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.screens.main.mvp.view.adapter.SectionsPagerAdapter


/**
 * Created by derrickrocha on 12/9/17.
 */
class MainView(context: Context?) : FrameLayout(context) {

    private val mFragmentManager = (context as AppCompatActivity).supportFragmentManager

    private lateinit var mViewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private var mToolbar: Toolbar

    init {
        inflate(getContext(), R.layout.activity_main, this)
        mToolbar = findViewById(R.id.toolbar) as Toolbar
        initTabLayout()
        initViewPager()
        initAdapter()
    }

    private fun initTabLayout(){
        tabLayout = findViewById(R.id.tab_layout) as TabLayout
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_library_books_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_live_tv_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_file_download_white_24dp))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

    }

    private fun initViewPager(){
        mViewPager = findViewById(R.id.container) as ViewPager
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
}