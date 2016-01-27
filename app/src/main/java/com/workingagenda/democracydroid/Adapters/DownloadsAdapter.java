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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.workingagenda.democracydroid.R;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by fen on 12/9/15.
 */
public class DownloadsAdapter extends ArrayAdapter<File> {

    public DownloadsAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    public DownloadsAdapter(Context context, int resource, List<File> files){
        super(context, resource, files);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_download, null);
        }

        File f = getItem(position);
        if (f != null) {
            ImageView img = (ImageView) v.findViewById(R.id.row_image);
            TextView txt = (TextView) v.findViewById(R.id.row_title);
            String title = f.getName();
            Boolean isVideo = false;
            try {
                //Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //img.setImageBitmap(image);
            } catch (Exception ex) {
                Log.v("Episode Adapter", "exception");
            }

            if (txt != null) {
                txt.setText(f.getName());
            }
            if(f.getName().endsWith(".mp4")){
                isVideo = true;
            }
            if(isVideo){
                img.setImageResource(R.drawable.ic_movie_black_24dp);
            } else {
                img.setImageResource(R.drawable.ic_mic_none_black_24dp);
            }
        }

        return v;
    }

}