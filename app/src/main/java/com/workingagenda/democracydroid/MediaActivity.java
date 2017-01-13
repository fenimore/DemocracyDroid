package com.workingagenda.democracydroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

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
    private MediaSource mediaSource;

    private Uri url; // cause all urls are uris
    private String title;
    private Toolbar toolbar;
    // TODO: Description?
    // TODO: Date?
    private long mMediaPosition;
    private boolean flag = true; // for toggling status and mediacontroller
    
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
        // Call setUpPlayer in the onResume override
    }

    private void setUpPlayer() {
        // ExoPlayer Default Set Up
        //Handler mainHandler = new Handler(); // NOTE: Not needed?
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoFactory);
        // Load controls
        LoadControl loadControl = new DefaultLoadControl();
        // Create Player
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(),
                trackSelector, loadControl);

        // ExoPlayer Views
        mVideoView = (SimpleExoPlayerView) findViewById(R.id.media_player);
        mVideoView.setPlayer(player);
        mVideoView.requestFocus();

        // Intent Get Extras
        Bundle extras = getIntent().getExtras();
        url = Uri.parse((String) extras.get("url"));
        title = (String) extras.get("title"); // Doesn't work
        getSupportActionBar().setTitle(title);
        // Set Source
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(this, "DemocracyDroid"));
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        mediaSource = new ExtractorMediaSource(url,
                dataSourceFactory, extractorsFactory, null, null);
        if (mMediaPosition != 0) {
            player.seekTo(mMediaPosition);
        }
        player.setPlayWhenReady(true);
        player.prepare(mediaSource);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("pos", mMediaPosition);
        Log.d("Saving Inst", String.valueOf(mMediaPosition));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Media", "onPause called");
        mMediaPosition = player.getCurrentPosition();
        player.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Media", "onStop called");
        player.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Media", "onResume called");
        setUpPlayer();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch(e.getAction()) {
            case MotionEvent.ACTION_UP:
                if(flag) {
                    hideStatusBar();
                } else {
                    getSupportActionBar().show();
                }
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

    @Override
    protected void onDestroy() {
        //mVideoView.stopPlayback();
        super.onDestroy();
        Log.d("onDestroy", "Do Destroy");
        player.release();
    }
}


