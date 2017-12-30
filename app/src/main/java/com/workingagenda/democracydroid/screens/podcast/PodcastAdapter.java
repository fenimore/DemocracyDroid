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
package com.workingagenda.democracydroid.screens.podcast;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.workingagenda.democracydroid.Adapters.ViewHolders.EpisodeViewHolder;
import com.workingagenda.democracydroid.Network.Episode;
import com.workingagenda.democracydroid.R;

import java.util.List;

public class PodcastAdapter extends RecyclerView.Adapter<EpisodeViewHolder> {
    private final LayoutInflater mInflator;
    private final List<Episode> mEpisodes;

    public PodcastAdapter(Context context, List<Episode> episodes){
        mEpisodes = episodes;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflator.inflate(R.layout.row_episodes, null);
        return new EpisodeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        Episode episode = mEpisodes.get(position);
        holder.showEpisode(episode);
    }


    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }
}
