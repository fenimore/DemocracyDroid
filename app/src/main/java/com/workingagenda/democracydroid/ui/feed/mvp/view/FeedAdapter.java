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
package com.workingagenda.democracydroid.ui.feed.mvp.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workingagenda.democracydroid.ui.feed.mvp.view.ViewHolders.BaseStoryViewHolder;
import com.workingagenda.democracydroid.ui.feed.mvp.view.ViewHolders.EpisodeViewHolder;
import com.workingagenda.democracydroid.ui.feed.mvp.view.ViewHolders.StoryHeadlineViewHolder;
import com.workingagenda.democracydroid.ui.feed.mvp.view.ViewHolders.StoryViewHolder;
import com.workingagenda.democracydroid.Network.Episode;
import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.ui.feed.FeedType;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<BaseStoryViewHolder> {

    private static final int TYPE_HEADLINE = 0;
    private static final int TYPE_EPISODE = 1;
    private final LayoutInflater mInflator;
    private final List<Episode> mEpisodes;
    private final FeedType mFeedType;

    public FeedAdapter(Context context, List<Episode> episodes, FeedType feedType){
        mInflator = LayoutInflater.from(context);
        mEpisodes = episodes;
        mFeedType = feedType;
    }

    @Override
    public BaseStoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mFeedType.equals(FeedType.VIDEO)){
            View v = mInflator.inflate(R.layout.row_episodes, null);
            return new EpisodeViewHolder(v);
        }
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
