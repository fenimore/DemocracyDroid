package com.workingagenda.democracydroid.dagger

import com.workingagenda.democracydroid.Network.ServerApi

/**
 * Created by derrickrocha on 12/31/17.
 */
interface ApplicationComponent {

    fun serverApi():ServerApi
}