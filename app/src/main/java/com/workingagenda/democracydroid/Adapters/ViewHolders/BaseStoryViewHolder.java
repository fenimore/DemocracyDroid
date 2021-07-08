package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.app.Activity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.StoryActivity;

public abstract class BaseStoryViewHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    Episode mEpisode;

    BaseStoryViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(itemView.getContext());
        menu.setHeaderTitle(R.string.democracy_now);
        inflater.inflate(R.menu.blog_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_blog_description) {
            if (mEpisode == null) {
                return false;
            }
            new AlertDialog.Builder(itemView.getContext())
                    // Get Description and Title
                    .setTitle(R.string.democracy_now_story)
                    .setMessage(mEpisode.getDescription() + "\n\n" + mEpisode.getTitle())
                    .setPositiveButton(R.string.close, null)
                    .show();
            return true;
        }
        return false;
    }

    void loadTranscript(Episode story) {
        Intent intent = new Intent(itemView.getContext(), StoryActivity.class);
        intent.putExtra("url", story.getUrl());
        intent.putExtra("title", story.getTitle());
        intent.putExtra("date", story.getPubDate());
        ((Activity) itemView.getContext()).startActivityForResult(intent, 0); //Activity load = 0
    }

    public abstract void showEpisode(Episode episode);
}
