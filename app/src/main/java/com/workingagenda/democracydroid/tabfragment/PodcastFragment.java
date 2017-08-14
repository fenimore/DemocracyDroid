package com.workingagenda.democracydroid.tabfragment;

/**
 * Created by derrickrocha on 7/16/17.
 */

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.workingagenda.democracydroid.Adapters.EpisodeAdapter;
import com.workingagenda.democracydroid.Adapters.GridSpacingItemDecoration;
import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;
import com.workingagenda.democracydroid.Helpers.DpToPixelHelper;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 */
public class PodcastFragment extends Fragment {

    //Declare some variables
    private RecyclerView mList;
    private TextView mTxt;
    private RelativeLayout mProgress;
    private EpisodeAdapter episodeAdapter;
    private ArrayList<Episode> mEpisodes;
    private int LIVE_TIME = 8;
    private SimpleDateFormat mFormat;


    private SwipeRefreshLayout mySwipeRefreshLayout;

    // Episode objects!!!

    // set up custom adapter with episodes
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PodcastFragment() {
    }

    public void refresh() {
        mySwipeRefreshLayout.setRefreshing(true);
        if (mEpisodes.size() > 1){
            mEpisodes.clear();
            episodeAdapter.notifyDataSetChanged();
        }
        new GetVideoFeed().execute("https://www.democracynow.org/podcast-video.xml");
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PodcastFragment newInstance(int sectionNumber) {
        PodcastFragment fragment = new PodcastFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Find View Items
        mList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mTxt = (TextView) rootView.findViewById(android.R.id.empty);
        mProgress = (RelativeLayout) rootView.findViewById(R.id.progess_layout);
        mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mEpisodes = new ArrayList<>();
        //registerForContextMenu(mList);
        // Callback calls GetAudioFeed

        episodeAdapter = new EpisodeAdapter(getContext(), mEpisodes);
        mList.setAdapter(episodeAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        mList.setLayoutManager(layoutManager);
        mList.addItemDecoration(new GridSpacingItemDecoration(1, DpToPixelHelper.dpToPx(4,getResources().getDisplayMetrics()), true));        mFormat = new SimpleDateFormat("yyyy-MMdd");
        new PodcastFragment.GetVideoFeed().execute("https://www.democracynow.org/podcast-video.xml");
        if (mySwipeRefreshLayout != null ) {
            mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mTxt.setText("");
                    refresh();
                }
            });
        }

