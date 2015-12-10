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
                URL url = new URL(e.getImageUrl());

                //Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //img.setImageBitmap(image);
            } catch (MalformedURLException ex) {
                Log.v("EpisodeAdapter","malformed URL");
            } catch (IOException ex) {
                Log.v("Episode Adapter", "io exception");
            } catch (Exception ex) {
                Log.v("Episode Adapter", "exception");
            }

            if (txt != null) {
                if (e.getTitle().length() > 15){
                    txt.setText(e.getTitle().substring(14));
                } else {
                    txt.setText(e.getTitle());
                }
            }
        }

        return v;
    }

}

class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;

    public BitmapWorkerTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return null;
        //return decodeSampledBitmapFromResource(getResources(), data, 100, 100));
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
