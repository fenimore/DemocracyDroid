package com.workingagenda.democracydroid.Network.Podcast;

import android.os.AsyncTask;

import com.workingagenda.democracydroid.Network.ServerApi;
import com.workingagenda.democracydroid.Network.Episode;

import java.util.ArrayList;

/**
 * Created by derrickrocha on 8/28/17.
 */

public class GetVideoFeed extends AsyncTask<Object,Void,ArrayList<Episode>> {

    private final GetVideoFeedCallback mCallback;
    private final ServerApi mServerApi;
    private final boolean mShowLoading;

    public GetVideoFeed(boolean showLoading,ServerApi serverApi,GetVideoFeedCallback callback){
        mCallback = callback;
        mServerApi = serverApi;
        mShowLoading = showLoading;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onGetVideoFeedPreExecute(mShowLoading);
    }

    @Override
    protected ArrayList<Episode> doInBackground(Object... objects) {
        ArrayList<Episode> epis = new ArrayList<>();
        try {
            epis = mServerApi.getVideoFeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epis;    }

    @Override
    protected void onPostExecute(ArrayList<Episode> result) {
        super.onPostExecute(result);
        mCallback.onGetVideoFeedPostExecute(result);
    }

    public interface GetVideoFeedCallback{

        void onGetVideoFeedPreExecute(boolean showLoading);

        void onGetVideoFeedPostExecute(ArrayList<Episode> result);
    }

}
