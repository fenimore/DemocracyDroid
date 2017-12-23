package com.workingagenda.democracydroid.screens.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.workingagenda.democracydroid.screens.main.mvp.MainModel
import com.workingagenda.democracydroid.screens.main.mvp.view.MainView

class MainActivity : AppCompatActivity() {

    private lateinit var mainModel: MainModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainModel = MainModel(this)
        val view = MainView(this)
        setContentView(view)
        setSupportActionBar(view.getToolbar())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = mainModel.onCreateOptionsMenu(menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = mainModel.onOptionsItemSelected(item)
}
