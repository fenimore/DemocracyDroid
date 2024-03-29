package com.workingagenda.democracydroid.ui.player;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.core.service.MediaService;
import com.workingagenda.democracydroid.databinding.ActivityMediaBinding;

public class MediaActivity extends AppCompatActivity {
    ActivityMediaBinding binding;
    private ExoPlayer player;
    private Uri url; // cause all urls are uris
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("ServiceConnection", "connected");
            MediaService.LocalBinder binder = (MediaService.LocalBinder) service;
            MediaService mediaService = binder.getService();
            player = mediaService.setUpPlayer(url);
            Log.d("ServiceConnection", player.toString());

            binding.mediaPlayer.setPlayer(player);
            binding.mediaPlayer.requestFocus();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("ServiceConnection", "onServiceDisconnected");
            player.release();
        }
    };
    private String title;
    private String path;
    // TODO: Description?
    // TODO: Date?
    private long mMediaPosition;
    private boolean flag = false; // for toggling status and mediacontroller

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mMediaPosition = savedInstanceState.getInt("pos");
            Log.d("Unbundling: ", String.valueOf(mMediaPosition));
        }
        binding = ActivityMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mediaToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.mediaToolbar.setTitle("Democracy Droid!");

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "Do Destroy");
        unbindService(mConnection);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (path.contains(".mp3"))
                return true;
            if (flag)
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
        switch (item.getItemId()) {
            case R.id.menu_media_share:
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
        final WindowInsetsControllerCompat controller
                = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.statusBars());
        getSupportActionBar().hide();
    }
}
