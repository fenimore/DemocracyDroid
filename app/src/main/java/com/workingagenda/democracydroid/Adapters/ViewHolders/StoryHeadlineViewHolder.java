package com.workingagenda.democracydroid.Adapters.ViewHolders;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

/**
 * Created by derrickrocha on 7/15/17.
 */
public class StoryHeadlineViewHolder extends StoryViewHolder {
    private final ImageView mImg;
    private TextView mTxt;

    public StoryHeadlineViewHolder(View itemView) {
        super(itemView);
        mImg = (ImageView) itemView.findViewById(R.id.row_image);
        mTxt = (TextView) itemView.findViewById(R.id.row_title);
    }

    public void showEpisode(Episode episode){
        if (episode != null) {


            try {
                Picasso.with(itemView.getContext()).load(episode.getImageUrl()).into(mImg);
            } catch (Exception ex) {
                Log.v("Story Adapter", "exception" + ex.toString());
            }

            if (mTxt != null) {
                mTxt.setText(episode.getTitle());
            }
            // Special formatting for headlines
            if ( episode.getTitle().startsWith("Headlines")){
                try {
                    Picasso.with(itemView.getContext()).load("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png").into(mImg);
                } catch (Exception e) {
                    Log.v("Story Adapter", "exception" + e.toString());
                }

            }
        }
    }
}
