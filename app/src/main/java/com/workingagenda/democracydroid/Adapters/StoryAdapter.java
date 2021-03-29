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
package com.workingagenda.democracydroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.workingagenda.democracydroid.Adapters.ViewHolders.BaseStoryViewHolder;
import com.workingagenda.democracydroid.Adapters.ViewHolders.StoryHeadlineViewHolder;
import com.workingagenda.democracydroid.Adapters.ViewHolders.StoryViewHolder;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.databinding.RowStoryBinding;
import com.workingagenda.democracydroid.databinding.RowStoryHeadlineBinding;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<BaseStoryViewHolder> {

    private static final int TYPE_HEADLINE = 0;
    private static final int TYPE_EPISODE = 1;
    private final LayoutInflater mInflator;
    private final List<Episode> mEpisodes;

    public StoryAdapter(Context context, List<Episode> episodes) {
        mInflator = LayoutInflater.from(context);
        mEpisodes = episodes;
    }

    @NonNull
    @Override
    public BaseStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EPISODE) {
            return new StoryViewHolder(RowStoryBinding.inflate(mInflator));
        } else {
            return new StoryHeadlineViewHolder(RowStoryHeadlineBinding.inflate(mInflator));
        }
    }

    @Override
    public void onBindViewHolder(BaseStoryViewHolder holder, int position) {
        holder.showEpisode(mEpisodes.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Episode episode = mEpisodes.get(position);
        if (episode.getTitle().startsWith("Headlines")) {
            return TYPE_HEADLINE;
        } else {
            return TYPE_EPISODE;
        }
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }
}
