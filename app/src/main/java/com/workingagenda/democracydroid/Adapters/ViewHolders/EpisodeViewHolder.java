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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.workingagenda.democracydroid.MediaActivity;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

import static android.speech.tts.TextToSpeech.Engine.DEFAULT_STREAM;

/**
 * Created by derrickrocha on 7/16/17.
 */
public class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener {

    private final TextView txt;
    private final ImageView img;
    private final TextView tag;
    private final ImageView mOptions;
    private final ImageView mDownload;
    // ENUMS
    public static final int STREAM_VIDEO = 0;
    public static final int STREAM_AUDIO = 1;
    public static final int OPEN_THIS_APP = 0;
    private Episode mEpisode;

    public EpisodeViewHolder(final View itemView) {
        super(itemView);
        img = (ImageView) itemView.findViewById(R.id.row_image);
        txt = (TextView) itemView.findViewById(R.id.row_title);
        tag = (TextView) itemView.findViewById(R.id.row_tag);
        tag.setMaxLines(3);
        mOptions = (ImageView)itemView.findViewById(R.id.row_options);
        mDownload = (ImageView)itemView.findViewById(R.id.row_download);
        itemView.setOnCreateContextMenuListener(this);

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
                if (fullTitle.startsWith("Democracy Now!")){
                    String title = fullTitle.substring(14).trim();
                    txt.setText(title);
                }
                else {
                    txt.setText(fullTitle);
                }
            }
            if (tag != null) {
                String description = e.getDescription().trim();
                String tagString = description.substring(description.indexOf(";") + 1).trim();
                tag.setText(tagString);
                tag.setEllipsize(TextUtils.TruncateAt.END);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadEpisode(e);
                }
            });
            mDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog description = new AlertDialog.Builder(itemView.getContext()).create();
                    // Get Description and Title
                    description.setTitle("Download");
                    description.setMessage("Are you sure you want to download today's episode?");
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE){
                                return;
                            }

                            if (DEFAULT_STREAM == STREAM_VIDEO)
                                Download(e.getVideoUrl(), e.getTitle(), e.getDescription());
                            else if (DEFAULT_STREAM == STREAM_AUDIO)
                                Download(e.getAudioUrl(), e.getTitle(), e.getDescription());
                            else
                                Download(e.getVideoUrl(), e.getTitle(), e.getDescription());
                        }
                    };
                    description.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", listener);
                    description.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", listener);
                    description.show();
                }
            }
            );
            mOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOptions.showContextMenu();
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
        if (mEpisode.getTitle().length() > 16){
            if("Today's Broadcast".equals(mEpisode.getTitle())){
                actionTitle = mEpisode.getTitle();
            } else if (mEpisode.getTitle().startsWith("Democracy Now!")){
                actionTitle = mEpisode.getTitle().substring(14);
            } else {
                actionTitle = mEpisode.getTitle();
            }
        }

        switch(menuItem.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, mEpisode.getTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, mEpisode.getUrl());
                sendIntent.setType("text/plain");
                itemView.getContext().startActivity(sendIntent);
                return true;
            case R.id.reverse_default_media:
                if (mEpisode.getVideoUrl().contains("m3u8"))
                    startMediaIntent(mEpisode.getAudioUrl(), 1, mEpisode.getTitle());
                else if (DEFAULT_STREAM == 0)
                    startMediaIntent(mEpisode.getAudioUrl(), DEFAULT_OPEN, actionTitle);
                else
                    startMediaIntent(mEpisode.getVideoUrl(), DEFAULT_OPEN, actionTitle);
                return true;
            case R.id.reverse_default_open:
                int reverseOpen = 0;
                if (reverseOpen == DEFAULT_OPEN)
                    reverseOpen = 1;
                if (DEFAULT_STREAM == 0)
                    startMediaIntent(mEpisode.getVideoUrl(), reverseOpen, actionTitle);
                else
                    startMediaIntent(mEpisode.getAudioUrl(), reverseOpen, actionTitle);
                return true;
            case R.id.action_description:
                AlertDialog description = new AlertDialog.Builder(itemView.getContext()).create();
                // Get Description and Title
                description.setTitle("The War and Peace Report");
                description.setMessage(mEpisode.getDescription() + "\n\n" + mEpisode.getTitle());
                description.setButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                description.show();
                return true;
            case R.id.video_download:
                if (mEpisode.getTitle().equals("Stream Live"))
                    return true;
                Download(mEpisode.getVideoUrl(), mEpisode.getTitle(), mEpisode.getDescription());
                return true;
            case R.id.audio_download:
                if (mEpisode.getTitle().equals("Stream Live"))
                    return true;
                Download(mEpisode.getAudioUrl(), mEpisode.getTitle(), mEpisode.getDescription());
                return true;
            case R.id.open_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mEpisode.getUrl()), "*/*");
                itemView.getContext().startActivity(intent);
                return true;
        }
        return false;
    }

    // FIXME: Show progress:
    // http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
    public void Download(String url, String title, String desc) {
        if (ContextCompat.checkSelfPermission(itemView.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ((Activity)itemView.getContext()).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            // TODO: catch onRequestPermissionsResult
        } else {
            if (url.equals("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/play/democracynow/playlist.m3u8")) {
                Toast toast = Toast.makeText(itemView.getContext(),
                        "You can't download the Live Stream", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(desc);
            request.setTitle(title);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
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
}
