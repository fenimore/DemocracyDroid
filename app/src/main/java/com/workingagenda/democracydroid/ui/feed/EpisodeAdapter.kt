package com.workingagenda.democracydroid.ui.feed

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.workingagenda.democracydroid.Network.Episode
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.ui.media.MediaActivity
import com.workingagenda.democracydroid.util.Constants

/**
 * Created by derrickrocha on 3/10/18.
 */
class EpisodeAdapter(private val context: Context, private val mEpisodes: List<Episode>) : RecyclerView.Adapter<BaseStoryViewHolder>() {
    private val mInflator: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseStoryViewHolder {
        val v = mInflator.inflate(R.layout.row_episodes, null)
        return EpisodeViewHolder(v)
    }

    override fun onBindViewHolder(holder: BaseStoryViewHolder, position: Int) {
        holder.showEpisode(mEpisodes[position])
    }

    override fun getItemCount(): Int {
        return mEpisodes.size
    }

    inner internal class EpisodeViewHolder(itemView: View) : BaseStoryViewHolder(itemView), View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        override fun currentEpisode(): Episode {
            return mEpisodes[adapterPosition]
        }

        private val titleView: TextView?
        private val imageView: ImageView
        private val tagView: TextView?
        private val optionsView: ImageView
        private val downloadView: ImageView

        private val STREAM_VIDEO = 0
        private val STREAM_AUDIO = 1
        private val OPEN_THIS_APP = 0

        init {
            imageView = itemView.findViewById(R.id.row_image)
            titleView = itemView.findViewById(R.id.row_title)
            tagView = itemView.findViewById(R.id.row_tag)
            tagView!!.maxLines = 3
            optionsView = itemView.findViewById(R.id.row_options)
            downloadView = itemView.findViewById(R.id.row_download)
        }

        override fun showEpisode(episode: Episode?) {
            if (episode != null) {
                try {
                    imageView.setImageURI(Uri.parse(episode.imageUrl))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                if (titleView != null) {
                    val fullTitle = episode.title.trim { it <= ' ' }
                    if (fullTitle.startsWith("Democracy Now!")) {
                        val title = fullTitle.substring(14).trim { it <= ' ' }
                        titleView.text = title
                    } else {
                        titleView.text = fullTitle
                    }
                }
                if (tagView != null) {
                    var description = episode.description.trim { it <= ' ' }
                    if (description.startsWith("Headlines for ")) {
                        description = description.substring(description.indexOf(";") + 1)
                    }
                    tagView.text = description
                    tagView.ellipsize = TextUtils.TruncateAt.END
                }
                itemView.setOnClickListener { loadEpisode(episode) }
                downloadView.setOnClickListener {
                    val builder = AlertDialog.Builder(itemView.context)
                    builder.setTitle("Download")
                    builder.setMessage("Are you sure you want to download today's episode?")
                    builder.setNeutralButton("Cancel") { dialog, which -> }
                    builder.setNegativeButton("Audio") { dialog, which -> Download(episode.audioUrl, episode.title, episode.description) }
                    builder.setPositiveButton("Video") { dialog, which -> Download(episode.videoUrl, episode.title, episode.description) }
                    val alert = builder.create()
                    alert.show()
                }
                optionsView.setOnClickListener { optionsView.showContextMenu() }
            }
        }

        private fun loadEpisode(episode: Episode) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val DEFAULT_STREAM = Integer.parseInt(preferences.getString(Constants.STREAM_PREFERENCE, "0")) // 0=video
            val DEFAULT_OPEN = Integer.parseInt(preferences.getString(Constants.OPEN_PREFERENCE, "0")) // 0 = within this app
            // Set the Title for Toolbar
            var actionTitle = "Democracy Now!"
            val title = episode.title.trim { it <= ' ' }
            if (title.length > 16) {
                actionTitle = if (title.startsWith("Democracy Now!"))
                    title.substring(14)
                else
                    title
            }
            if (DEFAULT_STREAM == STREAM_VIDEO)
                startMediaIntent(episode.videoUrl, DEFAULT_OPEN, actionTitle)
            else if (DEFAULT_STREAM == STREAM_AUDIO)
                startMediaIntent(episode.audioUrl, DEFAULT_OPEN, actionTitle)
        }

