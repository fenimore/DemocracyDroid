/*
 *
 *   Copyright (C) 2014-2015 Fenimore Love
 *
 *   This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.workingagenda.democracydroid.Adapters;

import android.content.Context;
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
 * Created by fen on 12/9/15.
 */
public class EpisodeAdapter extends ArrayAdapter<Episode> {

    public EpisodeAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    public EpisodeAdapter(Context context, int resource, List<Episode> episodes){
        super(context, resource, episodes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_episodes, null);
            Log.d("Set click?", "whats the deal");
            //v.setLongClickable(true);
            //v.setClickable(true);
        }

        Episode e = getItem(position);
        if (e != null) {
            ImageView img = (ImageView) v.findViewById(R.id.row_image);
            TextView txt = (TextView) v.findViewById(R.id.row_title);
            try {
                Picasso.with(getContext()).load(e.getImageUrl()).into(img);
                //Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //img.setImageBitmap(image);
            } catch (Exception ex) {
                Log.v("Episode Adapter", "exception");
            }

            if (txt != null) {
                if (e.getTitle().length() > 16){
                    txt.setText(e.getTitle().substring(14));
                } else {
                    txt.setText(e.getTitle());
                }
            }
        }

        return v;
    }

}
