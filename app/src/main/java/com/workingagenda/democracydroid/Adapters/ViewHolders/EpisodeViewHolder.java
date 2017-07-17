package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

/**
 * Created by derrickrocha on 7/16/17.
 */
public class EpisodeViewHolder extends RecyclerView.ViewHolder{

    private final TextView txt;
    private final ImageView img;
    private final TextView tag;
    private boolean PREF_DESC;


    public EpisodeViewHolder(View itemView) {
        super(itemView);
        img = (ImageView) itemView.findViewById(R.id.row_image);
        txt = (TextView) itemView.findViewById(R.id.row_title);
        tag = (TextView) itemView.findViewById(R.id.row_tag);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        PREF_DESC = preferences.getBoolean("desc_preference", true);
    }

    public void showEpisode(Episode e) {


        if (e != null) {

            try {
                img.setImageURI(Uri.parse(e.getImageUrl()));
            } catch (Exception ex) {
                Log.v("Episode Adapter", "exception");
            }

            if (txt != null) {
                if (e.getTitle().length() > 16){
                    if("Today's Broadcast".equals(e.getTitle())){
                        txt.setText(e.getTitle());
                    } else if (e.getTitle().startsWith("Democracy Now!")){
                        txt.setText(e.getTitle().substring(14));
                    } else {
                        txt.setText(e.getTitle());
                    }
                } else {
                    txt.setText(e.getTitle());
                }
            }
            if (tag != null && PREF_DESC) {
                if (e.getDescription() != null) {
                    tag.setText(e.getDescription().substring(e.getDescription().indexOf(";") + 2));
                }
            }


        }
    }
}
