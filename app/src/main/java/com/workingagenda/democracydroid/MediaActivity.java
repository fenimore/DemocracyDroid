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
    private String title;
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
        toolbar.setTitle("Democracy Droid!");
        // Views
        mMediaController = new MediaController(this);
        mVideoView = (VideoView) findViewById(R.id.media_player);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            boolean flag;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        if(flag) {
                            mMediaController.hide();
                            getSupportActionBar().hide();
                        } else {
                            mMediaController.show();
                            getSupportActionBar().show();
                        }
                        flag = !flag;
                        return true;
                }
                return false;
            }
        });
        // Intent Get Extras
        Bundle extras = getIntent().getExtras();
        url = Uri.parse((String) extras.get("url"));
        title = (String) extras.get("title"); // Doesn't work
        getSupportActionBar().setTitle(title);
        // Handle Media Playing
        mVideoView.setVideoURI(url);
        if (mMediaPosition != Integer.MIN_VALUE) {
            mVideoView.seekTo(mMediaPosition);
        }
        mVideoView.start();
        // Media Controller
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);
        // Hide toolbar once video starts
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                hideStatusBar();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMediaPosition = mVideoView.getCurrentPosition();
        outState.putInt("pos", mMediaPosition);
    }
    // BTW this is only for Android 4.1 and UP?
    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //http://stackoverflow.com/questions/9987042/videoview-onresume-loses-buffered-portion-of-the-video
        // TODO: Try using SharedPreferences?
        //mVideoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mVideoView.suspend();
    }
}


