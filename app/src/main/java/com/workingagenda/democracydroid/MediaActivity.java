package com.workingagenda.democracydroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by fen on 8/11/16.
 */
public class MediaActivity extends AppCompatActivity {

    private SimpleExoPlayerView mVideoView;
    private SimpleExoPlayer player;
    //private MediaSource mediaSource;

    //private MediaService.LocalBinder binder;

    private Uri url; // cause all urls are uris
    private String title;
    private String path;
    private Toolbar toolbar;
    // TODO: Description?
    // TODO: Date?
    private long mMediaPosition;
    private boolean flag = false; // for toggling status and mediacontroller
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mMediaPosition = savedInstanceState.getInt("pos");
            Log.d("Unbundling: ", String.valueOf(mMediaPosition));
        }
        setContentView(R.layout.activity_media);
        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Democracy Droid!");

        // Intent Get Extras
        Bundle extras = getIntent().getExtras();
        path = (String) extras.get("url");
        url = Uri.parse(path);
        title = (String) extras.get("title"); // Doesn't work
        getSupportActionBar().setTitle(title);

        // Start Service
        Intent i = new Intent(this, MediaService.class);
        Log.d("Service", mConnection.toString());
        bindService(i, mConnection, BIND_AUTO_CREATE);
        if (!path.contains("mp3")) hideStatusBar();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putLong("pos", mAMediaPosition);
        Log.d("Saving Inst", String.valueOf(mMediaPosition));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Media", "onPause called");
        //mMediaPosition = player.getCurrentPosition();
        //player.release();
        //unbindService(mConnection);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Media", "onStop called");
        //player.release();
        //unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        //mVideoView.stopPlayback();
        super.onDestroy();
        Log.d("onDestroy", "Do Destroy");
        //unbindService(mConnection);
        //stopService(new Intent(getApplicationContext(), MediaService.class));
        unbindService(mConnection);
        //player.release();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch(e.getAction()) {
            case MotionEvent.ACTION_UP:
                if (path.contains(".mp3"))
                    return true;
                if(flag)
                    hideStatusBar();
                else
                    getSupportActionBar().show();
                flag = !flag;
                return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_media, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Democracy Now! " + title);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return true;
        }
    }

    // BTW this is only for Android 4.1 and UP?
    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
    }



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("ServiceConnection","connected");
            MediaService.LocalBinder binder = (MediaService.LocalBinder) service;
            MediaService mediaService = binder.getService();
            player = mediaService.setUpPlayer(url);
            Log.d("ServiceConnection", player.toString());
            // ExoPlayer Views
            mVideoView = (SimpleExoPlayerView) findViewById(R.id.media_player);
            mVideoView.setPlayer(player);
            mVideoView.requestFocus();

            if (!path.contains(".mp3"))
                hideStatusBar();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("ServiceConnection", "onServiceDisconnected");
            player.release();
        }
    };
}


