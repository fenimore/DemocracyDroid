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
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
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
import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;
import com.workingagenda.democracydroid.Objects.Episode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    public SectionsPagerAdapter mSectionsPagerAdapter;
    public int DEFAULT_TAB;
    public boolean PREF_FIRST_TIME;

    // ENUMS
    public static final int STREAM_VIDEO = 0;
    public static final int STREAM_AUDIO = 1;
    public static final int OPEN_THIS_APP = 0;
    public static final int OPEN_EXTERNAL_APP = 1;

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
        PREF_FIRST_TIME = preferences.getBoolean("first_preference", true);
        // TODO: have splash screen for new users
        Log.d("First time", String.valueOf((PREF_FIRST_TIME)));
        // Tab Layouts
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        boolean spanish = preferences.getBoolean("spanish_preference", false);
        String storyTitle = !spanish ? "Stories"  : "Noticias";
        String broadTitle = !spanish ? "Broadcast" : "Difusiones";
        String downTitle = !spanish ? "Downloads" : "Descargas";
        tabLayout.addTab(tabLayout.newTab().setText(storyTitle).setIcon(R.drawable.ic_library_books_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText(broadTitle).setIcon(R.drawable.ic_live_tv_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText(downTitle).setIcon(R.drawable.ic_file_download_white_24dp));
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
            // Call Fragment refresh methods
            getSupportFragmentManager().getFragments();
            for(Fragment x :getSupportFragmentManager().getFragments()){
                if (x instanceof PodcastFragment)
                    ((PodcastFragment) x).refresh();
                if (x instanceof StoryFragment)
                    ((StoryFragment) x).refresh();
                if (x instanceof DownloadFragment)
                    ((DownloadFragment) x).refresh();
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
        if (id == R.id.action_exclusives) {
            String url = "https://www.democracynow.org/categories/web_exclusive";
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

        private void refresh() {
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
            mBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
            mEpisodes = new ArrayList<Episode>();
            mBar.setVisibility(View.GONE);
            registerForContextMenu(mList);
            // Is this necessary?
            mList.setEmptyView(mTxt);
            // Callback calls GetAudioFeed
            episodeAdapter = new EpisodeAdapter(getContext(), R.layout.row_episodes, mEpisodes);
            mList.setAdapter(episodeAdapter);
            mFormat = new SimpleDateFormat("yyyy-MMdd");
            new GetVideoFeed().execute("https://www.democracynow.org/podcast-video.xml");
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
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int DEFAULT_STREAM = Integer.parseInt(preferences.getString("stream_preference", "0")); // 0=video
            int DEFAULT_OPEN = Integer.parseInt(preferences.getString("open_preference", "0")); // 0 = within this ap
            int pos = info.position;
            Episode e = mEpisodes.get(pos);
            String actionTitle = "Democracy Now!";
            if (e.getTitle().length() > 16){
                if("Today's Broadcast".equals(e.getTitle())){
                    actionTitle = e.getTitle();
                } else if (e.getTitle().startsWith("Democracy Now!")){
                    actionTitle = e.getTitle().substring(14);
                } else {
                    actionTitle = e.getTitle();
                }
            }

            switch(item.getItemId()) {
                case R.id.action_share:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, e.getTitle());
                    sendIntent.putExtra(Intent.EXTRA_TEXT, e.getUrl());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    return true;
                case R.id.reverse_default_media:
                    if (e.getVideoUrl().contains("m3u8"))// FIXME: live streaming is broke, open in another browser
                        startMediaIntent(e.getAudioUrl(), 1, e.getTitle());
                    else if (DEFAULT_STREAM == 0)
                        startMediaIntent(e.getAudioUrl(), DEFAULT_OPEN, actionTitle);
                    else
                        startMediaIntent(e.getVideoUrl(), DEFAULT_OPEN, actionTitle);
                    return true;
                case R.id.reverse_default_open:
                    int reverseOpen = 0;
                    if (reverseOpen == DEFAULT_OPEN)
                        reverseOpen = 1;
                    if (DEFAULT_STREAM == 0)
                        startMediaIntent(e.getVideoUrl(), reverseOpen, actionTitle);
                    else
                        startMediaIntent(e.getAudioUrl(), reverseOpen, actionTitle);
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

                new GetAudioFeed().execute(feed); // must be called onPostExecute
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

    /**
     * A placeholder fragment containing a simple view.
     */

    public static class StoryFragment extends Fragment {
        private RecyclerView sList;
        private ArrayList<Episode> mStories;
        private TextView sTxt;
        private ProgressBar sBar;
        // TODO: private SwipeRefreshLayout storySwipeRefreshLayout;
        private SwipeRefreshLayout storySwipeRefreshLayout;
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

        private void refresh() {
            storySwipeRefreshLayout.setRefreshing(true);
            if (mStories.size() > 1){
                mStories.clear();
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
            sList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            sTxt = (TextView) rootView.findViewById(android.R.id.empty);
            sBar = (ProgressBar) rootView.findViewById(R.id.sBar);
            sBar.setVisibility(View.GONE);
            mStories = new ArrayList<>();
            storyAdapter = new StoryAdapter(getContext(), mStories);
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
           /* mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    Episode episode = mStories.get(position);
                    if (episode!= null && episode.getTitle()!=null
                            &&episode.getTitle().startsWith("Headlines")) {
                        return 2;
                    }
                    else {
                        return 1;
                    }
                }
            });*/
            sList.setLayoutManager(layoutManager);
            sList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            sList.setItemAnimator(new DefaultItemAnimator());
            sList.setAdapter(storyAdapter);
            storySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
            registerForContextMenu(sList);
            new GetStoryFeed().execute("https://www.democracynow.org/democracynow.rss");
          /*  sList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mStories.get(position) == null) return;
                    Episode s = mStories.get(position);
                    loadTranscript(s);
                }
            });*/

            if (storySwipeRefreshLayout != null ) {
                storySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        sTxt.setText("");
                        refresh();
                    }
                });
            }
            return rootView;

        }
        private void loadTranscript(Episode story) { //author does'nt work
            Intent intent = new Intent(getContext(), StoryActivity.class);
            intent.putExtra("url", story.getUrl()); //can't pass in article object?
            intent.putExtra("title", story.getTitle());
            intent.putExtra("date", story.getPubDate());
            startActivityForResult(intent, 0); //Activity load = 0
        }

        /**
         * RecyclerView item decoration - give equal margin around grid item
         */
        public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

            private int spanCount;
            private int spacing;
            private boolean includeEdge;

            public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
                this.spanCount = spanCount;
                this.spacing = spacing;
                this.includeEdge = includeEdge;
            }


            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int column = position % spanCount; // item column

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            }
        }

        /**
         * Converting dp to pixel
         */
        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
            Episode b = mStories.get(pos);
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
        private class GetStoryFeed extends AsyncTask<String, Void, List<Episode>> {
            @Override
            protected List<Episode> doInBackground(String... params) {
                ArrayList<Episode> stories = new ArrayList<>();
                ArrayList<Episode> todaysStories = new ArrayList<>(32);
                try {
                    RssReader rssReader = new RssReader(params[0]);
                    for(RssItem item : rssReader.getItems()){
                        Episode b = new Episode();
                        b.setTitle(item.getTitle());
                        b.setDescription(item.getDescription());
                        b.setPubDate(item.getPubDate());
                        b.setImageUrl(item.getContentEnc());
                        b.setUrl(item.getLink());
                        // Headlines are last in Feed, sort by Headlines
                        todaysStories.add(0, b);
                        if (b.getTitle().contains("Headlines")) {
                            stories.addAll(todaysStories);
                            todaysStories.clear();
                        }
                    }
                    if (!todaysStories.isEmpty()) {
                        stories.addAll(todaysStories);
                    }
                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");

                }
                return stories;
            }
            @Override
            protected void onPostExecute(List<Episode> stories) {
                mStories.addAll(stories);
                storyAdapter.notifyDataSetChanged();
                if (storySwipeRefreshLayout != null){
                    storySwipeRefreshLayout.setRefreshing(false);
                }
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
