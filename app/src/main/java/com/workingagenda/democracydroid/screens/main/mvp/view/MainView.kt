package com.workingagenda.democracydroid.screens.main.mvp.view

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.widget.FrameLayout
import android.util.TypedValue
import com.workingagenda.democracydroid.Helpers.DpToPixelHelper
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.screens.main.mvp.view.adapter.SectionsPagerAdapter


/**
 * Created by derrickrocha on 12/9/17.
 */
class MainView(context: Context?, private val mFragmentManager: FragmentManager) : FrameLayout(context) {

    private lateinit var mCoordinatorLayout: CoordinatorLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private var mToolbar: Toolbar? = null
    private var appbarLayout: AppBarLayout? = null

    private var toolbarHeight : Int = 0

    init {
        initCoordinatorLayout()
        initAppbar()
        initToolbar()
        initTabLayout()
        initViewPager()
        initAdapter()
    }

    private fun initCoordinatorLayout() {
        mCoordinatorLayout = CoordinatorLayout(context)
        addView(mCoordinatorLayout)
    }

    private fun initAppbar() {
        appbarLayout = AppBarLayout(context)
        appbarLayout!!.context.setTheme(R.style.AppTheme_AppBarOverlay)
        appbarLayout!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        mCoordinatorLayout!!.addView(appbarLayout)
    }

    private fun initToolbar() {
        mToolbar = android.support.v7.widget.Toolbar(context)
        val tv = TypedValue()
        toolbarHeight = if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        else
            DpToPixelHelper.dpToPx(56,resources.displayMetrics)
        val params = AppBarLayout.LayoutParams(LayoutParams.MATCH_PARENT,toolbarHeight)
        mToolbar!!.popupTheme = R.style.AppTheme_PopupOverlay
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        mToolbar!!.layoutParams = params
        appbarLayout!!.addView(mToolbar)
    }

    private fun initTabLayout(){
        tabLayout = TabLayout(context)
        tabLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_library_books_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_live_tv_white_24dp))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_file_download_white_24dp))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        appbarLayout!!.addView(tabLayout)
    }

    private fun initViewPager(){
        mViewPager = ViewPager(context)
        mViewPager.id = R.id.container
        val params = CoordinatorLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        params.behavior = AppBarLayout.ScrollingViewBehavior()
        mViewPager.layoutParams = params
        mViewPager.offscreenPageLimit = 2
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        mCoordinatorLayout.addView(mViewPager)
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

    fun moveAdapterPosition(position:Int){
        mViewPager.currentItem = position
    }

    fun getToolbar(): Toolbar? = mToolbar
}