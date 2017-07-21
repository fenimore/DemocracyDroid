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
            StoryViewHolder viewHolder = new StoryViewHolder(v);
            return viewHolder;
        }
        else {
            View v = mInflator.inflate(R.layout.row_story_headline, null);
            StoryHeadlineViewHolder viewHolder = new StoryHeadlineViewHolder(v);
            return viewHolder;
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
