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

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.workingagenda.democracydroid.Network.Episode;
import com.workingagenda.democracydroid.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryAdapter extends RecyclerView.Adapter<BaseStoryViewHolder> {

    private static final int TYPE_HEADLINE = 0;
    private static final int TYPE_EPISODE = 1;
    private final LayoutInflater inflater;
    private final List<Episode> episodes;
    private final Context context;

    public StoryAdapter(Context context, List<Episode> episodes){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.episodes = episodes;
    }

    @Override
    public BaseStoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EPISODE) {
            View v = inflater.inflate(R.layout.row_story, null);
            return new StoryViewHolder(v);
        }
        else {
            View v = inflater.inflate(R.layout.row_story_headline, null);
            return new StoryHeadlineViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(BaseStoryViewHolder holder, int position) {
        holder.showEpisode(episodes.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Episode episode = episodes.get(position);
        if (episode.getTitle().startsWith("Headlines")){
            return TYPE_HEADLINE;
        }
        else{
            return TYPE_EPISODE;
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    class StoryHeadlineViewHolder extends BaseStoryViewHolder {

        @BindView(R.id.row_headline_image) ImageView mImg;
        @BindView(R.id.row_options) ImageView mOptions;
        @BindView(R.id.row_title) TextView mTxt;

        StoryHeadlineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void showEpisode(final Episode episode){
            if (episode != null) {
                if (mTxt != null) {
                    mTxt.setText(episode.getTitle());
                }
                try {
                    mImg.setImageURI(Uri.parse(context.getResources().getString(R.string.democracy_now_logo_url)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOptions.showContextMenu();
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadTranscript(episode);
                    }
                });
            }
        }

        @Override
        public Episode currentEpisode() {
            return episodes.get(getAdapterPosition());
        }
    }

    class StoryViewHolder extends BaseStoryViewHolder {

        @BindView(R.id.row_image) ImageView image;
        @BindView(R.id.row_options) ImageView options;
        @BindView(R.id.row_title) TextView text;

         StoryViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void showEpisode(final Episode episode){
            if (episode != null) {
                try {
                    image.setImageURI(Uri.parse(episode.getImageUrl()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                text.setText(episode.getTitle());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadTranscript(episode);
                    }
                });
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        options.showContextMenu();
                    }
                });
            }
        }

        @Override
        public Episode currentEpisode() {
            return episodes.get(getAdapterPosition());
        }

    }
}
