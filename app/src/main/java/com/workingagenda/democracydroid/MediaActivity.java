package com.workingagenda.democracydroid;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class MediaActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private Uri url; // cause all urls are uris
    private String title;
    private String path;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Democracy Droid!");

        // Intent Get Extras
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        path = (String) extras.get("url");
        if (path != null && !path.isEmpty()) {
            url = Uri.parse(path);
            title = (String) extras.get("title");
            getSupportActionBar().setTitle(title);
            // Start Service
            Intent i = new Intent(this, MediaService.class);
            Log.d("Service", mConnection.toString());
            bindService(i, mConnection, BIND_AUTO_CREATE);
            if (!path.contains("mp3")) hideStatusBar();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Saving Inst", String.valueOf(mMediaPosition));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Media", "onPause called");
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
        unbindService(mConnection);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch(e.getAction()) {
            case MotionEvent.ACTION_MOVE:
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

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
    }



    private final ServiceConnection mConnection = new ServiceConnection()  {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
           Log.d("ServiceConnection","connected");
            MediaService.LocalBinder binder = (MediaService.LocalBinder) service;
            MediaService mediaService = binder.getService();
            player = mediaService.setUpPlayer(url);
            Log.d("ServiceConnection", player.toString());
            // ExoPlayer Views
            StyledPlayerView mVideoView = findViewById(R.id.media_player);
            mVideoView.setPlayer(player);
            mVideoView.requestFocus();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("ServiceConnection", "onServiceDisconnected");
            player.release();
        }
    };
}


