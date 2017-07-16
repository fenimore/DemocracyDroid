package com.workingagenda.democracydroid.tabfragment;

/**
 * Created by derrickrocha on 7/16/17.
 */

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.workingagenda.democracydroid.Adapters.EpisodeAdapter;
import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;
import com.workingagenda.democracydroid.MediaActivity;
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

    // ENUMS
    public static final int STREAM_VIDEO = 0;
    public static final int STREAM_AUDIO = 1;
    public static final int OPEN_THIS_APP = 0;

    //Declare some variables
    private ListView mList;
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
        mList = (ListView) rootView.findViewById(android.R.id.list);
        mTxt = (TextView) rootView.findViewById(android.R.id.empty);
        mProgress = (RelativeLayout) rootView.findViewById(R.id.progess_layout);
        mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mEpisodes = new ArrayList<>();
        mProgress.setVisibility(View.GONE);
        registerForContextMenu(mList);
        // Callback calls GetAudioFeed
        episodeAdapter = new EpisodeAdapter(getContext(), R.layout.row_episodes, mEpisodes);
        mList.setAdapter(episodeAdapter);
        mFormat = new SimpleDateFormat("yyyy-MMdd");
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

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Episode e = mEpisodes.get(i);
                loadEpisode(e);
            }
        });

        return rootView;
    }

    private void loadEpisode(Episode e) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
        int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this app
        // Set the Title for Toolbar
        String actionTitle = "Democracy Now!";
        if (e.getTitle().length() > 16){
            if("Today's Broadcast".equals(e.getTitle()))
                actionTitle = e.getTitle();
            else if (e.getTitle().startsWith("Democracy Now!"))
                actionTitle = e.getTitle().substring(14);
            else
                actionTitle = e.getTitle();
        }

        if (DEFAULT_STREAM == STREAM_VIDEO)
            startMediaIntent(e.getVideoUrl(), DEFAULT_OPEN, actionTitle);
        else if (DEFAULT_STREAM == STREAM_AUDIO)
            startMediaIntent(e.getAudioUrl(), DEFAULT_OPEN, actionTitle);
    }

    // start an activity either in this pap or another -- pass in either video
    // or audio stream.
    private void startMediaIntent(String url, int open, String title) {
        // pass in the URL if either audio or video (make check above)
        // Media Activity
        if (open == OPEN_THIS_APP) {
            Intent intent = new Intent(getContext(), MediaActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivityForResult(intent, 0); //Activity load = 0
        } else {
            // FIXME: SecurityException
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "*/*");
            startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==android.R.id.list) {
            MenuInflater inflater = new MenuInflater(getContext());
            menu.setHeaderTitle("Democracy Now!");
            inflater.inflate(R.menu.context_menu, menu);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
            int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this app

            if (DEFAULT_STREAM == 0)
                menu.getItem(2).setTitle("Stream Audio");
            else
                menu.getItem(2).setTitle("Stream Video");

            if(DEFAULT_OPEN == 0)
                menu.getItem(3).setTitle("Stream in Another App");
            else
                menu.getItem(3).setTitle("Stream in This App");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private class GetVideoFeed extends AsyncTask<String, Void, ArrayList<Episode>> {
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
            e.setTitle(item.getTitle());
            e.setVideoUrl(item.getVideoUrl());
            e.setDescription(item.getDescription());
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
        String todayVid = "https://hot.dvlabs.com/democracynow/video-podcast/dn"
                + formattedDate + ".mp4";
        String todayVid2 = "https://publish.dvlabs.com/democracynow/video-podcast/dn"
                + formattedDate + ".mp4";
        String todayAudio = "https://traffic.libsyn.com/democracynow/dn"
                + formattedDate + "-1.mp3";
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return epis;
        if (todayVid.equals(epis.get(0).getVideoUrl())) return epis;
        if (todayVid2.equals(epis.get(0).getVideoUrl()))return epis;
        if (hourOfDay < LIVE_TIME) return epis;
        Episode episode = getUnlistedStream(hourOfDay, todayAudio, todayVid);
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
            String today_audio = "https://traffic.libsyn.com/democracynow/dn"
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
                //Log.d("Episode:", "\n" + mEpisodes.get(i).getAudioUrl()+ "\n"+ mEpisodes.get(i).getVideoUrl());;
            }
            episodeAdapter.notifyDataSetChanged();
        }
    }

    // FIXME: Show progress:
    // http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
    public void Download(String url, String title, String desc) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            // TODO: catch onRequestPermissionsResult
        } else {
            if (url.equals("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/play/democracynow/playlist.m3u8")) {
                Toast toast = Toast.makeText(getActivity(),
                        "You can't download the Live Stream", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(desc);
            request.setTitle(title);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            String fileext = url.substring(url.lastIndexOf('/') + 1);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS, fileext);
            //http://stackoverflow.com/questions/24427414/getsystemservices-is-undefined-when-called-in-a-fragment

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            // TODO: Save que ID for cancel button
            Toast toast = Toast.makeText(getActivity(), "Starting download of " + title, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
