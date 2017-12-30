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
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.workingagenda.democracydroid.screens.media.MediaActivity;
import com.google.android.exoplayer2.Player.EventListener;

public class MediaService extends Service {

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

        TrackSelection.Factory videoFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
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
        Log.d("Extension", ext);
        if (ext.equals(".m3u8")) {
            Handler mHandler = new Handler();
            String userAgent = Util.getUserAgent(this, "DemocracyDroid");
            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                    userAgent, null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    1800000,
                    true);
            HlsMediaSource mediaSource = new HlsMediaSource(url, dataSourceFactory, 1800000,
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
        // Notification
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
        player.addListener(new EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {}
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}
            @Override
            public void onLoadingChanged(boolean isLoading) {}
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {}
            @Override
            public void onRepeatModeChanged(int repeatMode) {}
            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {}
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                String TAG = "ExoError";
                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(), "Episode isn't available yet! Try again in a few minutes.", duration);
                        toast.show();
                        player.release();
                        break;
                    case ExoPlaybackException.TYPE_RENDERER:
                        Log.e(TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                        break;
                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        Log.e(TAG, "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                        break;
                }
            }
            @Override
            public void onPositionDiscontinuity(int reason) {}
            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
            @Override
            public void onSeekProcessed() {}
        });

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
}
