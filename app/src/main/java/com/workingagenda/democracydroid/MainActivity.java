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
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.workingagenda.democracydroid.Adapters.BlogAdapter;
import com.workingagenda.democracydroid.Adapters.DownloadsAdapter;
import com.workingagenda.democracydroid.Adapters.EpisodeAdapter;
import com.workingagenda.democracydroid.Adapters.PagerAdapter;
import com.workingagenda.democracydroid.Adapters.StoryAdapter;
import com.workingagenda.democracydroid.Helpers.DownloadsDataSave;
import com.workingagenda.democracydroid.Objects.Download;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;

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
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int DEFAULT_TAB = 2;

    //ArrayAdapter<String> AudioListAdapter;
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Blog").setIcon(R.drawable.ic_web_asset_white_24dp));
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

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Live Weekdays 8 am ET", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});

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
            return true;
        }
        if (id == R.id.action_refresh) {
            getSupportFragmentManager().getFragments();
            for(Fragment x :getSupportFragmentManager().getFragments()){
                //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //ft.detach(x).attach(x).commit();
                if (x instanceof PodcastFragment) {
                   // ((PodcastFragment) x).populateList(((PodcastFragment) x).GetVideoFeed());
                    ((PodcastFragment) x).refresh();
                }
                if (x instanceof BlogFragment) {
                    // ((PodcastFragment) x).populateList(((PodcastFragment) x).GetVideoFeed());
                    ((BlogFragment) x).refresh();
                }
                if (x instanceof DownloadFragment) {
                    // ((PodcastFragment) x).populateList(((PodcastFragment) x).GetVideoFeed());
                    ((DownloadFragment) x).refresh();
                }

            }
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


        //Declaire some variables
        private ListView mList;
        private TextView mTxt;
        private EpisodeAdapter episodeAdapter;
        private int LIVE_TIME = 8;
        //ArrayAdapter<String> VideoListAdapter;

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
                mTxt.setText(R.string.connect_error);
            }

        }
        private void refresh() {
            mTxt.setText(R.string.connecting);
            // En fait, Je pense que on doit clear the actual data
            if (episodes.size() > 1){
                episodes.clear();
                episodeAdapter.notifyDataSetChanged();
            }

            new GetVideoFeed().execute("http://www.democracynow.org/podcast-video.xml");
            new GetAudioFeed().execute("http://www.democracynow.org/podcast.xml"); // must be called second

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

            mList = (ListView) rootView.findViewById(android.R.id.list);
            mTxt = (TextView) rootView.findViewById(android.R.id.empty);

            registerForContextMenu(mList);

            mList.setEmptyView(mTxt);

            new GetVideoFeed().execute("http://www.democracynow.org/podcast-video.xml");
            new GetAudioFeed().execute("http://www.democracynow.org/podcast.xml"); // must be called second




            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    registerForContextMenu(view);
                    Episode e = episodes.get(i);
                    // CHANGE INTENT depending on the
                    Intent y = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getVideoUrl()));
                    startActivityForResult(y, 0); //ACTIVITY_LOAD = 0?

                    /**
                     * TODO:Have the APP GALLERY play the video
                     */
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
                //MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
            }
        }
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = info.position;
            Episode e = episodes.get(pos);
            switch(item.getItemId()) {
                case R.id.action_share:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, e.getTitle());
                    sendIntent.putExtra(Intent.EXTRA_TEXT, e.getUrl());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    return true;
                case R.id.action_audio:
                    Intent y = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getAudioUrl()));
                    startActivityForResult(y, 0); //ACTIVITY_LOAD = 0?
                    return true;
                case R.id.action_download_audio:
                    Download(e.getAudioUrl(), e.getTitle(), e.getDescription());
                    return true;

                case R.id.action_description:
                    AlertDialog description = new AlertDialog.Builder(getContext()).create();
                    // Get Description and Title
                    description.setTitle("The War and Peace Report");
                    description.setMessage(e.getDescription() + "\n\n" + e.getTitle());
                    //description.setIcon(R.drawable.dm_icon_small);
                    description.setButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing?
                        }
                    });
                    description.show();
                    return true;
                case R.id.action_download:
                    Download(e.getVideoUrl(), e.getTitle(), e.getDescription());
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        private class GetVideoFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    RssReader rssReader = new RssReader(params[0]);
                    for(RssItem item : rssReader.getItems()){
                        //VideoListAdapter.add(item.getTitle().substring(14));
                        // This should just be the Episode Object (class?)
                        Episode e = new Episode();
                        e.setTitle(item.getTitle());
                        e.setVideoUrl(item.getVideoUrl());
                        e.setDescription(item.getDescription());
                        e.setImageUrl(item.getImageUrl());
                        e.setUrl(item.getLink());
                        episodes.add(e);
                    }
                    episodes = checkLiveStream(episodes); // and add video in link
                                                          // not yet in RSS feed

                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //VideoListAdapter.notifyDataSetChanged();
                // Aud=ioListAdapter.notifyDataSetChanged();
                Log.d("Populating list", "Connection Populate List");
                populateList(episodes);

            }
        }

        private ArrayList<Episode> checkLiveStream(ArrayList<Episode> episodes){
            // Make it Pretty, and NY eastern Time
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MMdd");
            TimeZone timeZone = TimeZone.getTimeZone("GMT-400");
            Calendar c = Calendar.getInstance(timeZone);
            String formattedDate = format.format(c.getTime());
            // Some Variables fo' later
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int hourOfDay= c.get(Calendar.HOUR_OF_DAY);

            if ( dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY ){
                //http://publish.dvlabs.com/democracynow/video-podcast/dn2016-0513.mp4
                String today_video = "http://publish.dvlabs.com/democracynow/video-podcast/dn"
                        + formattedDate + ".mp4";
                String today_audio = "http://publish.dvlabs.com/democracynow/video-podcast/dn"
                        + formattedDate + "-1.mp3";
                if (today_video != episodes.get(0).getVideoUrl()){
                    if ( LIVE_TIME == hourOfDay ){
                        Log.d("YO it's time for live", "stream");
                        Episode live = new Episode();
                        live.setTitle("Stream Live");
                        live.setVideoUrl("https://livestream.com/DemocracyNow/daily");
                        live.setDescription("Stream Live between 8 and 9 weekdays Eastern time");
                        live.setImageUrl("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png");
                        live.setUrl("https://livestream.com/DemocracyNow");
                        episodes.add(0, live);
                    } else {
                        Episode todays_episode = new Episode();
                        todays_episode.setTitle("Today's Broadcast");
                        todays_episode.setVideoUrl(today_video);
                        todays_episode.setAudioUrl(today_audio);
                        todays_episode.setDescription("Watch Today's broadcast (it isn't yet added to the RSS feed");
                        todays_episode.setImageUrl("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png");
                        todays_episode.setUrl("https://democracynow.org");
                        episodes.add(0, todays_episode);
                    }
                }
            }

            return episodes;
        }

        private class GetAudioFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    RssReader rssReader = new RssReader(params[0]);
                    int j = 0;
                    TimeZone timeZone = TimeZone.getTimeZone("GMT-400");
                    Calendar c = Calendar.getInstance(timeZone);
                    if ( LIVE_TIME == c.get(Calendar.HOUR_OF_DAY)){
                        j = 1;
                        episodes.get(0).setAudioUrl("https://livestream.com/DemocracyNow");
                    }

                    for(RssItem item : rssReader.getItems()){
                        episodes.get(j).setAudioUrl(item.getVideoUrl());
                        // Audio Feed must be called before Video Feed
                        // Otherewise the episodes objects wont be there
                        j++;
                    }
                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        //http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
        public void Download(String url, String title, String desc){
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            } else {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription(desc);
                request.setTitle(title);
                // in order for this if to run, you must use the android 3.2 to compile your app
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

                Toast toast = Toast.makeText(getActivity(), "Starting download of " +title, Toast.LENGTH_LONG);
                toast.show();
            }

        }

    }
    public static class BlogFragment extends Fragment {

        //Declaire some variables
        private ListView bList;
        ArrayList<Episode> blogPosts = new ArrayList<Episode>(20);
        private TextView bTxt;
        private BlogAdapter blogAdapter;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public BlogFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BlogFragment newInstance(int sectionNumber) {
            BlogFragment fragment = new BlogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void populateList(ArrayList<Episode> blogs) {
            if (blogs.size() > 1){
                blogAdapter = new BlogAdapter(getContext(), R.layout.row_blog, blogs);
                bList.setAdapter(blogAdapter);
            }
            else {
                bTxt.setText(R.string.connect_error);
            }

        }

        private void refresh() {
            bTxt.setText(R.string.connecting);
            // En fait, Je pense que on doit clear the actual data
            if (blogPosts.size() > 1){
                blogPosts.clear();
                blogAdapter.notifyDataSetChanged();
            }

            new GetBlogFeed().execute("http://www.democracynow.org/democracynow-blog.rss");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_blog, container, false);

            bList = (ListView) rootView.findViewById(android.R.id.list);
            bTxt = (TextView) rootView.findViewById(android.R.id.empty);
            bList.setEmptyView(bTxt);
            registerForContextMenu(bList);
            new GetBlogFeed().execute("http://www.democracynow.org/democracynow-blog.rss");


            bList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Episode b = blogPosts.get(position);
                    // CHANGE INTENT depending on the
                    if (b.getVideoUrl() != null) {
                        Intent y = new Intent(Intent.ACTION_VIEW, Uri.parse(b.getVideoUrl()));
                        startActivityForResult(y, 0); //ACTIVITY_LOAD = 0?
                    } else {
                        Intent y = new Intent(Intent.ACTION_VIEW, Uri.parse(b.getUrl()));
                        startActivityForResult(y, 0); //ACTIVITY_LOAD = 0?
                    }
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
                //MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.blog_menu, menu);
            }
        }
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            //int pos = ; FIND A WAY TO PASS LiST ITEM POSITION?
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = info.position;
            Episode b = blogPosts.get(pos);
            switch(item.getItemId()) {
                case R.id.action_blog_description:
                    AlertDialog description = new AlertDialog.Builder(getContext()).create();
                    // Get Description and Title
                    description.setTitle("Democracy Now! Blog");
                    description.setMessage(b.getDescription() + "\n\n" + b.getTitle());
                    //description.setIcon(R.drawable.dm_icon_small);
                    description.setButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing?
                        }
                    });
                    description.show();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        private class GetBlogFeed extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    RssReader rssReader = new RssReader(params[0]);
                                        for(RssItem item : rssReader.getItems()){
                        //VideoListAdapter.add(item.getTitle().substring(14));
                        // This should just be the Episode Object (class?)
                        Episode b = new Episode();
                                            b.setTitle(item.getTitle());
                                            b.setVideoUrl(item.getVideoUrl());
                                            b.setDescription(item.getDescription());
                                            b.setImageUrl(item.getImageUrl());
                                            b.setUrl(item.getLink());
                        blogPosts.add(b);
                    }
                    //if(in between the hours, add a initial episodeto the list.);
                    //DateFormat df = DateFormat.getDateInstance();


                    //EpisodeAdapter episodeAdapter = new EpisodeAdapter(getContext(), R.layout.row_episodes, episodes);
                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");

                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //VideoListAdapter.notifyDataSetChanged();
                // AudioListAdapter.notifyDataSetChanged();
                populateList(blogPosts);

            }
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */

    public static class StoryFragment extends Fragment {
        private ListView sList;
        ArrayList<Episode> storyPosts = new ArrayList<Episode>(20);
        private TextView sTxt;
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
            if (stories.size() > 1){
                storyAdapter = new StoryAdapter(getContext(), R.layout.row_story, stories);
                sList.setAdapter(storyAdapter);
            }
            else {
                sTxt.setText(R.string.connect_error);
            }

        }

        private void refresh() {
            sTxt.setText(R.string.connecting);
            // En fait, Je pense que on doit clear the actual data
            if (storyPosts.size() > 1){
                storyPosts.clear();
                storyAdapter.notifyDataSetChanged();
            }

            new GetStoryFeed().execute("http://www.democracynow.org/democracynow-blog.rss");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_story, container, false);

            sList = (ListView) rootView.findViewById(android.R.id.list);
            sTxt = (TextView) rootView.findViewById(android.R.id.empty);
            sList.setEmptyView(sTxt);
            registerForContextMenu(sList);
            new GetStoryFeed().execute("http://www.democracynow.org/democracynow.rss");

            sList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Episode s = storyPosts.get(position);
                    // Add Story Activity
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
                //MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.blog_menu, menu);
            }
        }
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            //int pos = ; FIND A WAY TO PASS LiST ITEM POSITION?
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = info.position;
            Episode b = storyPosts.get(pos);
            switch(item.getItemId()) {
                case R.id.action_blog_description:
                    AlertDialog description = new AlertDialog.Builder(getContext()).create();
                    // Get Description and Title
                    description.setTitle("Democracy Now! Story");
                    description.setMessage(b.getDescription() + "\n\n" + b.getTitle());
                    //description.setIcon(R.drawable.dm_icon_small);
                    description.setButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing?
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
                try {
                    RssReader rssReader = new RssReader(params[0]);
                                        for(RssItem item : rssReader.getItems()){
                        //VideoListAdapter.add(item.getTitle().substring(14));
                        // This should just be the Episode Object (class?)
                        Episode b = new Episode();
                                            b.setTitle(item.getTitle());
                                            b.setDescription(item.getDescription());
                                            b.setPubDate(item.getPubDate());
                                            b.setImageUrl(item.getImageUrl());
                                            b.setUrl(item.getLink());
                                            storyPosts.add(b);
                                            Log.v("Story", b.getUrl());

                    }
                    //if(in between the hours, add a initial episodeto the list.);
                    //DateFormat df = DateFormat.getDateInstance();


                    //EpisodeAdapter episodeAdapter = new EpisodeAdapter(getContext(), R.layout.row_episodes, episodes);
                } catch (Exception e) {
                    Log.v("Error Parsing Data", e + "");

                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //VideoListAdapter.notifyDataSetChanged();
                // AudioListAdapter.notifyDataSetChanged();
                populateList(storyPosts);

            }
        }
    }


    /**
     * A Download fragment
     */
    public static class DownloadFragment extends Fragment {

        //Declaire some variables
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
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(f), "*/*");
                    startActivity(intent);
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
                //MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.download_menu, menu);
            }
        }
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            //int pos = ; FIND A WAY TO PASS LiST ITEM POSITION?
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = info.position;
            switch(item.getItemId()) {
                case R.id.action_delete:
                    File file = files.get(pos);
                    file.delete();
                    files = getListFiles();
                    dList.setAdapter(new DownloadsAdapter(getContext(), R.layout.row_download, files));
                    return true;

                default:
                    return super.onContextItemSelected(item);
            }
        }
        private List<File> getListFiles() {
            ArrayList<File> inFiles = new ArrayList<File>();
            File parentDir = new File(Environment.getExternalStorageDirectory().toString()+"/Podcasts");
            File[] files = parentDir.listFiles();
            if(files != null) { // I don't know why I need this, but otherwise it breaks
                for (File file : files) {
                    if(file.getName().startsWith("dn")){ // there must be a smarter way to do this
                        if(file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4")){
                            inFiles.add(file);
                        }
                    }
                }
            }
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
            //return PodcastFragment.newInstance(position + 1);
            switch(position) {

                case 0: return BlogFragment.newInstance(position + 1);
                case 1: return StoryFragment.newInstance(position + 1);
                case 2: return PodcastFragment.newInstance(position + 1);
                case 3: return DownloadFragment.newInstance(position + 1);
                default: return PodcastFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
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
                case 3:
                    return "SECTION 4";
            }
            return null;
        }

    }

}
