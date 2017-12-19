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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.workingagenda.democracydroid.R;

import java.io.File;
import java.util.List;

/**
 * Created by fen on 12/9/15.
 */
@SuppressWarnings("DefaultFileTemplate")
public class DownloadsAdapter extends ArrayAdapter<File> {

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
            ImageView img = v.findViewById(R.id.row_image);
            TextView txt = v.findViewById(R.id.row_title);
            String title = f.getName();
            Boolean isVideo = false;

            if (txt != null && title.startsWith("dn")){
                title = title.substring(0, title.length() - 4);
                title = title.substring(2, title.length());
                title = title.substring(0, 7) + "-" + title.substring(7, title.length());
                if (title.endsWith("-1"))
                    title = title.substring(0, title.length()-2);
                txt.setText(title);
            } else if (txt != null) {
                title = title.substring(0, title.length() - 12);
                txt.setText(title);
            }

            if(f.getName().endsWith(".mp4")){
                isVideo = true;
            }
            if (isVideo) {
                img.setImageResource(R.drawable.ic_movie_black_24dp);
            } else {
                img.setImageResource(R.drawable.ic_mic_none_black_24dp);
            }
        }

        return v;
    }

}
