package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.databinding.RowStoryHeadlineBinding;

/**
 * Created by derrickrocha on 7/15/17.
 */
public class StoryHeadlineViewHolder extends BaseStoryViewHolder {
    final RowStoryHeadlineBinding binding;
    private final ImageView mImg;
    private final ImageView mOptions;
    private final TextView mTxt;

    public StoryHeadlineViewHolder(final RowStoryHeadlineBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        mImg = binding.rowStoryHeadlineImage;
        mTxt = binding.rowStoryHeadlineTitle;
        mOptions = binding.rowStoryHeadlineOptions;
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void showEpisode(final Episode episode) {
        if (episode != null) {
            mEpisode = episode;
            if (mTxt != null) {
                mTxt.setText(episode.getTitle());
            }
            try {
                mImg.setImageURI(
                        Uri.parse("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png"));
            } catch (Exception e) {
                Log.v("Story Adapter", "exception" + e.toString());
            }
            mOptions.setOnClickListener(view -> mOptions.showContextMenu());
            itemView.setOnClickListener(view -> loadTranscript(episode));
        }
    }
}
