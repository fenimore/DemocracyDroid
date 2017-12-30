package com.workingagenda.democracydroid.screens.podcast;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workingagenda.democracydroid.Adapters.GridSpacingItemDecoration;
import com.workingagenda.democracydroid.util.DpToPixelHelper;
import com.workingagenda.democracydroid.MainApplication;
import com.workingagenda.democracydroid.Network.Podcast.GetAudioFeed;
import com.workingagenda.democracydroid.Network.Podcast.GetVideoFeed;
import com.workingagenda.democracydroid.Network.ServerApi;
import com.workingagenda.democracydroid.Network.Episode;
import com.workingagenda.democracydroid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class PodcastFragment extends Fragment {

    private View mProgress;
    private PodcastAdapter mPodcastAdapter;
    private ArrayList<Episode> mEpisodes;
    private final int LIVE_TIME = 8;
    private SimpleDateFormat mFormat;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ServerApi mServerApi;

    public void refresh() {
        getVideoFeed(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView mList = rootView.findViewById(R.id.recycler_view);
        mProgress = rootView.findViewById(R.id.progress_icon);

        mySwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        if (mySwipeRefreshLayout != null ) {
            mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
        }

        mEpisodes = new ArrayList<>();
        mServerApi = new ServerApi();

        mPodcastAdapter = new PodcastAdapter(getContext(), mEpisodes);
        mList.setAdapter(mPodcastAdapter);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.addItemDecoration(new GridSpacingItemDecoration(1, DpToPixelHelper.dpToPx(4,getResources().getDisplayMetrics()), true));
        mFormat = new SimpleDateFormat("yyyy-MMdd", Locale.US);
        getVideoFeed(true);

        return rootView;
    }

    private void getVideoFeed(boolean showLoading) {
        new GetVideoFeed(showLoading, mServerApi, new GetVideoFeed.GetVideoFeedCallback() {

            @Override
            public void onGetVideoFeedPreExecute(boolean showLoading) {
                mProgress.setVisibility(showLoading ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onGetVideoFeedPostExecute(ArrayList<Episode>episodes) {
                mProgress.setVisibility(View.GONE);
                showEpisodes(episodes);
                getAudioFeed();
            }
        }).execute();
    }

    private void getAudioFeed() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean hasSpanish = preferences.getBoolean("spanish_preference", false);
        String feed = "https://www.democracynow.org/podcast.xml";
        if (hasSpanish) {
            feed = "https://www.democracynow.org/podcast-es.xml";
        }
        new GetAudioFeed(new GetAudioFeed.GetAudioFeedCallback() {
            @Override
            public void onGetAudioFeedPreExecute() {

            }

            @Override
            public void onGetAudioFeedPostExecute(List<String> audioLinks) {
                if (audioLinks.size() < 1) {
                    Snackbar.make(mProgress,R.string.connect_error,Snackbar.LENGTH_INDEFINITE).show();
                }
                else {
                    if (mySwipeRefreshLayout != null) {
                        mySwipeRefreshLayout.setRefreshing(false);
                    }

                    TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
                    Calendar c = Calendar.getInstance(timeZone);
                    String formattedDate = mFormat.format(c.getTime());
                    String today_audio = "dn" + formattedDate;
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                    Log.v("Count A/V", String.valueOf(audioLinks.size()) + " / " + String.valueOf(mEpisodes.size()));

                    boolean onSchedule = (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && hourOfDay > LIVE_TIME - 1);
                    if (onSchedule && hourOfDay == LIVE_TIME) {
                        audioLinks.add(0, "http://democracynow.videocdn.scaleengine.net/" +
                                "democracynow-iphone/play/democracynow/playlist.m3u8");
                    } else if (onSchedule && audioLinks.size() > 0 &&
                            !audioLinks.get(0).contains(today_audio)) {
                        audioLinks.add(0, "http://traffic.libsyn.com/democracynow/" + today_audio + ".mp3");
                    }
                    int SIZE = Math.min(mEpisodes.size(), audioLinks.size());
                    for (int i = 0; i < SIZE; i++) {
                        mEpisodes.get(i).setAudioUrl(audioLinks.get(i));
                        Log.d("Episode", "Day: " + String.valueOf(dayOfWeek)
                                + " Hour: " + String.valueOf(hourOfDay));
                        Log.d("EpisodeVideo", mEpisodes.get(i).getVideoUrl());
                        Log.d("EpisodeAudio", mEpisodes.get(i).getAudioUrl());
                    }
                    mProgress.setVisibility(View.GONE);
                    mPodcastAdapter.notifyDataSetChanged();
                }
            }
        }).execute(feed); // must be called onPostExecute
    }

    private void showEpisodes(ArrayList<Episode> episodes) {
        mEpisodes.clear();
        mEpisodes.addAll(episodes);
        mPodcastAdapter.notifyDataSetChanged();
    }

}
