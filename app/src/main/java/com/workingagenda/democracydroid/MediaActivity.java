package com.workingagenda.democracydroid;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by fen on 8/11/16.
 */
public class MediaActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private MediaController mMediaController;
    private Uri url; // cause all urls are uris
    private Toolbar toolbar;
    // TODO: Description?
    // TODO: Date?
    private int mMediaPosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mMediaPosition = savedInstanceState.getInt("pos");
        }
        setContentView(R.layout.activity_media);
        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Democracy Now!");
        // Views
        mVideoView = (VideoView) findViewById(R.id.media_player);
        // Intent Get Extras
        Bundle extras = getIntent().getExtras();
        url = Uri.parse((String) extras.get("url"));
        // Handle Media Playing
        mVideoView.setVideoURI(url);
        if (mMediaPosition != Integer.MIN_VALUE) {
            mVideoView.seekTo(mMediaPosition);
        }
        mVideoView.start();
        // Media Controller
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);


        // Hide toolbar once video starts
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Hide support bar
                getSupportActionBar().hide();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMediaPosition = mVideoView.getCurrentPosition();
        outState.putInt("pos", mMediaPosition);
    }
}
