package com.workingagenda.democracydroid.core.network.podcast;

import android.os.AsyncTask;

import com.workingagenda.democracydroid.core.network.ServerApi;
import com.workingagenda.democracydroid.core.object.Episode;

import java.util.ArrayList;

public class GetVideoFeed extends AsyncTask<Object, Void, ArrayList<Episode>> {

    private final GetVideoFeedCallback mCallback;
    private final ServerApi mServerApi;
    private final boolean mShowLoading;

    public GetVideoFeed(boolean showLoading, ServerApi serverApi, GetVideoFeedCallback callback) {
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
        ArrayList<Episode> episodeList = new ArrayList<>();
        try {
            episodeList = mServerApi.getVideoFeed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return episodeList;
    }

    @Override
    protected void onPostExecute(ArrayList<Episode> result) {
        super.onPostExecute(result);
        mCallback.onGetVideoFeedPostExecute(result);
    }

    public interface GetVideoFeedCallback {
        void onGetVideoFeedPreExecute(boolean showLoading);

        void onGetVideoFeedPostExecute(ArrayList<Episode> result);
    }
}
