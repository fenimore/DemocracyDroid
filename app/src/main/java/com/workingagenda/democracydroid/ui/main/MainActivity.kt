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
package com.workingagenda.democracydroid.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.workingagenda.democracydroid.ui.main.mvp.MainModel
import com.workingagenda.democracydroid.ui.main.mvp.MainPresenter
import com.workingagenda.democracydroid.ui.main.mvp.view.MainView

class MainActivity : AppCompatActivity() {

    private lateinit var mainModel: MainModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainModel = MainModel(this)
        val view = MainView(this)
        val presenter = MainPresenter(mainModel,view)
        presenter.onCreate()
        setContentView(view)
        setSupportActionBar(view.getToolbar())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = mainModel.onCreateOptionsMenu(menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = mainModel.onOptionsItemSelected(item)
}

