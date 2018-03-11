package com.workingagenda.democracydroid.util

import android.content.Intent
import android.net.Uri
import com.workingagenda.democracydroid.ui.about.AboutActivity
import com.workingagenda.democracydroid.ui.main.MainActivity
import com.workingagenda.democracydroid.ui.media.MediaActivity
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

        fun startMediaActivity(mainActivity: MainActivity, url: String, title: String) {
            val intent = Intent(mainActivity, MediaActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("title", title)
            mainActivity.startActivityForResult(intent, 0) //Activity load = 0
        }

        fun startShareIntent(mainActivity: MainActivity, title: String?, url: String?) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            sendIntent.putExtra(Intent.EXTRA_TEXT, url)
            sendIntent.type = "text/plain"
            mainActivity.startActivity(sendIntent)
        }

    }
}