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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workingagenda.democracydroid.Adapters.ViewHolders.BaseStoryViewHolder;
import com.workingagenda.democracydroid.Adapters.ViewHolders.StoryHeadlineViewHolder;
import com.workingagenda.democracydroid.Adapters.ViewHolders.StoryViewHolder;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

import java.util.List;

/**
 * Created by fen on 4/10/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class StoryAdapter extends RecyclerView.Adapter<BaseStoryViewHolder> {

    private static final int TYPE_HEADLINE = 0;
    private static final int TYPE_EPISODE = 1;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private final List<Episode> mEpisodes;

    public StoryAdapter(Context context, List<Episode> episodes){
        mContext = context;
        mInflator = LayoutInflater.from(mContext);
        mEpisodes = episodes;
    }

    @Override
    public BaseStoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EPISODE) {
            View v = mInflator.inflate(R.layout.row_story, null);
            return new StoryViewHolder(v);
        }
        else {
            View v = mInflator.inflate(R.layout.row_story_headline, null);
            return new StoryHeadlineViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(BaseStoryViewHolder holder, int position) {
        holder.showEpisode(mEpisodes.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Episode episode = mEpisodes.get(position);
        if (episode.getTitle().startsWith("Headlines")){
            return TYPE_HEADLINE;
        }
        else{
            return TYPE_EPISODE;
        }
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }
}
