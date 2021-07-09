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
package com.workingagenda.democracydroid.ui.adapter.viewholder;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.workingagenda.democracydroid.core.object.Episode;
import com.workingagenda.democracydroid.databinding.RowStoryBinding;

public class StoryViewHolder extends BaseStoryViewHolder {
    final RowStoryBinding binding;
    private final ImageView mImg;
    private final ImageView mOptions;
    private final TextView mTxt;

    public StoryViewHolder(final RowStoryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        mImg = binding.rowStoryImage;
        mTxt = binding.rowStoryTitle;
        mOptions = binding.rowStoryOptions;
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void showEpisode(final Episode episode) {
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
            itemView.setOnClickListener(view -> loadTranscript(episode));
            mOptions.setOnClickListener(view -> mOptions.showContextMenu());
        }
    }
}
