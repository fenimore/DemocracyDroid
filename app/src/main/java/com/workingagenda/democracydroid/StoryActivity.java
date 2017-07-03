package com.workingagenda.democracydroid;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by fen on 5/14/16.
 */
public class StoryActivity extends AppCompatActivity {

    // TODO: Get video links from page, and offer a play option
    // TODO: GO HERE:
    // <a class="download_video" href="https://publish.dvlabs.com/democracynow/360/dn2016-0810.mp4?start=2994.0">
    // And of course for audio...
    private TextView testing;
    private WebView webview;

    // Data
    private String title;
    private String date;
    private String url;
    private String content;
    private String author;
    private String video;
    private String audio;

    private static final String CSS = "<head><style type='text/css'> "
            + "body {max-width: 100%; margin: 0.3cm; font-family: sans-serif-light; color: black; background-color: #f6f6f6; line-height: 150%} "
            + "* {max-width: 100%; word-break: break-word}"
            + "h1, h2 {font-weight: normal; line-height: 130%} "
            + "h1 {font-size: 170%; margin-bottom: 0.1em} "
            + "h2 {font-size: 140%} "

            + ".donate_container {background: #333; color: #FFFFFF; width: 100%; text-align:center; display: inline-block} "
            + ".donate_prompt {width:66%}"

            + ".donate_button {background: #458589; "
            + "background: linear-gradient(to bottom, #458589 0%, #2A6075 100%);	"
            + "color: white;	float:right; font-weight: 400;	text-transform: uppercase;	padding: 10px 12px;	width: 90px;	margin-left: 1em;	"
            + "text-align: center;} "
            + "a {color: #0099CC}"
            + "ul {list-style-type: none;overflow: hidden;}"
            + "h1 a {color: inherit; text-decoration: none}"
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        webview = (WebView) findViewById(R.id.webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        url = (String) extras.get("url");
        title = (String) extras.get("title");
        date = (String) extras.get("date");
        Log.v("url", url);
        new RetrieveContent().execute(url);
    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
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
        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
             NavUtils.navigateUpFromSameTask(this);
             return true;
        } else if (id == R.id.action_share) {
            // share intent
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            sendIntent.putExtra(Intent.EXTRA_TEXT, date + " \n\n" + url);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        } else if (id == R.id.action_web) {
            // Open Story in Browser
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        } else if (id == R.id.action_story_audio) {
            Log.d("StoryActivity", audio);
            Intent intent = new Intent(this, MediaActivity.class);
            intent.putExtra("url", audio); //can't pass in article object
            intent.putExtra("title", title);
            startActivityForResult(intent, 0); //Activity load = 0
            return true;
        } else if (id == R.id.action_story_video) {
            Log.d("StoryActivity", video);
            Intent intent = new Intent(this, MediaActivity.class);
            intent.putExtra("url", video); //can't pass in article object?
            intent.putExtra("title", title);
            startActivityForResult(intent, 0); //Activity load = 0
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class RetrieveContent extends AsyncTask<String, Void, String> {
        private Exception exception;
        protected String doInBackground(String... urls){
            try {
                String cont = getContent(urls[0]);
                return cont;
            }catch (Exception e){
                this.exception = e;
                Log.v("Story Failure:", e.toString());
                return null;
            }
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            String page = "<h2>" + title + "</h2><hr>"+date+"<hr>" + "Viewer Supported News: <a class='donate_button' data-width='800' data-height='590' data-ga-action='Story: Donate' href='https://democracynow.org/donate'>Donate</a><br>Donate at democracynow.org<hr>" + result;
            webview.loadData(page, "text/html; charset=utf-8", "UTF-8"); //but don't just
        }
    }

    // Use Jsoup to get the content? This is sloppy
    private String getContent(String url) throws IOException {
        // DN feed starting spitting out this http://www.democracynow.org:443/2017/5/12/on_black_mamas_bail_out_day
        // so I got to make sure the port isn't included anymore! It'll be fixed soon I bet.
        url = url.replaceFirst(":443", "");
        Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
        Element data;
        // Get the Individual Videos
        Element videoElem = doc.getElementsByClass("download_video").get(0);
        Element audioElem = doc.getElementsByClass("download_audio").get(0);
        audio = audioElem.attr("abs:href");
        video = videoElem.attr("abs:href");
        // Get the Transcript URL
        if (doc.getElementById("headlines") == null){
            data = doc.getElementsByClass("story_with_left_panel").first();// get the third content div,
            data.getElementsByClass("audio_player_container").remove();
            data.getElementsByClass("close").remove();
            data.getElementsByClass("controls").remove();
            data.getElementsByClass("left_panel").remove();
            data.getElementsByClass("get_cd_dvd").remove();
            data.getElementsByClass("download_video").remove();
            data.getElementsByClass("other_formats").remove();
            data.getElementsByClass("download_audio").remove();
            data.getElementsByClass("show_modal").remove();
            data.getElementsByClass("donate_banner").remove();
            data.getElementById("social_download_modal").remove();
            data.getElementsByClass("share_mobile").remove();
            data.getElementsByClass("share_counter").remove();
        } else {
            data = doc.getElementById("headlines");// get the third content div,
        }
        // Change the links to absolute!! so that images work
        Elements select_img = data.select("img");
        Elements select = data.select("a");
        for(Element e:select_img){e.attr("src", e.absUrl("src"));}
        for(Element e:select){e.attr("href", e.absUrl("href"));}
        data.getElementsByClass("donate_container").remove();
        String cont = data.toString();
        cont = CSS + cont + "</body>";
        content = cont;
        return cont;
    }
}
