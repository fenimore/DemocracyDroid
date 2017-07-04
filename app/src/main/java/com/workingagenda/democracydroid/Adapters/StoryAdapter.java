package com.workingagenda.democracydroid.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

import java.util.List;

/**
 * Created by fen on 4/10/16.
 */
public class StoryAdapter extends ArrayAdapter<Episode> {

    public StoryAdapter(Context context, int resource, List<Episode> episodes){
        super(context, resource, episodes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v;

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.row_story, null);

        Episode b = getItem(position);
        if (b != null) {
            ImageView img = (ImageView) v.findViewById(R.id.row_image);
            TextView txt = (TextView) v.findViewById(R.id.row_title);
            try {
                Picasso.with(getContext()).load(b.getImageUrl()).into(img);
            } catch (Exception ex) {
                Log.v("Story Adapter", "exception" + ex.toString());
            }

            if (txt != null) {
                txt.setText(b.getTitle());
            }
            // Special formatting for headlines
            if ( b.getTitle().startsWith("Headlines")){
                try {
                    Picasso.with(getContext()).load("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png").into(img); // TODO Change image
                } catch (Exception e) {
                    Log.v("Story Adapter", "exception" + e.toString());
                }
                assert txt != null;
                txt.setAllCaps(true);
                txt.setTypeface(txt.getTypeface(), Typeface.BOLD);
                txt.setPadding(0, 25, 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setMinimumHeight(img.getMinimumHeight());
                }
            }
        }

        return v;
    }
}
