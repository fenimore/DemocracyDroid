package com.workingagenda.democracydroid.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.workingagenda.democracydroid.R;

/**
 * Created by fen on 1/14/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class AboutActivity extends AppCompatActivity {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.about_title);
        toolbar.setLogo(R.drawable.appicon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView txt1 = findViewById(R.id.about_1);
        txt1.setText(R.string.about_dm);
        TextView txt2 = findViewById(R.id.about_2);
        txt2.setText(R.string.about_app);
        TextView txt4 = findViewById(R.id.about_4);
        txt4.setText(R.string.about_info);

        Button btnRev = findViewById(R.id.reviewButton);
        Button btnSrc = findViewById(R.id.sourceButton);
        Button btnDon = findViewById(R.id.btn_donate);
        Button btnMail = findViewById(R.id.emailButton);

        btnRev.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
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
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));
                }
            }
        });

        btnSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/fenimore/democracydroid");
                Intent goToSource = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Github backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToSource.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(goToSource);
            }
        });

        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] addresses = new String[1];
                addresses[0] = "exorable.ludos@gmail.com";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Democracy Droid Support");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi Fenimore,");
                startActivity(intent);
            }
        });

        btnDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.democracynow.org/donate");
                Intent donateIntent = new Intent(Intent.ACTION_VIEW, uri);
                donateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(donateIntent);
            }
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
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_donate) {
            String url = "https://www.democracynow.org/donate";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        if (id == R.id.action_site) {
            String url = "http://www.democracynow.org/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}