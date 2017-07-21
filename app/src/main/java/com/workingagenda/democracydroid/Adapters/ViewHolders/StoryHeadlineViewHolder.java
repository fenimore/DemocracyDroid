package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

/**
 * Created by derrickrocha on 7/15/17.
 */
public class StoryHeadlineViewHolder extends BaseStoryViewHolder {

    private final ImageView mImg;
    private final ImageView mOptions;
    private TextView mTxt;

    public StoryHeadlineViewHolder(View itemView) {
        super(itemView);
        mImg = (ImageView) itemView.findViewById(R.id.row_headline_image);
        mTxt = (TextView) itemView.findViewById(R.id.row_title);
        mOptions = (ImageView)itemView.findViewById(R.id.row_options);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void showEpisode(Episode episode){
        if (episode != null) {
            mEpisode = episode;
            if (mTxt != null) {
                mTxt.setText(episode.getTitle());
            }
            try {
                mImg.setImageURI(Uri.parse("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png"));
            } catch (Exception e) {
                Log.v("Story Adapter", "exception" + e.toString());
            }
            mOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOptions.showContextMenu();
                }
            });
        }
    }
}
