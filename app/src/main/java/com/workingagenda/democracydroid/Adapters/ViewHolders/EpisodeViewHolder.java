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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.workingagenda.democracydroid.MediaActivity;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

public class EpisodeViewHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    // ENUMS
    private static final int STREAM_VIDEO = 0;
    private static final int STREAM_AUDIO = 1;
    private static final int OPEN_THIS_APP = 0;
    private final TextView txt;
    private final ImageView img;
    private final TextView tag;
    private final ImageView mOptions;
    private Episode mEpisode;

    public EpisodeViewHolder(final View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.row_episodes_image);
        txt = itemView.findViewById(R.id.row_episodes_title);
        tag = itemView.findViewById(R.id.row_episodes_tag);
        tag.setMaxLines(3);
        mOptions = itemView.findViewById(R.id.row_episodes_options);
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadEpisode(e);
                }
            });
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
        int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this app

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

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
        int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this ap
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
            case R.id.menu_context_open_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mEpisode.getUrl()), "*/*");
                itemView.getContext().startActivity(intent);
                return true;
        }
        return false;
    }
}
