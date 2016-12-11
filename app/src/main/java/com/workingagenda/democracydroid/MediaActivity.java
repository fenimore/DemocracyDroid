package com.workingagenda.democracydroid;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        // Views
        mMediaController = new MediaController(this);
        mVideoView = (VideoView) findViewById(R.id.media_player);
        mVideoView.requestFocus();
        // Intent Get Extras
        Bundle extras = getIntent().getExtras();
        url = Uri.parse((String) extras.get("url"));
        title = (String) extras.get("title"); // Doesn't work
        getSupportActionBar().setTitle(title);
        // Handle Media Playing
        mVideoView.setVideoURI(url);

        // Media Controller
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //Log.d("ERROR Duration", String.valueOf(mp.getDuration()));
                Log.d("ERROR Buffer", String.valueOf(mVideoView.getBufferPercentage()));
                Log.d("ERROR Current Position", String.valueOf(mVideoView.getCurrentPosition()));
                return false;
            }
        });

        // Hide toolbar once video starts
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                hideStatusBar();
                if (mMediaPosition != 0) {
                    Log.d("MediaPosition", String.valueOf(mMediaPosition));
                    mVideoView.seekTo(mMediaPosition);
                }
                mVideoView.start();
            }
        });
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.d("Pos", String.valueOf(mMediaPosition));
        outState.putInt("pos", mMediaPosition);
        Log.d("Saving Inst", String.valueOf(mMediaPosition));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Media", "onPause called");
        mMediaPosition = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Media", "onStop called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Media", "onResume called");
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(flag) {
                    hideStatusBar();
                    mMediaController.hide();
                } else {
                    mMediaController.show(0);
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
                // finish(); // instead??
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
        mVideoView.stopPlayback();
        super.onDestroy();
    }


}


