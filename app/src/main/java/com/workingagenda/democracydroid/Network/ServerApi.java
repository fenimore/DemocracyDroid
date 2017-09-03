package com.workingagenda.democracydroid.Network;

import com.workingagenda.democracydroid.Feedreader.RssItem;
import com.workingagenda.democracydroid.Feedreader.RssReader;
import com.workingagenda.democracydroid.Objects.Episode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by derrickrocha on 8/27/17.
 */

public class ServerApi {

    private int LIVE_TIME = 8;
    private SimpleDateFormat mFormat;

    public ServerApi(){
        mFormat = new SimpleDateFormat("yyyy-MMdd");
    }

    public ArrayList<Episode>getVideoFeed() throws Exception {

        RssReader rssReader = new RssReader("https://www.democracynow.org/podcast-video.xml");
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

}
