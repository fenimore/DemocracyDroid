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
package com.workingagenda.democracydroid.ui.feed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.workingagenda.democracydroid.ui.media.MediaActivity;
import com.workingagenda.democracydroid.Network.Episode;
import com.workingagenda.democracydroid.R;

public class EpisodeViewHolder extends BaseStoryViewHolder implements View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener {

    private final TextView titleView;
    private final ImageView imageView;
    private final TextView tagView;
    private final ImageView optionsView;
    private final ImageView downloadView;

    private static final int STREAM_VIDEO = 0;
    private static final int STREAM_AUDIO = 1;
    private static final int OPEN_THIS_APP = 0;
    private Episode episode;

    public EpisodeViewHolder(final View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.row_image);
        titleView = itemView.findViewById(R.id.row_title);
        tagView = itemView.findViewById(R.id.row_tag);
        tagView.setMaxLines(3);
        optionsView = itemView.findViewById(R.id.row_options);
        downloadView = itemView.findViewById(R.id.row_download);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void showEpisode(final Episode episode) {
        if (episode != null) {
            this.episode = episode;
            try {
                imageView.setImageURI(Uri.parse(episode.getImageUrl()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (titleView != null) {
                String fullTitle = episode.getTitle().trim();
                if (fullTitle.startsWith("Democracy Now!")){
                    String title = fullTitle.substring(14).trim();
                    titleView.setText(title);
                }
                else {
                    titleView.setText(fullTitle);
                }
            }
            if (tagView != null) {
                String description = episode.getDescription().trim();
                if (description.startsWith("Headlines for ")) {
                    description = description.substring(description.indexOf(";") + 1);
                }
                tagView.setText(description);
                tagView.setEllipsize(TextUtils.TruncateAt.END);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadEpisode(episode);
                }
            });
            downloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Download");
                    builder.setMessage("Are you sure you want to download today's episode?");
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.setNegativeButton("Audio", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Download(episode.getAudioUrl(), episode.getTitle(), episode.getDescription());
                        }
                    });
                    builder.setPositiveButton("Video", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Download(episode.getVideoUrl(), episode.getTitle(), episode.getDescription());
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            );
            optionsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionsView.showContextMenu();
                }
            });
        }
    }

    private void loadEpisode(Episode e) {
        if (e != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
            int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this app
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
            ((Activity)itemView.getContext()).startActivityForResult(intent, 0); //Activity load = 0
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
        int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this app

        if (DEFAULT_STREAM == 0)
            menu.getItem(2).setTitle("Stream Audio");
        else
            menu.getItem(2).setTitle("Stream Video");

        if(DEFAULT_OPEN == 0)
            menu.getItem(3).setTitle("Stream in Another App");
        else
            menu.getItem(3).setTitle("Stream in This App");
        for (int i = 0;i<menu.size();i++){
            menu.getItem(i).setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
        int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this ap
        String actionTitle = "Democracy Now!";
        if (episode.getTitle().length() > 16){
            if("Today's Broadcast".equals(episode.getTitle())){
                actionTitle = episode.getTitle();
            } else if (episode.getTitle().startsWith("Democracy Now!")){
                actionTitle = episode.getTitle().substring(14);
            } else {
                actionTitle = episode.getTitle();
            }
        }

        switch(menuItem.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, episode.getTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, episode.getUrl());
                sendIntent.setType("text/plain");
                itemView.getContext().startActivity(sendIntent);
                return true;
            case R.id.reverse_default_media:
                if (episode.getVideoUrl().contains("m3u8"))
                    startMediaIntent(episode.getAudioUrl(), 1, episode.getTitle());
                else if (DEFAULT_STREAM == 0)
                    startMediaIntent(episode.getAudioUrl(), DEFAULT_OPEN, actionTitle);
                else
                    startMediaIntent(episode.getVideoUrl(), DEFAULT_OPEN, actionTitle);
                return true;
            case R.id.reverse_default_open:
                int reverseOpen = 0;
                if (reverseOpen == DEFAULT_OPEN)
                    reverseOpen = 1;
                if (DEFAULT_STREAM == 0)
                    startMediaIntent(episode.getVideoUrl(), reverseOpen, actionTitle);
                else
                    startMediaIntent(episode.getAudioUrl(), reverseOpen, actionTitle);
                return true;
            case R.id.action_description:
                AlertDialog description = new AlertDialog.Builder(itemView.getContext()).create();
                // Get Description and Title
                description.setTitle("The War and Peace Report");
                description.setMessage(episode.getDescription() + "\n\n" + episode.getTitle());
                description.setButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                description.show();
                return true;
            case R.id.video_download:
                if (episode.getTitle().equals("Stream Live"))
                    return true;
                Download(episode.getVideoUrl(), episode.getTitle(), episode.getDescription());
                return true;
            case R.id.audio_download:
                if (episode.getTitle().equals("Stream Live"))
                    return true;
                Download(episode.getAudioUrl(), episode.getTitle(), episode.getDescription());
                return true;
            case R.id.open_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(episode.getUrl()), "*/*");
                itemView.getContext().startActivity(intent);
                return true;
        }
        return false;
    }

    private void Download(String url, String title, String desc) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(itemView.getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ((Activity)itemView.getContext()).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return;
                // TODO: catch onRequestPermissionsResult
            }
        }
        if ("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/play/democracynow/playlist.m3u8".equals(url)) {
            Toast toast = Toast.makeText(itemView.getContext(),
                    "You can't download the Live Stream", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(desc);
        request.setTitle(title);
        request.allowScanningByMediaScanner();
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
