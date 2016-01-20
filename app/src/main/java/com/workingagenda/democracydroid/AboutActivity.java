package com.workingagenda.democracydroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by fen on 1/14/16.
 */
public class AboutActivity extends AppCompatActivity {

    //Declaire some variables
    public TextView Txt1;
    public TextView Txt2;
    public TextView Txt3;
    public TextView Txt4;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.about_title);
        toolbar.setLogo(R.mipmap.ic_launcher);
        // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        Txt1 = (TextView) findViewById(R.id.about_1);
        Txt1.setText(R.string.about_app);
        Txt2 = (TextView) findViewById(R.id.about_2);
        Txt2.setText(R.string.about_dm);
        Txt3 = (TextView) findViewById(R.id.about_3);
        Txt3.setText(R.string.about_instructions);
        Txt4 = (TextView) findViewById(R.id.about_4);
        Txt4.setText(R.string.about_info);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_back) {
            finish();
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