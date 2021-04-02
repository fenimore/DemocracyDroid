/*
 *
 *   Copyright (C) 2014-2015 Fenimore Love
 *
 *   This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workingagenda.democracydroid.MediaActivity;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.databinding.RowEpisodesBinding;

import static androidx.core.content.ContextCompat.*;

public class EpisodeViewHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    // ENUMS
    private static final int STREAM_VIDEO = 0;
    private static final int STREAM_AUDIO = 1;
    private static final int OPEN_THIS_APP = 0;
    final RowEpisodesBinding binding;
    private final TextView txt;
    private final ImageView img;
    private final TextView tag;
    private final ImageView mOptions;
    private final ImageView mDownload;
    private final SharedPreferences preferences;
    private Episode mEpisode;

    public EpisodeViewHolder(final RowEpisodesBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        img = binding.rowEpisodesImage;
        txt = binding.rowEpisodesTitle;
        tag = binding.rowEpisodesTag;
        tag.setMaxLines(3);
        mOptions = binding.rowEpisodesOptions;
        mDownload = binding.rowDownload;
        itemView.setOnCreateContextMenuListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
    }

    public void showEpisode(final Episode e) {
        if (e != null) {
            mEpisode = e;
            try {
                img.setImageURI(Uri.parse(e.getImageUrl()));
            } catch (Exception ex) {
                Log.v("Episode Adapter", "exception");
            }
            if (txt != null) {
                String fullTitle = e.getTitle().trim();
                if (fullTitle.startsWith("Democracy Now!")) {
                    String title = fullTitle.substring(14).trim();
                    txt.setText(title);
                } else {
                    txt.setText(fullTitle);
                }
            }
            if (tag != null) {
                String description = e.getDescription().trim();
                if (description.startsWith("Headlines for ")) {
                    description = description.substring(description.indexOf(";") + 1);
                }
                tag.setText(description);
                tag.setEllipsize(TextUtils.TruncateAt.END);
            }
            itemView.setOnClickListener(view -> loadEpisode(e));
            mOptions.setOnClickListener(view -> mOptions.showContextMenu());
            mDownload.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Download");
                builder.setMessage("Are you sure you want to download today's episode?");
                builder.setNeutralButton("Cancel", (dialog, which) -> {});
                builder.setNegativeButton("Audio", (dialog, which) ->
                        Download(e.getAudioUrl(), e.getTitle(), e.getDescription()));
                builder.setPositiveButton("Video", (dialog, which) ->
                        Download(e.getVideoUrl(), e.getTitle(), e.getDescription()));
                AlertDialog alert = builder.create();
                alert.show();
            }
        );
    }
}



    private void loadEpisode(Episode e) {
        if (e != null) {
            int DEFAULT_STREAM = Integer.parseInt(preferences.getString("pref_default_stream", "0")); // 0=video
            int DEFAULT_OPEN = Integer.parseInt(preferences.getString("pref_default_media_player", "0")); // 0 = within this app
            // Set the Title for Toolbar
            String actionTitle = "Democracy Now!";
            String title = e.getTitle().trim();
            if (title.length() > 16) {
                if (title.startsWith("Democracy Now!"))
                    actionTitle = title.substring(14);
                else
                    actionTitle = title;
            }
            if (DEFAULT_STREAM == STREAM_VIDEO)
                startMediaIntent(e.getVideoUrl(), DEFAULT_OPEN, actionTitle);
            else if (DEFAULT_STREAM == STREAM_AUDIO)
                startMediaIntent(e.getAudioUrl(), DEFAULT_OPEN, actionTitle);
        }
    }

    // start an activity either in this pap or another -- pass in either video
    // or audio stream.
    private void startMediaIntent(String url, int open, String title) {
        // pass in the URL if either audio or video (make check above)
        // Media Activity
        if (open == OPEN_THIS_APP) {
            Intent intent = new Intent(itemView.getContext(), MediaActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            ((Activity) itemView.getContext()).startActivityForResult(intent, 0); //Activity load = 0
        } else {
            // FIXME: SecurityException
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "*/*");
            itemView.getContext().startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(itemView.getContext());
        menu.setHeaderTitle("Democracy Now!");
        inflater.inflate(R.menu.context_menu, menu);
        int DEFAULT_STREAM = Integer.parseInt(preferences.getString("pref_default_stream", "0")); // 0=video
        int DEFAULT_OPEN = Integer.parseInt(preferences.getString("pref_default_media_player", "0")); // 0 = within this app

        if (DEFAULT_STREAM == 0)
            menu.getItem(0).setTitle("Stream Audio");
        else
            menu.getItem(0).setTitle("Stream Video");

        if (DEFAULT_OPEN == 0)
            menu.getItem(1).setTitle("Stream in Another App");
        else
            menu.getItem(1).setTitle("Stream in This App");
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setOnMenuItemClickListener(this);
        }
    }


    // FIXME: Show progress:
    // http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
    private void Download(String url, String title, String desc) {
        if (checkSelfPermission(itemView.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ((Activity)itemView.getContext()).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            // TODO: catch onRequestPermissionsResult
        } else {
            if ("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/play/democracynow/playlist.m3u8".equals(url)) {
                Toast toast = Toast.makeText(itemView.getContext(),
                        "You can't download the Live Stream", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(desc);
            request.setTitle(title);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            String fileext = url.substring(url.lastIndexOf('/') + 1);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, fileext);
            //http://stackoverflow.com/questions/24427414/getsystemservices-is-undefined-when-called-in-a-fragment

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            // TODO: Save que ID for cancel button
            Toast toast = Toast.makeText(itemView.getContext(), "Starting download of " + title, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int DEFAULT_STREAM = Integer.parseInt(preferences.getString("pref_default_stream", "0")); // 0=video
        int DEFAULT_OPEN = Integer.parseInt(preferences.getString("pref_default_media_player", "0")); // 0 = within this ap
        String actionTitle = "Democracy Now!";
        if (mEpisode.getTitle().length() > 16) {
            if ("Today's Broadcast".equals(mEpisode.getTitle())) {
                actionTitle = mEpisode.getTitle();
            } else if (mEpisode.getTitle().startsWith("Democracy Now!")) {
                actionTitle = mEpisode.getTitle().substring(14);
            } else {
                actionTitle = mEpisode.getTitle();
            }
        }

        switch (menuItem.getItemId()) {
            case R.id.menu_context_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, mEpisode.getTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, mEpisode.getUrl());
                sendIntent.setType("text/plain");
                itemView.getContext().startActivity(sendIntent);
                return true;
            case R.id.menu_context_reverse_default_media:
                if (mEpisode.getVideoUrl().contains("m3u8"))
                    startMediaIntent(mEpisode.getAudioUrl(), 1, mEpisode.getTitle());
                else if (DEFAULT_STREAM == 0)
                    startMediaIntent(mEpisode.getAudioUrl(), DEFAULT_OPEN, actionTitle);
                else
                    startMediaIntent(mEpisode.getVideoUrl(), DEFAULT_OPEN, actionTitle);
                return true;
            case R.id.menu_context_reverse_default_open:
                int reverseOpen = 0;
                if (reverseOpen == DEFAULT_OPEN)
                    reverseOpen = 1;
                if (DEFAULT_STREAM == 0)
                    startMediaIntent(mEpisode.getVideoUrl(), reverseOpen, actionTitle);
                else
                    startMediaIntent(mEpisode.getAudioUrl(), reverseOpen, actionTitle);
                return true;
            case R.id.menu_context_description:
                new AlertDialog.Builder(itemView.getContext())
                        // Get Description and Title
                        .setTitle("The War and Peace Report")
                        .setMessage(mEpisode.getDescription() + "\n\n" + mEpisode.getTitle())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.menu_video_download:
                if (mEpisode.getTitle().equals("Stream Live"))
                    return true;
                Download(mEpisode.getVideoUrl(), mEpisode.getTitle(), mEpisode.getDescription());
                return true;
            case R.id.menu_audio_download:
                if (mEpisode.getTitle().equals("Stream Live"))
                    return true;
                Download(mEpisode.getAudioUrl(), mEpisode.getTitle(), mEpisode.getDescription());
                return true;
            case R.id.menu_context_open_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mEpisode.getUrl()), "*/*");
                itemView.getContext().startActivity(intent);
                return true;
        }
        return false;
    }
}
