package com.workingagenda.democracydroid;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class StoryActivity extends AppCompatActivity {

    private static final String CSS = "<head><style type='text/css'> "
            + "body {max-width: 100%; margin: 0.3cm; font-family: sans-serif-light; color: black; background-color: #f6f6f6; line-height: 150%} "
            + "* {max-width: 100%; word-break: break-word}"
            + "h1, h2 {font-weight: normal; line-height: 130%} "
            + "h1 {font-size: 170%; margin-bottom: 0.1em} "
            + "h2 {font-size: 140%} "
            + ".donate_container {background: #333; color: #FFFFFF; width: 100%; text-align:center; display: inline-block} "
            + ".donate_prompt {width:66%}"
            + ".donate_button {background: #01afef; "
            + "color: white;	float:right; font-weight: 400;	text-transform: uppercase;	padding: 10px 12px;	width: 90px;	margin-left: 1em;	"
            + "text-align: center;} "
            + "ul > li {display: inline-block;  zoom:1;*display:inline;"
            + "margin-right:15px;margin-left: 15px;}"
            + "a {color: #0099CC; text-decoration: none;}"
            + "h1 a {color: inherit;}"
            + "img {height: auto} "
            + "pre {white-space: pre-wrap;} "
            + "blockquote {border-left: thick solid #a6a6a6; background-color: #e6e6e6; margin: 0.5em 0 0.5em 0em; padding: 0.5em} "
            + "p {margin: 0.8em 0 0.8em 0} "
            + ".submitted {color: #666666; border-top:1px cyan; border-bottom:1px blue; padding-top:2px; padding-bottom:2px; font-weight:800 } "
            + "ul, ol {margin: 0 0 0.8em 0.6em; padding: 0 0 0 1em} "
            + "ul li, ol li {margin: 0 0 0.8em 0; padding: 0} "
            + "div.button-section {padding: 0.4cm 0; margin: 0; text-align: center} "
            + ".button-section p {margin: 0.1cm 0 0.2cm 0}"
            + ".button-section p.marginfix {margin: 0.5cm 0 0.5cm 0}"
            + ".button-section input, .button-section a {font-family: sans-serif-light; font-size: 100%; color: #FFFFFF; background-color:#52A7DF; text-decoration: none; border: none; border-radius:0.2cm; padding: 0.3cm} "
            + "</style><meta name='viewport' content='width=device-width'/></head><body>";
    private WebView webview;
    // Data
    private String title;
    private String date;
    private String url;
    private String video;
    private String audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        webview = findViewById(R.id.story_webview);
        Toolbar toolbar = findViewById(R.id.story_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        url = (String) extras.get("url");
        title = (String) extras.get("title");
        date = (String) extras.get("date");
        assert date != null;
        date = date.substring(0, date.lastIndexOf("-"));
        new RetrieveContent().execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (video == null && audio == null) {
            return super.onOptionsItemSelected(item);
        }
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_story_share:
                // share intent
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sendIntent.putExtra(Intent.EXTRA_TEXT, date + " \n\n" + url);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.menu_story_web:
                // Open Story in Browser
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            case R.id.menu_story_play_audio:
                Intent intent = new Intent(this, MediaActivity.class);
                intent.putExtra("url", audio);
                intent.putExtra("title", title);
                startActivityForResult(intent, 0); //Activity load = 0
                return true;
            case R.id.menu_story_play_video:
                Intent intent1 = new Intent(this, MediaActivity.class);
                intent1.putExtra("url", video);
                intent1.putExtra("title", title);
                startActivityForResult(intent1, 0); //Activity load = 0
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
        Element data;
        // Get the Individual Videos
        Element videoElem = doc.getElementsByClass("download_video").get(0);
        Element audioElem = doc.getElementsByClass("download_audio").get(0);
        audio = audioElem.attr("abs:href");
        video = videoElem.attr("abs:href");
        // Get the Transcript URL
        if (doc.getElementById("headlines") == null) {
            data = doc.getElementById("story_text");
            data.getElementsByClass("left_panel").remove();
            data.getElementsByClass("hidden-xs").remove();
            data.getElementsByClass("hidden-sm").remove();
        } else {
            data = doc.getElementById("headlines"); // get the third content div,
        }
        // Change the links to absolute!! so that images work
        Elements select_img = data.select("img");
        Elements select = data.select("a");
        for (Element e : select_img) {
            e.attr("src", e.absUrl("src"));
        }
        for (Element e : select) {
            e.attr("href", e.absUrl("href"));
        }
        data.getElementsByClass("donate_container").remove();
        return CSS + data.toString() + "</body>";
    }

    private class RetrieveContent extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                return getContent(urls[0]);
            } catch (Exception e) {
                Log.v("Story Failure:", e.toString());
                return null;
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String page = "<h2>" + title + "</h2><strong>" + date +
                    "</strong><br><small>Viewer Supported News:</small> " +
                    "<a class='donate_button' data-width='800' data-height='590' " +
                    "data-ga-action='Story: Donate' href='https://democracynow.org/donate'>" +
                    "Donate</a><br>Donate at democracynow.org<hr>" + result;
            webview.loadDataWithBaseURL(null, page,
                    "text/html; charset=utf-8", "UTF-8", null);
        }
    }
}
