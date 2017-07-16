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
public class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    private final ImageView mImg;
    private final ImageView mOptions;
    private TextView mTxt;
    private Episode mEpisode;

    public StoryViewHolder(final View itemView) {
        super(itemView);
        mImg = (ImageView) itemView.findViewById(R.id.row_image);
        mTxt = (TextView) itemView.findViewById(R.id.row_title);
        mOptions = (ImageView)itemView.findViewById(R.id.row_options);
        itemView.setOnCreateContextMenuListener(this);

    }

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(itemView.getContext());
        menu.setHeaderTitle("Democracy Now!");
        inflater.inflate(R.menu.blog_menu,menu);
        menu.getItem(0).setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.action_blog_description:
                AlertDialog description = new AlertDialog.Builder(itemView.getContext()).create();
                // Get Description and Title
                description.setTitle("Democracy Now! Story");
                description.setMessage(mEpisode.getDescription() + "\n\n" + mEpisode.getTitle());
                description.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                description.show();
                return true;
        }
        return false;
    }
}
