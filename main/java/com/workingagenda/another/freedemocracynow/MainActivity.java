/*
 *
 *   Copyright (C) 2015 Fenimore Love
 *
 *   This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   This project uses Shirwa Mohamed's RSS library
 *   https://github.com/ShirwaM/Simplistic-RSS
 */
package com.workingagenda.another.freedemocracynow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * TODO:Add images to the list adapter and Icon to Actionbar?
 */

public class MainActivity extends ActionBarActivity {

    private static final int ACTIVITY_LOAD=0;


    private ListView mList;
    ArrayAdapter<String> adapter;

    public String[] urlArray = new String[15];
    public String[] titleArray = new String[15];
    public String[] descriptionArray = new String[15];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String>(this, R.layout.democracy_row);
        new GetRssFeed().execute("http://www.democracynow.org/podcast-video.xml");

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txt = (TextView) findViewById(R.id.textEdit);
                txt.setText(titleArray[i]);
                Intent y = new Intent(Intent.ACTION_VIEW, Uri.parse(urlArray[i]));
                startActivityForResult(y, ACTIVITY_LOAD);
                /**
                 * TODO:Have the APP GALLERY play the video
                 */
            }
        });
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog description = new AlertDialog.Builder(
                        MainActivity.this).create();
                // Get Description and Title
                description.setTitle("Headlines:");
                description.setMessage(descriptionArray[i] +"\n\n" + titleArray[i]);
                description.setIcon(R.drawable.dm_icon_small);
                description.setButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //...
                    }
                });
                description.show();
                return true;
            }
        });
    }

    /**
     * This method calls DM's rss feed
     * and then it plugs the VideoUrl, Title/date, and Description
     * into arrays to be later displayed etc.
     * it also fills the list adapter
     */
    private class GetRssFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RssReader rssReader = new RssReader(params[0]);
                int j = 0;
                for (RssItem item : rssReader.getItems()){
                    adapter.add(item.getTitle());
                    urlArray[j] = item.getVideoUrl();
                    titleArray[j] = item.getTitle();
                    descriptionArray[j] = item.getDescription();
                    j++;
                }
            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            mList.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Options Menu has three options, Refresh, Launch DM Website, Launch About Activity
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            finish();
            startActivity(getIntent());
            return true;
        }
        if (id == R.id.action_launch_dm) {
            String url = "http://www.democracynow.org";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        if (id == R.id.action_about){
            Intent i = new Intent(this, aboutActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}

