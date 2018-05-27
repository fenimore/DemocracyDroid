package com.workingagenda.democracydroid.Network;

import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;
import com.workingagenda.democracydroid.Objects.Episode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("DefaultFileTemplate")
public class ServerApi {

    private final int LIVE_TIME = 8;
    private final SimpleDateFormat mFormat;

    private final String DN_LIVE_STREAM= "http://democracynow.videocdn.scaleengine.net/" +
            "democracynow-iphone/play/democracynow/playlist.m3u8";
    private final String DN_VIDEO_HOST = "http://publish.dvlabs.com/democracynow/video-podcast/dn";
    private final String DN_VIDEO_FEED = "https://www.democracynow.org/podcast-video.xml";
    private final String TIME_ZONE = "America/New_York";


    public ServerApi(){
        mFormat = new SimpleDateFormat("yyyy-MMdd", Locale.US);
    }

    public ArrayList<Episode>getVideoFeed() throws Exception {

        RssReader rssReader = new RssReader(DN_VIDEO_FEED);
        List<RssItem>items = rssReader.getItems();
        int size = items.size();
        ArrayList<Episode> epis = new ArrayList<>(size);
        for(int i = 0; i< size; i++) {
            Episode e = new Episode();
            RssItem item = items.get(i);
            String title = item.getTitle();
            e.setTitle(title);
            e.setVideoUrl(item.getVideoUrl());
            String description = item.getDescription();
            e.setDescription(description);
            e.setImageUrl(item.getImageUrl());
            e.setUrl(item.getLink());
            epis.add(e);
        }
        return checkLiveStream(epis);
    }

    private ArrayList<Episode> checkLiveStream(ArrayList<Episode> epis) {
        // Make it Pretty, and NY eastern Time
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);
        Calendar c = Calendar.getInstance(timeZone);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        boolean onSchedule = (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && hourOfDay > LIVE_TIME - 1);

        if (!onSchedule) return epis;

        String formattedDate = mFormat.format(c.getTime());

        if (epis.get(0).getVideoUrl().contains(formattedDate))
            return epis;

        Episode episode = getUnlistedStream();
        if (LIVE_TIME == hourOfDay) {
            episode.setVideoUrl(DN_LIVE_STREAM);
            episode.setTitle("Stream Live");
        } else {
            String todayVid = DN_VIDEO_HOST + formattedDate + ".mp4";
            episode.setVideoUrl(todayVid);
        }
        epis.add(0, episode);
        return epis;
    }

    private Episode getUnlistedStream() {
        Episode todaysEpisode = new Episode();
        todaysEpisode.setDescription("Stream Live between 8 and 9 weekdays Eastern time, " +
                "the War and Peace Report");
        todaysEpisode.setImageUrl("https://upload.wikimedia.org/wikipedia/en/thumb/0/01/" +
                "Democracy_Now!_logo.svg/220px-Democracy_Now!_logo.svg.png");
        todaysEpisode.setUrl("http://m.democracynow.org/");
        // Add Today's Broadcast even if RSS feed isn't updated yet
        todaysEpisode.setTitle("Today's Broadcast");
        todaysEpisode.setDescription("Democracy Now! The War and Peace Report");
        return todaysEpisode;
    }
}
