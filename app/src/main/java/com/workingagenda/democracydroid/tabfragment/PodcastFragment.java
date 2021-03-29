package com.workingagenda.democracydroid.tabfragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.workingagenda.democracydroid.Adapters.EpisodeAdapter;
import com.workingagenda.democracydroid.Adapters.GridSpacingItemDecoration;
import com.workingagenda.democracydroid.Helpers.DpToPixelHelper;
import com.workingagenda.democracydroid.Network.Podcast.GetAudioFeed;
import com.workingagenda.democracydroid.Network.Podcast.GetVideoFeed;
import com.workingagenda.democracydroid.Network.ServerApi;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.databinding.FragmentMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PodcastFragment extends Fragment {
    private final String TAG = "PODCASTS";
    private final int LIVE_TIME = 8;
    private final String DN_SPANISH_FEED = "https://www.democracynow.org/podcast-es.xml";
    private final String DN_AUDIO_FEED = "https://www.democracynow.org/podcast.xml";
    private final String DN_AUDIO_HOSTING = "http://traffic.libsyn.com/democracynow/";
    private final String DN_LIVE_HOSTING = "http://democracynow.videocdn.scaleengine.net/democracynow-" +
            "iphone/play/democracynow/playlist.m3u8";
    private EpisodeAdapter episodeAdapter;
    private ArrayList<Episode> mEpisodes;
    private SimpleDateFormat mFormat;
    private boolean mSpanishFeed = false;
    private ServerApi mServerApi;
    private FragmentMainBinding binding;

    public void refresh() {
        getVideoFeed(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);

        if (binding.mainSwipeRefresh != null) {
            binding.mainSwipeRefresh.setOnRefreshListener(this::refresh);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSpanishFeed = preferences.getBoolean("pref_spanish", false);

        mEpisodes = new ArrayList<>();
        mServerApi = new ServerApi();

        episodeAdapter = new EpisodeAdapter(getContext(), mEpisodes);
        binding.mainRecyclerview.setAdapter(episodeAdapter);
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.mainRecyclerview.addItemDecoration(new GridSpacingItemDecoration(
                1, DpToPixelHelper.dpToPx(4, getResources().getDisplayMetrics()), true));
        mFormat = new SimpleDateFormat("yyyy-MMdd", Locale.US);
        getVideoFeed(true);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getVideoFeed(boolean showLoading) {
        new GetVideoFeed(showLoading, mServerApi, new GetVideoFeed.GetVideoFeedCallback() {

            @Override
            public void onGetVideoFeedPreExecute(boolean showLoading) {
                binding.mainProgressIcon.setVisibility(showLoading ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onGetVideoFeedPostExecute(ArrayList<Episode> episodes) {
                binding.mainProgressIcon.setVisibility(View.GONE);
                showEpisodes(episodes);
                getAudioFeed();
            }
        }).execute();
    }

    private void getAudioFeed() {
        String feed = mSpanishFeed ? DN_SPANISH_FEED : DN_AUDIO_FEED;

        new GetAudioFeed(new GetAudioFeed.GetAudioFeedCallback() {
            @Override
            public void onGetAudioFeedPreExecute() {
            }

            @Override
            public void onGetAudioFeedPostExecute(List<String> audioLinks) {
                if (audioLinks.size() < 1) {
                    Snackbar.make(binding.mainProgressIcon, R.string.connect_error, Snackbar.LENGTH_INDEFINITE).show();
                    return;
                }
                if (binding.mainSwipeRefresh != null) {
                    binding.mainSwipeRefresh.setRefreshing(false);
                }
                audioLinks = fillInMissingAudioLink(audioLinks);
                int smallest = Math.min(mEpisodes.size(), audioLinks.size());
                for (int i = 0; i < smallest; i++) {
                    mEpisodes.get(i).setAudioUrl(audioLinks.get(i));
                }
                Log.d(TAG, mEpisodes.toString());
                binding.mainProgressIcon.setVisibility(View.GONE);
                episodeAdapter.notifyDataSetChanged();
            }
        }).execute(feed); // must be called onPostExecute
    }

    private void showEpisodes(ArrayList<Episode> episodes) {
        mEpisodes.clear();
        mEpisodes.addAll(episodes);
        episodeAdapter.notifyDataSetChanged();
    }

    private List<String> fillInMissingAudioLink(List<String> audioUrls) {
        if (audioUrls.size() < 1) return audioUrls;
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        Calendar c = Calendar.getInstance(timeZone);
        String formattedDate = mFormat.format(c.getTime());
        String todays_audio = "dn" + formattedDate;
        // Today's audio already present
        if (audioUrls.get(0).contains(todays_audio)) return audioUrls;

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        boolean onSchedule = dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && hourOfDay > LIVE_TIME - 1;
        // There isn't an unlisted audio recording or live feed
        if (!onSchedule) return audioUrls;

        if (hourOfDay == LIVE_TIME) {
            audioUrls.add(0, DN_LIVE_HOSTING);
            return audioUrls;
        } else {
            audioUrls.add(0, DN_AUDIO_HOSTING + todays_audio + ".mp3");
        }

        return audioUrls;
    }
}
