package com.workingagenda.democracydroid.tabfragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.workingagenda.democracydroid.Adapters.EpisodeAdapter;
import com.workingagenda.democracydroid.Adapters.GridSpacingItemDecoration;
import com.workingagenda.democracydroid.Helpers.DpToPixelHelper;
import com.workingagenda.democracydroid.Network.Podcast.GetAudioFeed;
import com.workingagenda.democracydroid.Network.Podcast.GetVideoFeed;
import com.workingagenda.democracydroid.Network.ServerApi;
import com.workingagenda.democracydroid.Objects.Episode;
import com.workingagenda.democracydroid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PodcastFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private final String TAG = "PODCASTS";
    private final int LIVE_TIME = 8;
    private final String DN_SPANISH_FEED = "https://www.democracynow.org/podcast-es.xml";
    private final String DN_AUDIO_FEED = "https://www.democracynow.org/podcast.xml";
    private final String DN_AUDIO_HOSTING = "http://traffic.libsyn.com/democracynow/";
    private final String DN_LIVE_HOSTING = "http://democracynow.videocdn.scaleengine.net/democracynow-" +
            "iphone/play/democracynow/playlist.m3u8";
    private View mProgress;
    private EpisodeAdapter episodeAdapter;
    private ArrayList<Episode> mEpisodes;
    private SimpleDateFormat mFormat;
    private boolean mSpanishFeed = false;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ServerApi mServerApi;

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

    public void refresh() {
        getVideoFeed(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView mList = rootView.findViewById(R.id.main_recyclerview);
        mProgress = rootView.findViewById(R.id.main_progress_icon);

        mySwipeRefreshLayout = rootView.findViewById(R.id.main_swiperefresh);
        if (mySwipeRefreshLayout != null) {
            mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSpanishFeed = preferences.getBoolean("spanish_preference", false);

        mEpisodes = new ArrayList<>();
        mServerApi = new ServerApi();

        episodeAdapter = new EpisodeAdapter(getContext(), mEpisodes);
        mList.setAdapter(episodeAdapter);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.addItemDecoration(new GridSpacingItemDecoration(
                1, DpToPixelHelper.dpToPx(4, getResources().getDisplayMetrics()), true));
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
            public void onGetVideoFeedPostExecute(ArrayList<Episode> episodes) {
                mProgress.setVisibility(View.GONE);
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
                    Snackbar.make(mProgress, R.string.connect_error, Snackbar.LENGTH_INDEFINITE).show();
                    return;
                }
                if (mySwipeRefreshLayout != null) {
                    mySwipeRefreshLayout.setRefreshing(false);
                }
                audioLinks = fillInMissingAudioLink(audioLinks);
                int smallest = Math.min(mEpisodes.size(), audioLinks.size());
                for (int i = 0; i < smallest; i++) {
                    mEpisodes.get(i).setAudioUrl(audioLinks.get(i));
                }
                Log.d(TAG, mEpisodes.toString());
                mProgress.setVisibility(View.GONE);
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
