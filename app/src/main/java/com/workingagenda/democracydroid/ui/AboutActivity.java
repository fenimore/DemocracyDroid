package com.workingagenda.democracydroid.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.workingagenda.democracydroid.R;
import com.workingagenda.democracydroid.databinding.ActivityAboutBinding;

/**
 * Created by fen on 1/14/16.
 */
public class AboutActivity extends AppCompatActivity {
    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.aboutToolbar);
        binding.aboutToolbar.setTitle(R.string.about_title);
        binding.aboutToolbar.setLogo(R.drawable.appicon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.aboutDm.setText(R.string.about_dm);
        binding.aboutApp.setText(R.string.about_app);
        binding.aboutInfo.setText(R.string.about_info);

        binding.aboutReviewBtn.setOnClickListener(v -> {
            Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="
                                + getBaseContext().getPackageName())));
            }
        });

        binding.aboutSourceBtn.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://github.com/fenimore/democracydroid");
            Intent goToSource = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Github backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToSource.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(goToSource);
        });

        binding.aboutEmailBtn.setOnClickListener(v -> {
            String[] addresses = new String[1];
            addresses[0] = "exorable.ludos@gmail.com";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Democracy Droid Support");
            intent.putExtra(Intent.EXTRA_TEXT, "Hi Fenimore,");
            startActivity(intent);
        });

        binding.aboutDonateBtn.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.democracynow.org/");
            Intent donateIntent = new Intent(Intent.ACTION_VIEW, uri);
            donateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(donateIntent);
        });

        binding.aboutContactBtn.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.democracynow.org/contact");
            Intent contactIntent = new Intent(Intent.ACTION_VIEW, uri);
            contactIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(contactIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_main_settings:
                return true;
            case R.id.menu_main_site:
                String url = "http://www.democracynow.org/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
