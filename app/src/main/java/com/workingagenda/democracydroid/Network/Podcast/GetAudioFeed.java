package com.workingagenda.democracydroid.Network.Podcast;

import android.os.AsyncTask;

import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;

import java.util.ArrayList;
import java.util.List;

public class GetAudioFeed extends AsyncTask<String, Void, List<String>> {

    private final GetAudioFeedCallback mCallback;

    public GetAudioFeed(GetAudioFeedCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onGetAudioFeedPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... params) {
        RssReader rssReader = new RssReader(params[0]);

        ArrayList<String> audio = new ArrayList<>();
        try {
            for (RssItem item : rssReader.getItems())
                audio.add(item.getVideoUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return audio;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        mCallback.onGetAudioFeedPostExecute(strings);
    }

    public interface GetAudioFeedCallback {
        void onGetAudioFeedPreExecute();

        void onGetAudioFeedPostExecute(List<String> strings);
    }
}
