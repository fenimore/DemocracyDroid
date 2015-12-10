package com.workingagenda.democracydroid;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
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
            v = vi.inflate(R.layout.episode_row, null);
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
