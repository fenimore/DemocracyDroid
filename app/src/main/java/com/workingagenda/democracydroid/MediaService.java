package com.workingagenda.democracydroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by fen on 2/6/17.
 */

public class MediaService extends Service {

    public static final int BUFFER_SEGMENT_SIZE = 16 * 1024; // Original value was 64 * 1024
    public static final int VIDEO_BUFFER_SEGMENTS = 50; // Original value was 200
    public static final int AUDIO_BUFFER_SEGMENTS = 20; // Original value was 54
    public static final int BUFFER_SEGMENT_COUNT = 64; // Original value was 256

    private String title;
    private String path;

    private SimpleExoPlayer player;
    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }

    private final IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.d("MediaService", "OnCreate");
        // Load controls
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoFactory);
        LoadControl loadControl = new DefaultLoadControl();

        // Create Player
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(),
                trackSelector, loadControl);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public SimpleExoPlayer setUpPlayer(Uri url) {
        String ext = url.toString().substring(url.toString().lastIndexOf("."));
        if (ext.equals(".m3u8")) {
            Handler mHandler = new Handler();
            String userAgent = Util.getUserAgent(this, "DemocracyDroid");
            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                    userAgent, null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    1800000,
                    true);
            MediaSource mediaSource = new ExtractorMediaSource(url,dataSourceFactory, Mp3Extractor.FACTORY,
                    mHandler, null);
            player.setPlayWhenReady(true);
            player.prepare(mediaSource);
        } else {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                    Util.getUserAgent(this, "DemocracyDroid"));
            // Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(url,
                    dataSourceFactory, extractorsFactory, null, null);
            player.setPlayWhenReady(true);
            player.prepare(mediaSource);
        }

		Intent notIntent = new Intent(getApplicationContext(), MediaActivity.class);
        // TODO: bundle
		notIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0,
				notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(this);

		builder.setContentIntent(pendInt)
		.setSmallIcon(R.drawable.ic_mic_none_white_24dp)
		.setTicker("Democracy Now!")
		.setOngoing(true)
		.setContentTitle("Democracy Now!")
		.setContentText("The War and Peace Report");
        Notification not = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            not = builder.build();
        }
        startForeground(1333, not);
        return player;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    // TODO: startPlaying
    // TODO: notification
    // TODO: release on unbind

    public class LocalBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }


    /*private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
                : uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }*/
}
