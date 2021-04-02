package com.workingagenda.democracydroid.tabfragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.workingagenda.democracydroid.Adapters.GridSpacingItemDecoration;
import com.workingagenda.democracydroid.Adapters.StoryAdapter;
import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;
import com.workingagenda.democracydroid.Helpers.DpToPixelHelper;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.databinding.FragmentStoryBinding;

import java.util.ArrayList;
import java.util.List;

public class StoryFragment extends Fragment {
    private ArrayList<Episode> mStories;
    private StoryAdapter storyAdapter;
    private FragmentStoryBinding binding;

    public StoryFragment() {
    }

    public void refresh() {
        new GetStoryFeed(false).execute("https://www.democracynow.org/democracynow.rss");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStoryBinding.inflate(inflater, container, false);

        mStories = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), mStories);
        binding.storyRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.storyRecyclerview.addItemDecoration(new GridSpacingItemDecoration(
                1, DpToPixelHelper.dpToPx(4, getResources().getDisplayMetrics()), true)
        );
        binding.storyRecyclerview.setItemAnimator(new DefaultItemAnimator());
        binding.storyRecyclerview.setAdapter(storyAdapter);
        new GetStoryFeed(true).execute("https://www.democracynow.org/democracynow.rss");
        if (binding.storySwipeRefresh != null) {
            binding.storySwipeRefresh.setOnRefreshListener(this::refresh);
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class GetStoryFeed extends AsyncTask<String, Void, List<Episode>> {
        private final boolean mShowLoading;

        public GetStoryFeed(boolean showLoading) {
            mShowLoading = showLoading;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.storyProgressIcon.setVisibility(mShowLoading ? View.VISIBLE : View.GONE);
        }

        @Override
        protected List<Episode> doInBackground(String... params) {
            ArrayList<Episode> stories = new ArrayList<>();
            ArrayList<Episode> todaysStories = new ArrayList<>(32);
            try {
                RssReader rssReader = new RssReader(params[0]);
                for (RssItem item : rssReader.getItems()) {
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
            if (binding.storySwipeRefresh != null) {
                binding.storySwipeRefresh.setRefreshing(false);
            }
            binding.storyProgressIcon.setVisibility(View.GONE);
        }
    }
}
