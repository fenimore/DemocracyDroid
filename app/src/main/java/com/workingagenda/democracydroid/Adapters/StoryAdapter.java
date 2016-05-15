package com.workingagenda.democracydroid.Adapters;

import android.content.Context;
import android.graphics.Color;
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

    public StoryAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    public StoryAdapter(Context context, int resource, List<Episode> episodes){
        super(context, resource, episodes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_story, null);
        }

        Episode b = getItem(position);
        if (b != null) {
            ImageView img = (ImageView) v.findViewById(R.id.row_image);
            TextView txt = (TextView) v.findViewById(R.id.row_title);
            try {
                Picasso.with(getContext()).load("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png").into(img); // TODO Change image
            } catch (Exception ex) {
                Log.v("Blog Adapter", "exception");
            }



            if (txt != null) {
                txt.setText(b.getTitle());

            }
            if ( b.getTitle().startsWith("Headlines")){
                txt.setAllCaps(true);
                txt.setTextColor(Color.parseColor("#670001"));
                txt.setPadding(0, 40, 0, 0);
                txt.setTextSize(17);
            }
        }

        return v;
    }
}
