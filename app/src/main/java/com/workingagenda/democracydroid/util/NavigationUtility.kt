package com.workingagenda.democracydroid.util

import android.content.Intent
import android.net.Uri
import com.workingagenda.democracydroid.ui.about.AboutActivity
import com.workingagenda.democracydroid.ui.main.MainActivity
import com.workingagenda.democracydroid.ui.settings.SettingsActivity

/**
 * Created by derrickrocha on 3/10/18.
 */
class NavigationUtility {

    companion object {

        @JvmStatic
        fun startSettingsActivity(mainActivity: MainActivity) {
            val intent = Intent(mainActivity, SettingsActivity::class.java)
            mainActivity.startActivity(intent)
        }

        fun startActionViewIntent(mainActivity: MainActivity, url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            mainActivity.startActivity(intent)
        }

        fun startAboutActivity(mainActivity: MainActivity) {
            val intent = Intent(mainActivity, AboutActivity::class.java)
            mainActivity.startActivityForResult(intent, 0)
        }

    }
}