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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.workingagenda.democracydroid.Adapters.ViewHolders.EpisodeViewHolder;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

import java.util.List;

/**
 * Created by fen on 12/9/15.
 */
public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeViewHolder> {
    private final LayoutInflater mInflator;
    private final List<Episode> mEpisodes;
    private boolean PREF_DESC;

    public EpisodeAdapter(Context context, List<Episode> episodes){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        PREF_DESC = preferences.getBoolean("desc_preference", true);
        mEpisodes = episodes;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflator.inflate(R.layout.row_episodes, null);
        EpisodeViewHolder viewHolder = new EpisodeViewHolder(v);
        return viewHolder;
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
