package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
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
import com.workingagenda.democracydroid.StoryActivity;

/**
 * Created by derrickrocha on 7/9/17.
 */
public class StoryViewHolder extends BaseStoryViewHolder {
    private final ImageView mImg;
    private final ImageView mOptions;
    private TextView mTxt;

    public StoryViewHolder(final View itemView) {
        super(itemView);
        mImg = (ImageView) itemView.findViewById(R.id.row_image);
        mTxt = (TextView) itemView.findViewById(R.id.row_title);
        mOptions = (ImageView)itemView.findViewById(R.id.row_options);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void showEpisode(final Episode episode){
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadTranscript(episode);
                }
            });
            mOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOptions.showContextMenu();
                }
            });
        }
    }

    private void loadTranscript(Episode story) { //author does'nt work
        Intent intent = new Intent(itemView.getContext(), StoryActivity.class);
        intent.putExtra("url", story.getUrl()); //can't pass in article object?
        intent.putExtra("title", story.getTitle());
        intent.putExtra("date", story.getPubDate());
        ((Activity)itemView.getContext()).startActivityForResult(intent, 0); //Activity load = 0
    }

}