        return rootView;
    }


    private class GetVideoFeed extends AsyncTask<String, Void, ArrayList<Episode>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Episode> doInBackground(String... params) {
            ArrayList<Episode> episodes = new ArrayList<>();
            try {
                episodes.addAll(parseVideoFeed(params[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return episodes;
        }

        @Override
        protected void onPostExecute(ArrayList<Episode> episodes) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String feed = "https://www.democracynow.org/podcast.xml";
            if (preferences.getBoolean("spanish_preference", false)) {
                feed = "https://www.democracynow.org/podcast-es.xml";
            }
            mEpisodes.clear();
            mEpisodes.addAll(episodes);

            new PodcastFragment.GetAudioFeed().execute(feed); // must be called onPostExecute
        }
    }

    private ArrayList<Episode> parseVideoFeed(String url) throws Exception {
        ArrayList<Episode> epis = new ArrayList<>();
        RssReader rssReader = new RssReader(url);
        for(RssItem item : rssReader.getItems()){
            Episode e = new Episode();
            String title = item.getTitle();
            e.setTitle(title);
            e.setVideoUrl(item.getVideoUrl());
            String description = item.getDescription();
            e.setDescription(description);
            e.setImageUrl(item.getImageUrl());
            e.setUrl(item.getLink());
            epis.add(e);
        }

        return checkLiveStream(epis); // and add video in link
        // not yet in RSS feed ;)
    }

    private ArrayList<Episode> checkLiveStream(ArrayList<Episode> epis) {
        // Make it Pretty, and NY eastern Time
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        Calendar c = Calendar.getInstance(timeZone);
        String formattedDate = mFormat.format(c.getTime());
        String todayVid1 = "http://hot.dvlabs.com/democracynow/video-podcast/dn"
                + formattedDate + ".mp4";
        String todayVid2 = "http://publish.dvlabs.com/democracynow/video-podcast/dn"
                + formattedDate + ".mp4";
        String todayAudio = "http://traffic.libsyn.com/democracynow/dn"
                + formattedDate + "-1.mp3";
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return epis;
        if (todayVid1.equals(epis.get(0).getVideoUrl())) return epis;
        if (todayVid2.equals(epis.get(0).getVideoUrl()))return epis;
        if (hourOfDay < LIVE_TIME) return epis;
        Episode episode = getUnlistedStream(hourOfDay, todayAudio, todayVid2);
        epis.add(0, episode);
        return epis;
    }

    private Episode getUnlistedStream(int hour, String audio, String vid){
        // Live Stream
        Episode todaysEpisode = new Episode();
        todaysEpisode.setDescription("Stream Live between 8 and 9 weekdays Eastern time, " +
                "the War and Peace Report");
        todaysEpisode.setImageUrl("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/" +
                "Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png");
        todaysEpisode.setUrl("http://m.democracynow.org/");
        if ( LIVE_TIME == hour ){
            todaysEpisode.setTitle("Stream Live");//"Stream Live");
            todaysEpisode.setVideoUrl("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/" +
                    "play/democracynow/playlist.m3u8");
            todaysEpisode.setAudioUrl("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/" +
                    "play/democracynow/playlist.m3u8");
        } else if ( hour > 8) {
            // Add Todays Broadcast even if RSS feed isn't updated yet
            todaysEpisode.setTitle("Today's Broadcast");
            todaysEpisode.setDescription("Democracy Now! The War and Peace Report");
            todaysEpisode.setVideoUrl(vid);
            todaysEpisode.setAudioUrl(audio);
        }
        return todaysEpisode;
    }

    private class GetAudioFeed extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            RssReader rssReader = new RssReader(params[0]);

            ArrayList<String> audio = new ArrayList<>();
            try {
                for(RssItem item : rssReader.getItems())
                    audio.add(item.getVideoUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return audio;
        }

        @Override
        protected void onPostExecute(List<String> audioLinks) {
            if (audioLinks.size() < 1) {
                mTxt.setText(R.string.connect_error);
            }
            if (mySwipeRefreshLayout != null ) {
                mySwipeRefreshLayout.setRefreshing(false);
            }

            TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
            Calendar c = Calendar.getInstance(timeZone);
            String formattedDate = mFormat.format(c.getTime());
            String today_audio = "http://traffic.libsyn.com/democracynow/dn"
                    + formattedDate + "-1.mp3";
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int hourOfDay= c.get(Calendar.HOUR_OF_DAY);
            Log.v("Count A/V", String.valueOf(audioLinks.size()) +" / "+ String.valueOf(mEpisodes.size()));

            boolean onSchedule = (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY  && hourOfDay > LIVE_TIME-1);
            if (onSchedule && hourOfDay == LIVE_TIME) {
                audioLinks.add(0, "http://democracynow.videocdn.scaleengine.net/" +
                        "democracynow-iphone/play/democracynow/playlist.m3u8");
            } else if (onSchedule && audioLinks.size() > 0 && !audioLinks.get(0).equals(today_audio)) {
                audioLinks.add(0, today_audio);
            }
            int SIZE = Math.min(mEpisodes.size(), audioLinks.size());
            for (int i =0; i < SIZE; i++) {
                mEpisodes.get(i).setAudioUrl(audioLinks.get(i));
                // FIXME: Audio has one more item than video?
                // Log.d("Episode:", "\n" + mEpisodes.get(i).getAudioUrl()+ "\n"+ mEpisodes.get(i).getVideoUrl());;
            }
            mProgress.setVisibility(View.GONE);
            episodeAdapter.notifyDataSetChanged();
        }
    }

}
