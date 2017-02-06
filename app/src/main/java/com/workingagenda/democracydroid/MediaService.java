package com.workingagenda.democracydroid;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by fen on 2/6/17.
 */

public class MediaService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // mp = newMediaplyaer
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Bundle bundle = intent.getBundleExtra("url");
        String path = intent.getStringExtra("url");//bundle.getString("url");
        Log.d("URL", intent.getExtras().toString());
        Log.d("URL", path);

        Uri url = Uri.parse(path);
        setUpPlayer(url);
        return START_STICKY;
    }

    private void setUpPlayer(Uri url) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoFactory);
        // Load controls
        LoadControl loadControl = new DefaultLoadControl();
        // Create Player
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(),
                trackSelector, loadControl);


        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(this, "DemocracyDroid"));
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(url,
                dataSourceFactory, extractorsFactory, null, null);

        player.setPlayWhenReady(true);
        player.prepare(mediaSource);
    }
}
