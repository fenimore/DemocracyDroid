package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

/**
 * Created by derrickrocha on 7/21/17.
 */
public abstract class BaseStoryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
    protected Episode mEpisode;

    public BaseStoryViewHolder(View itemView) {
        super(itemView);
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
                if (mEpisode == null)
                    return false;
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

    public abstract void showEpisode(Episode episode);
}
