package com.workingagenda.democracydroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

public class MediaService extends Service {

    private final IBinder binder = new LocalBinder();
    private PlayerNotificationManager playerNotificationManager;
    private SimpleExoPlayer player;

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.d("MediaService", "OnCreate");

        ExoTrackSelection.Factory videoFactory = new AdaptiveTrackSelection.Factory();

        TrackSelector trackSelector = new DefaultTrackSelector(getApplicationContext(), videoFactory);
        LoadControl loadControl = new DefaultLoadControl();

        // Create Player
        player = new SimpleExoPlayer.Builder(getApplicationContext())
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build();

        player.play();
        player.setHandleAudioBecomingNoisy(true);  // pause when bluetooth disconnects
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_SPEECH)
                .build();
        player.setAudioAttributes(audioAttributes, true);

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                getApplicationContext(),
                "com.workingagenda.democracydroid",
                R.string.channel_name,
                0,
                420,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    // TODO: Serialize and pass an episode to this service
                    // TODO: Set the image and title from the current Episode
                    @NonNull
                    @Override
                    public String getCurrentContentTitle(@NonNull Player player) {
                        return "Democracy Now!";
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(@NonNull Player player) {
                        Intent notIntent = new Intent(getApplicationContext(), MediaActivity.class);
                        // TODO: bundle
                        notIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        return PendingIntent.getActivity(getApplicationContext(), 0,
                                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(@NonNull Player player) {
                        return "The War and Peace Report";
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(@NonNull final Player player,
                                                      @NonNull final PlayerNotificationManager.BitmapCallback callback) {
                        return BitmapFactory.decodeResource(getResources(), R.drawable.appicon);
                    }
                },
                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId,
                                                     @NonNull Notification notification, boolean ongoing) {
                        startForeground(notificationId, notification);
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        stopSelf();
                    }
                }
        );

        playerNotificationManager.setPlayer(player);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public SimpleExoPlayer setUpPlayer(Uri url) {
        String ext = url.toString().substring(url.toString().lastIndexOf("."));
        Log.d("Extension", ext);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("DemocracyDroid!")
                .setTransferListener(null)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(1800000)
                .setAllowCrossProtocolRedirects(true);

        if (ext.equals(".m3u8")) {
            HlsMediaSource.Factory hlsExtractorFactory = new HlsMediaSource.Factory(dataSourceFactory);
            HlsMediaSource hlsMediaSource = hlsExtractorFactory.createMediaSource(MediaItem.fromUri(url));
            player.setMediaSource(hlsMediaSource);
        } else {
            MediaSourceFactory mediaFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);
            MediaSource mediaSource = mediaFactory.createMediaSource(MediaItem.fromUri(url));
            player.setMediaSource(mediaSource);
        }
        player.prepare();

        player.addListener(new EventListener() {
            @Override
            public void onPlayerError(@NonNull ExoPlaybackException error) {
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
        });

        return player;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        playerNotificationManager.setPlayer(null);
        player.stop();
        player.release();
        return false;
    }

    public class LocalBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }
}
