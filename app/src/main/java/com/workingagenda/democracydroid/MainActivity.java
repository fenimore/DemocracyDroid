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
package com.workingagenda.democracydroid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.workingagenda.democracydroid.Adapters.DownloadsAdapter;
import com.workingagenda.democracydroid.Adapters.EpisodeAdapter;
import com.workingagenda.democracydroid.Adapters.StoryAdapter;

import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private boolean PREF_WIFI;
    private int DEFAULT_TAB;
    private boolean PREF_FIRST_TIME;
    //ArrayAdapter<String> AudioListAdapter;
    private String actionTitle = "Democracy Droid!";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        DEFAULT_TAB = Integer.parseInt(preferences.getString("tab_preference", "1"));
        PREF_WIFI = preferences.getBoolean("wifi_preference", false);
        PREF_FIRST_TIME = preferences.getBoolean("first_preference", true);
        Log.d("First time", String.valueOf((PREF_FIRST_TIME)));
        // Tab Layouts
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Stories").setIcon(R.drawable.ic_library_books_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("Broadcast").setIcon(R.drawable.ic_live_tv_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("Downloads").setIcon(R.drawable.ic_file_download_white_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(DEFAULT_TAB);
        // Gather the Episode Lists
        // Set up the tab and View Pager
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            // Don't let user click before async tasks are done
            item.setEnabled(false);
            // According to settings...
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isWifi = mWifi.isConnected();
            // Only update on WIFI according to SharedPreferences
            Log.v("WIFI pref", String.valueOf(PREF_WIFI) );
            if (PREF_WIFI){
                if (!isWifi) {
                Log.v("WIFI", "false");
                return true;
                }
            }
            // Call Fragment refresh methods
            getSupportFragmentManager().getFragments();
            for(Fragment x :getSupportFragmentManager().getFragments()){
                if (x instanceof PodcastFragment) {
                    ((PodcastFragment) x).refresh();
                }
                if (x instanceof StoryFragment) {
                    ((StoryFragment) x).refresh();
                }
                if (x instanceof DownloadFragment) {
                    ((DownloadFragment) x).refresh();
                }
            }
            // FIXME: Somehow enable this after async call...
            item.setEnabled(true);
            return true;
        }
        if (id == R.id.action_donate) {
            String url = "https://www.democracynow.org/donate";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        if (id == R.id.action_site) {
            String url = "http://www.democracynow.org/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        if(id == R.id.action_about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivityForResult(intent, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PodcastFragment extends Fragment {

        //Declare some variables
        private ListView mList;
        private TextView mTxt;
        private ProgressBar mBar;
        private EpisodeAdapter episodeAdapter;
        private int LIVE_TIME = 8;// TODO: const

        private SwipeRefreshLayout mySwipeRefreshLayout;

        // Episode objects!!!
        ArrayList<Episode> episodes = new ArrayList<Episode>(20);
        // set up custom adapter with episodes
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PodcastFragment() {
        }

        public void populateList(ArrayList<Episode> episodes) {
            if (episodes.size() > 1){
                episodeAdapter = new EpisodeAdapter(getContext(), R.layout.row_episodes, episodes);
                mList.setAdapter(episodeAdapter);
            }
            else {
                mBar.setVisibility(View.GONE);
                mTxt.setText(R.string.connect_error);
            }
            if (mySwipeRefreshLayout != null ) {
                mySwipeRefreshLayout.setRefreshing(false);
            }
        }
        private void refresh() {
            mySwipeRefreshLayout.setRefreshing(true);
            if (episodes.size() > 1){
                episodes.clear();
                if (episodeAdapter != null) {
                    episodeAdapter.notifyDataSetChanged();
                }
            }
            // Call GetAudioFeed in GetVideoFeed Callback
            //http://www.democracynow.org/podcast-video.xml
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
            mBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

            mBar.setVisibility(View.GONE);
            registerForContextMenu(mList);
            // Is this necessary?
            mList.setEmptyView(mBar); // FIXME: Set to mTxt???S
            // Callback calls GetAudioFeed
            new GetVideoFeed().execute("https://www.democracynow.org/podcast-video.xml");
            if (mySwipeRefreshLayout != null ) {
                mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                });
            }

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Episode e = episodes.get(i);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
                    int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this app

                    // Set the Title for Toolbar
                    String actionTitle = "Democracy Now!";
                    if (e.getTitle().length() > 16){
                        if(e.getTitle() == "Today's Broadcast"){
                            actionTitle = e.getTitle();
                        } else if (e.getTitle().startsWith("Democracy Now!")){
                            actionTitle = e.getTitle().substring(14);
                        } else {
                            actionTitle = e.getTitle();
                        }
                    }

                    // FIXME: live streaming is broke, open in another browser
                    if (e.getTitle().equals("Stream Live"))
                        startMediaIntent(e.getVideoUrl(), 1, e.getTitle());
                    // Default Stream :
                    // 0 => Video stream 1 => Audio stream
                    // Default Open:
                    // 0 => In App / 1 => external app
                    if (DEFAULT_STREAM == 0)
                        startMediaIntent(e.getVideoUrl(), DEFAULT_OPEN, actionTitle);
                    if (DEFAULT_STREAM == 1)
                        startMediaIntent(e.getAudioUrl(), DEFAULT_OPEN, actionTitle);
                }
            });
            return rootView;
        }

        // start an activity either in this pap or another -- pass in either video
        // or audio stream.
        private void startMediaIntent(String url, int externalApp, String title) {
            // pass in the URL if either audio or video (make check above)
            if (externalApp == 0) {
                Intent intent = new Intent(getContext(), MediaActivity.class);
                intent.putExtra("url", url); //can't pass in article object?
                intent.putExtra("title", title); // Can parseable it, but not worth it
                startActivityForResult(intent, 0); //Activity load = 0
            } else {
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
                Log.d("Contex", menu.getItem(2).toString());
                Log.d("Contex", menu.getItem(3).toString());
                if (DEFAULT_STREAM == 0) {
                    menu.getItem(2).setTitle("Stream Audio");
                } else {
                    menu.getItem(2).setTitle("Stream Video");
                }

                if(DEFAULT_OPEN == 0) {
                    menu.getItem(3).setTitle("Stream in Another App");
                } else {
                    menu.getItem(3).setTitle("Stream in This App");
                }

            }
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
            int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this ap
            int pos = info.position;
            Episode e = episodes.get(pos);
            String actionTitle = "Democracy Now!";
            if (e.getTitle().length() > 16){
                if(e.getTitle() == "Today's Broadcast"){
                    actionTitle = e.getTitle();
                } else if (e.getTitle().startsWith("Democracy Now!")){
                    actionTitle = e.getTitle().substring(14);
                } else {
                    actionTitle = e.getTitle();
                }
            }
            Log.d("Itemid", String.valueOf(item.getItemId()));
            Log.d("Itemid", String.valueOf(R.id.reverse_default_open));
            switch(item.getItemId()) {
                case R.id.action_share:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, e.getTitle());
                    sendIntent.putExtra(Intent.EXTRA_TEXT, e.getUrl());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    return true;
                case R.id.reverse_default_media: // TODO: refactor - action_otherstream
                    if (DEFAULT_STREAM == 0) {
                        startMediaIntent(e.getAudioUrl(), DEFAULT_OPEN, actionTitle);
                    } else {
                        startMediaIntent(e.getVideoUrl(), DEFAULT_OPEN, actionTitle);
                    }
                    return true;
                case R.id.reverse_default_open: // TODO: refactor - action_stream-otherwise
                    int reverseOpen = 0;
                    if (reverseOpen == DEFAULT_OPEN)
                        reverseOpen = 1;
                    Log.d("Open", String.valueOf(reverseOpen));
                    Log.d("Open", String.valueOf(DEFAULT_OPEN));
                    if (DEFAULT_STREAM == 0) {
                        startMediaIntent(e.getVideoUrl(), reverseOpen, actionTitle);
                    } else {
                        startMediaIntent(e.getAudioUrl(), reverseOpen, actionTitle);
                    }
                    return true;
                case R.id.action_description:
                    AlertDialog description = new AlertDialog.Builder(getContext()).create();
                    // Get Description and Title
                    description.setTitle("The War and Peace Report");
                    description.setMessage(e.getDescription() + "\n\n" + e.getTitle());
                    description.setButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
                    description.show();
                    return true;
                case R.id.video_download:
                    if (e.getTitle().equals("Stream Live"))
                        return true;
                    Download(e.getVideoUrl(), e.getTitle(), e.getDescription());
                    return true;
                case R.id.audio_download:
                    if (e.getTitle().equals("Stream Live"))
                        return true;
                    Download(e.getAudioUrl(), e.getTitle(), e.getDescription());
                    return true;
                case R.id.open_browser:
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(e.getUrl()), "*/*");
                    startActivity(intent);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        private class GetVideoFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                Log.d("GetVideo", params[0]);
                try {
                    episodes = parseVideoFeed(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // TODO: Swap english feed for spanish according to the settings (3.0 milestone)
                // https://www.democracynow.org/podcast-es.xml
                new GetAudioFeed().execute("https://www.democracynow.org/podcast.xml"); // must be called second
            }
        }

        private ArrayList<Episode> parseVideoFeed(String url) throws Exception {
            RssReader rssReader = new RssReader(url);
            for(RssItem item : rssReader.getItems()){
                Episode e = new Episode();
                e.setTitle(item.getTitle());
                e.setVideoUrl(item.getVideoUrl());
                e.setDescription(item.getDescription());
                e.setImageUrl(item.getImageUrl());
                e.setUrl(item.getLink());
                episodes.add(e);
            }
            return checkLiveStream(episodes); // and add video in link
                                              // not yet in RSS feed ;)
        }

        private ArrayList<Episode> checkLiveStream(ArrayList<Episode> episodes) {
            // Make it Pretty, and NY eastern Time
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MMdd");
            TimeZone timeZone = TimeZone.getTimeZone("GMT-500");
            Calendar c = Calendar.getInstance(timeZone);
            String formattedDate = format.format(c.getTime());
            String today_video = "https://publish.dvlabs.com/democracynow/video-podcast/dn"
                    + formattedDate + ".mp4";
            String today_audio = "https://traffic.libsyn.com/democracynow/dn"
                    + formattedDate + "-1.mp3";
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
                return episodes;
            if (today_video.equals(episodes.get(0).getVideoUrl()))
                return episodes;
            if (hourOfDay < LIVE_TIME)
                return episodes;
            // Get the missing episode
            // TODO: test for early morning feed
            Episode episode = getUnlistedStream(hourOfDay, today_audio, today_video);
            episodes.add(0, episode);
            return episodes;
        }

        private Episode getUnlistedStream(int hour, String today_audio, String today_video){
            //Log.d("Today", today_video);//Log.d("Latest", episodes.get(0).getVideoUrl());
            // Live Stream
            Episode todays_episode = new Episode();
            todays_episode.setDescription("Stream Live between 8 and 9 weekdays Eastern time, " +
                    "the War and Peace Report");
            todays_episode.setImageUrl("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/" +
                    "Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png");
            todays_episode.setUrl("http://m.democracynow.org/");
            if ( LIVE_TIME == hour ){
                Log.d("YO it's time for live", "stream");
                todays_episode.setTitle("Stream Live");//"Stream Live");
                todays_episode.setVideoUrl("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/" +
                        "play/democracynow/playlist.m3u8");
                todays_episode.setAudioUrl("http://democracynow.videocdn.scaleengine.net/democracynow-iphone/" +
                        "play/democracynow/playlist.m3u8");
            } else if ( hour > 8) {
                // Add Todays Broadcast even if RSS feed isn't updated yet
                todays_episode.setTitle("Today's Broadcast");
                todays_episode.setVideoUrl(today_video);
                todays_episode.setAudioUrl(today_audio);
            }
            return todays_episode;
        }

        private class GetAudioFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                RssReader rssReader = new RssReader(params[0]);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MMdd");
                TimeZone timeZone = TimeZone.getTimeZone("GMT-500");
                Calendar c = Calendar.getInstance(timeZone);
                String formattedDate = format.format(c.getTime());
                String today_audio = "https://traffic.libsyn.com/democracynow/dn"
                        + formattedDate + "-1.mp3";
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                int hourOfDay= c.get(Calendar.HOUR_OF_DAY);
                ArrayList<String> audio = new ArrayList<>();
                try {
                    for(RssItem item : rssReader.getItems())
                        audio.add(item.getVideoUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean valid = (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY  && hourOfDay > LIVE_TIME);
                if ( hourOfDay == LIVE_TIME)
                    audio.add(0, "http://democracynow.videocdn.scaleengine.net/" +
                            "democracynow-iphone/play/democracynow/playlist.m3u8");
                if (!audio.get(0).equals(today_audio) && valid) // check rather if field is empty?
                    audio.add(0, today_audio);
                for (int i =0; i < episodes.size(); i++)
                    episodes.get(i).setAudioUrl(audio.get(i));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.v("Podcast", "Populating List");
                if (episodes != null) {
                    for (int i = 0; i < episodes.size(); i++){
                        Log.d("Episode:", episodes.get(i).toString());
                    }
                    populateList(episodes);
                }
            }
        }

        // FIXME: Show progress:
        // http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
        public void Download(String url, String title, String desc){
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
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
                Toast toast = Toast.makeText(getActivity(), "Starting download of " +title, Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public static class StoryFragment extends Fragment {
        private ListView sList;
        ArrayList<Episode> storyPosts = new ArrayList<Episode>(100);
        private TextView sTxt;
        private ProgressBar sBar;
        // TODO: private SwipeRefreshLayout storySwipeRefreshLayout;
        private StoryAdapter storyAdapter;
        private static final String ARG_SECTION_NUMBER = "section_number";


        public StoryFragment() {
        }
        public static StoryFragment newInstance(int sectionNumber) {
            StoryFragment fragment = new StoryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void populateList(ArrayList<Episode> stories) {
            Log.v("Load story feed", String.valueOf(stories.size()));

            if (stories.size() > 1){
                storyAdapter = new StoryAdapter(getContext(), R.layout.row_story, stories);
                sList.setAdapter(storyAdapter);
            } else {
                sBar.setVisibility(View.GONE);
                sTxt.setText(R.string.connect_error);
            }
        }

        private void refresh() {
            if (storyPosts.size() > 1){
                storyPosts.clear();
                if (storyAdapter != null ){
                    storyAdapter.notifyDataSetChanged();
                }
            }
            new GetStoryFeed().execute("https://www.democracynow.org/democracynow.rss");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_story, container, false);

            sList = (ListView) rootView.findViewById(android.R.id.list);
            sTxt = (TextView) rootView.findViewById(android.R.id.empty);
            sBar = (ProgressBar) rootView.findViewById(R.id.sBar);
            sBar.setVisibility(View.GONE);
            // TODO: // storySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
            sList.setEmptyView(sBar);
            registerForContextMenu(sList);
            new GetStoryFeed().execute("https://www.democracynow.org/democracynow.rss");
            sList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Episode s = storyPosts.get(position);
                    loadTranscript(s);
                }
            });
            return rootView;

        }
        private void loadTranscript(Episode story) { //author does'nt work
            Intent intent = new Intent(getContext(), StoryActivity.class);
            intent.putExtra("url", story.getUrl()); //can't pass in article object?
            intent.putExtra("title", story.getTitle());
            intent.putExtra("date", story.getPubDate());
            startActivityForResult(intent, 0); //Activity load = 0
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            if (v.getId()==android.R.id.list) {
                MenuInflater inflater = new MenuInflater(getContext());
                menu.setHeaderTitle("Democracy Now!");
                inflater.inflate(R.menu.blog_menu, menu);
            }
        }
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = info.position;
            Episode b = storyPosts.get(pos);
            switch(item.getItemId()) {
                case R.id.action_blog_description:
                    AlertDialog description = new AlertDialog.Builder(getContext()).create();
                    // Get Description and Title
                    description.setTitle("Democracy Now! Story");
                    description.setMessage(b.getDescription() + "\n\n" + b.getTitle());
                    description.setButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
                    description.show();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        private class GetStoryFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                if (storyPosts.size() > 0){
                    storyPosts = new ArrayList<>(100); // NOTE: of Episode objects
                }
                Log.v("Story count", String.valueOf(storyPosts.size()));
                try {
                    RssReader rssReader = new RssReader(params[0]);
                    for(RssItem item : rssReader.getItems()){
                        Episode b = new Episode();
                                            b.setTitle(item.getTitle());
                                            b.setDescription(item.getDescription());
                                            b.setPubDate(item.getPubDate());
                                            b.setImageUrl(item.getImageUrl());
                                            b.setUrl(item.getLink());
                                            storyPosts.add(b);
                    }
                    Log.v("Story count Two", String.valueOf(storyPosts.size()));
                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");

                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (storyPosts != null) {
                    populateList(storyPosts);
                }
                Log.v("Stories", "Populating List");
            }
        }
    }


    /**
     * A Download fragment
     */
    public static class DownloadFragment extends Fragment {
        public TextView Txt1;
        public Button btn;
        public Button btnRefresh;
        public ListView dList;
        public List<File> files;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public DownloadFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DownloadFragment newInstance(int sectionNumber) {
            DownloadFragment fragment = new DownloadFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_download, container, false);

            files = getListFiles();

            dList = (ListView) rootView.findViewById(android.R.id.list);
            Txt1 = (TextView) rootView.findViewById(R.id.download_help);
            Txt1.setText(R.string.download_help);
            dList.setEmptyView(Txt1);
            btn = (Button) rootView.findViewById(R.id.clear);
            btnRefresh= (Button) rootView.findViewById(R.id.refresh);
            registerForContextMenu(dList);

            dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext()).setTitle("Delete all downloads")
                        .setMessage("Are you sure you want to delete all episodes?\nLong click and episode to delete them individually.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    for (File file : files) {
                                        Log.d("File", file.getName());
                                        // remove files
                                        file.delete();
                                    }
                                    files = getListFiles();
                                    dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
                                    Toast toast = Toast.makeText(getActivity(), "Downloads Removed", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            });
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    files = getListFiles();
                    dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
                }
            });
            dList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    File f = files.get(position);
                    Intent y = new Intent(getContext(), MediaActivity.class);
                    y.putExtra("url", Uri.fromFile(f).toString()); //can't pass in article object?
                    y.putExtra("title", f.getName());
                    startActivityForResult(y, 0); //Activity load = 0
                }
            });

            return rootView;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            if (v.getId()==android.R.id.list) {
                MenuInflater inflater = new MenuInflater(getContext());
                menu.setHeaderTitle("Democracy Now!");
                inflater.inflate(R.menu.download_menu, menu);
            }
        }
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            //int pos = ; FIND A WAY TO PASS LiST ITEM POSITION?
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = info.position;
            File file = files.get(pos);
            switch(item.getItemId()) {
                case R.id.action_delete:
                    file.delete();
                    files = getListFiles();
                    dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
                    return true;
                case R.id.action_external_player:
                    Intent z = new Intent(Intent.ACTION_VIEW);
                    z.setDataAndType(Uri.fromFile(file), "*/*");
                    startActivity(z);
                default:
                    return super.onContextItemSelected(item);
            }
        }
        private List<File> getListFiles() {
            ArrayList<File> inFiles = new ArrayList<File>();
            File parentDir = new File(Environment.getExternalStorageDirectory().toString()+
                    File.separator + Environment.DIRECTORY_PODCASTS);
            File[] files = parentDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if(file.getName().startsWith("dn") || file.getName().endsWith("-podcast.mp4") || file.getName().endsWith("-podcast.mp3")){ // there must be a smarter way to do this
                        if(file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4")){
                            inFiles.add(file);
                        }
                    }
                }
            }
            // Collections.reverse(inFiles);
            return inFiles;
        }

        private void refresh(){
            //do nothing
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PodcastFragment (defined as a static inner class below).
            // Return PodcastFragment.newInstance(position + 1);
            switch(position) {

                case 0: return StoryFragment.newInstance(position + 1);
                case 1: return PodcastFragment.newInstance(position + 1);
                case 2: return DownloadFragment.newInstance(position + 1);
                default: return PodcastFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }

    }

}
