package com.workingagenda.democracydroid;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by fen on 8/11/16.
 */
public class MediaActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private MediaController mMediaController;
    private Uri url; // cause all urls are uris

    // TODO: Description?
    // TODO: Date?

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Views
        mVideoView = (VideoView)findViewById(R.id.media_player);
        // Intent Get Extras
        Bundle extras = getIntent().getExtras();
        url = Uri.parse((String) extras.get("url"));
        // Handle Media Playing
        mVideoView.setVideoURI(url);
        mVideoView.start();
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);
        
    }
}
