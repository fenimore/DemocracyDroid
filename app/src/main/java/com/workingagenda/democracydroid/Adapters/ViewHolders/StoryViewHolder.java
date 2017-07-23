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
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.StoryActivity;

/**
 * Created by derrickrocha on 7/9/17.
 */
public class StoryViewHolder extends BaseStoryViewHolder {
    private final ImageView mImg;
    private final ImageView mOptions;
    private TextView mTxt;

    public StoryViewHolder(final View itemView) {
        super(itemView);
        mImg = (ImageView) itemView.findViewById(R.id.row_image);
        mTxt = (TextView) itemView.findViewById(R.id.row_title);
        mOptions = (ImageView)itemView.findViewById(R.id.row_options);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void showEpisode(final Episode episode){
        if (episode != null) {
            mEpisode = episode;
            try {
                mImg.setImageURI(Uri.parse(episode.getImageUrl()));
            } catch (Exception ex) {
                Log.v("Story Adapter", "exception" + ex.toString());
            }
            if (mTxt != null) {
                mTxt.setText(episode.getTitle());
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadTranscript(episode);
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

    private void loadTranscript(Episode story) { //author does'nt work
        Intent intent = new Intent(itemView.getContext(), StoryActivity.class);
        intent.putExtra("url", story.getUrl()); //can't pass in article object?
        intent.putExtra("title", story.getTitle());
        intent.putExtra("date", story.getPubDate());
        ((Activity)itemView.getContext()).startActivityForResult(intent, 0); //Activity load = 0
    }

}