        // start an activity either in this pap or another -- pass in either video
        // or audio stream.
        private fun startMediaIntent(url: String, open: Int, title: String) {
            // pass in the URL if either audio or video (make check above)
            // Media Activity
            if (open == OPEN_THIS_APP) {
                val intent = Intent(context, MediaActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("title", title)
                (context as Activity).startActivityForResult(intent, 0) //Activity load = 0
            } else {
                // FIXME: SecurityException
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(url), "*/*")
                context.startActivity(intent)
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            val inflater = MenuInflater(context)
            menu.setHeaderTitle("Democracy Now!")
            inflater.inflate(R.menu.context_menu, menu)
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")) // 0=video
            val DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")) // 0 = within this app

            if (DEFAULT_STREAM == 0)
                menu.getItem(2).title = "Stream Audio"
            else
                menu.getItem(2).title = "Stream Video"

            if (DEFAULT_OPEN == 0)
                menu.getItem(3).title = "Stream in Another App"
            else
                menu.getItem(3).title = "Stream in This App"
            for (i in 0 until menu.size()) {
                menu.getItem(i).setOnMenuItemClickListener(this)
            }
        }

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
            val episode = mEpisodes[adapterPosition]
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")) // 0=video
            val DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")) // 0 = within this ap
            var actionTitle = "Democracy Now!"
            if (episode.title.length > 16) {
                actionTitle = when {
                    "Today's Broadcast" == episode!!.title -> episode.title
                    episode.title.startsWith("Democracy Now!") -> episode.title.substring(14)
                    else -> episode.title
                }
            }

            when (menuItem.itemId) {
                R.id.action_share -> {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, episode!!.title)
                    sendIntent.putExtra(Intent.EXTRA_TEXT, episode!!.url)
                    sendIntent.type = "text/plain"
                    context.startActivity(sendIntent)
                    return true
                }
                R.id.reverse_default_media -> {
                    when {
                        episode.videoUrl.contains("m3u8") -> startMediaIntent(episode.audioUrl, 1, episode.title)
                        DEFAULT_STREAM == 0 -> startMediaIntent(episode.audioUrl, DEFAULT_OPEN, actionTitle)
                        else -> startMediaIntent(episode.videoUrl, DEFAULT_OPEN, actionTitle)
                    }
                    return true
                }
                R.id.reverse_default_open -> {
                    var reverseOpen = 0
                    if (reverseOpen == DEFAULT_OPEN)
                        reverseOpen = 1
                    if (DEFAULT_STREAM == 0)
                        startMediaIntent(episode.videoUrl, reverseOpen, actionTitle)
                    else
                        startMediaIntent(episode.audioUrl, reverseOpen, actionTitle)
                    return true
                }
                R.id.action_description -> {
                    val description = AlertDialog.Builder(context).create()
                    // Get Description and Title
                    description.setTitle("The War and Peace Report")
                    description.setMessage(episode.description + "\n\n" + episode.title)
                    description.setButton("Ok") { _, _ ->
                        // do nothing
                    }
                    description.show()
                    return true
                }
                R.id.video_download -> {
                    if (episode.title == "Stream Live")
                        return true
                    Download(episode.videoUrl, episode.title, episode.description)
                    return true
                }
                R.id.audio_download -> {
                    if (episode.title == "Stream Live")
                        return true
                    Download(episode.audioUrl, episode.title, episode.description)
                    return true
                }
                R.id.open_browser -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(episode.url), "*/*")
                    context.startActivity(intent)
                    return true
                }
            }
            return false
        }

        private fun Download(url: String, title: String, desc: String) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    (context as Activity).requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                    return
                    // TODO: catch onRequestPermissionsResult
                }
            }
            if ("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/play/democracynow/playlist.m3u8" == url) {
                val toast = Toast.makeText(context,
                        "You can't download the Live Stream", Toast.LENGTH_LONG)
                toast.show()
                return
            }
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDescription(desc)
            request.setTitle(title)
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val fileext = url.substring(url.lastIndexOf('/') + 1)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, fileext)
            //http://stackoverflow.com/questions/24427414/getsystemservices-is-undefined-when-called-in-a-fragment

            // get download service and enqueue file
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
            // TODO: Save que ID for cancel button
            val toast = Toast.makeText(context, "Starting download of " + title, Toast.LENGTH_LONG)
            toast.show()

        }

    }
}