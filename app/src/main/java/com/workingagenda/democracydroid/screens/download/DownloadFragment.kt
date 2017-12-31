package com.workingagenda.democracydroid.screens.download

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.*
import com.workingagenda.democracydroid.Adapters.DownloadsAdapter
import com.workingagenda.democracydroid.R
import com.workingagenda.democracydroid.screens.media.MediaActivity
import java.io.File
import java.util.ArrayList

/**
 * Created by derrickrocha on 12/10/17.
 */
class DownloadFragment : Fragment() {
    lateinit var Txt1: TextView
    lateinit var btn: Button
    lateinit var btnRefresh: Button
    lateinit var dList: ListView
    lateinit var files: List<File>

    private val listFiles: List<File>
        get() {
            val inFiles = ArrayList<File>()
            val parentDir = File(Environment.getExternalStorageDirectory().toString() +
                    File.separator + Environment.DIRECTORY_PODCASTS)
            val files = parentDir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.name.startsWith("dn") || file.name.endsWith("-podcast.mp4") || file.name.endsWith("-podcast.mp3")) {
                        if (file.name.endsWith(".mp3") || file.name.endsWith(".mp4")) {
                            inFiles.add(file)
                        }
                    }
                }
            }
            return inFiles
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_download, container, false)

        files = listFiles

        dList = rootView.findViewById(android.R.id.list)
        Txt1 = rootView.findViewById(R.id.download_help)
        Txt1.setText(R.string.download_help)
        dList.emptyView = Txt1
        btn = rootView.findViewById(R.id.clear)
        btnRefresh = rootView.findViewById(R.id.refresh)
        registerForContextMenu(dList)

        dList.adapter = DownloadsAdapter(context, R.layout.row_download, files)

        btn.setOnClickListener {
            AlertDialog.Builder(context).setTitle("Delete all downloads")
                    .setMessage("Are you sure you want to delete all episodes?\nLong click and episode to delete them individually.")
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        for (file in files) {
                            Log.d("File", file.name)
                            // remove files
                            file.delete()
                        }
                        files = listFiles
                        dList.adapter = DownloadsAdapter(context, R.layout.row_download, files)
                        val toast = Toast.makeText(activity, "Downloads Removed", Toast.LENGTH_SHORT)
                        toast.show()
                    }.setNegativeButton(android.R.string.no) { dialog, which ->
                // do nothing
            }.setIcon(android.R.drawable.ic_dialog_alert).show()
        }
        btnRefresh.setOnClickListener {
            files = listFiles
            dList.adapter = DownloadsAdapter(context, R.layout.row_download, files)
        }
        dList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val f = files[position]
            val y = Intent(context, MediaActivity::class.java)
            y.putExtra("url", Uri.fromFile(f).toString()) //can't pass in article object?
            y.putExtra("title", f.name)
            startActivityForResult(y, 0) //Activity load = 0
        }

        return rootView
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == android.R.id.list) {
            val inflater = MenuInflater(context)
            menu.setHeaderTitle("Democracy Now!")
            inflater.inflate(R.menu.download_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        //int pos = ; FIND A WAY TO PASS LiST ITEM POSITION?
        val info = item?.menuInfo as AdapterView.AdapterContextMenuInfo
        val pos = info.position
        val file = files[pos]
        when (item.itemId) {
            R.id.action_delete -> {
                file.delete()
                files = listFiles
                dList.adapter = DownloadsAdapter(context, R.layout.row_download, files)
                return true
            }
            R.id.action_external_player -> {
                val z = Intent(Intent.ACTION_VIEW)
                z.setDataAndType(Uri.fromFile(file), "*/*")
                startActivity(z)
                return super.onContextItemSelected(item)
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    fun refresh() {
        //do nothing
    }
}