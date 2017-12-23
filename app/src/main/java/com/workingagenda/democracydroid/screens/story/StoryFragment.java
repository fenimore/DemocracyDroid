package com.workingagenda.democracydroid.screens.story;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.workingagenda.democracydroid.Adapters.GridSpacingItemDecoration;
import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;
import com.workingagenda.democracydroid.Helpers.DpToPixelHelper;
import com.workingagenda.democracydroid.Network.Episode;
import com.workingagenda.democracydroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by derrickrocha on 7/16/17.
 */

public class StoryFragment extends Fragment {
    private RecyclerView sList;
    private ArrayList<Episode> mStories;
    private TextView sTxt;
    private SwipeRefreshLayout storySwipeRefreshLayout;
    private StoryAdapter storyAdapter;
    private View mProgress;

    public void refresh() {
        new GetStoryFeed(false).execute("https://www.democracynow.org/democracynow.rss");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_story, container, false);
        sList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        sTxt = (TextView) rootView.findViewById(android.R.id.empty);
        mProgress = rootView.findViewById(R.id.progress_icon);
        mStories = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(),mStories);
        sList.setLayoutManager(new LinearLayoutManager(getActivity()));
        sList.addItemDecoration(new GridSpacingItemDecoration(1, DpToPixelHelper.dpToPx(4,getResources().getDisplayMetrics()), true));
        sList.setItemAnimator(new DefaultItemAnimator());
        sList.setAdapter(storyAdapter);
        storySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        new GetStoryFeed(true).execute("https://www.democracynow.org/democracynow.rss");
        if (storySwipeRefreshLayout != null ) {
            storySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
        }
        return rootView;

    }

    private class GetStoryFeed extends AsyncTask<String, Void, List<Episode>> {

        private final boolean mShowLoading;

        public GetStoryFeed(boolean showloading) {
            mShowLoading = showloading;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(mShowLoading ? View.VISIBLE : View.GONE);
        }

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
            mProgress.setVisibility(View.GONE);
        }
    }

}
