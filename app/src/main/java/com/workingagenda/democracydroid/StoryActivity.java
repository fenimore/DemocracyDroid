package com.workingagenda.democracydroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private TextView testing;
    private WebView webview;

    private String title;
    private String date;
    private String url;
    private String content;
    private String author;
    private static final String CSS = "<head><style type='text/css'> "
            + "body {max-width: 100%; margin: 0.3cm; font-family: sans-serif-light; color: black; background-color: #f6f6f6; line-height: 150%} "
            + "* {max-width: 100%; word-break: break-word}"
            + "h1, h2 {font-weight: normal; line-height: 130%} "
            + "h1 {font-size: 170%; margin-bottom: 0.1em} "
            + "h2 {font-size: 140%} "
            + "a {color: #0099CC}"
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
        toolbar.setLogo(R.mipmap.ic_launcher);

        Bundle extras = getIntent().getExtras();
        url = (String) extras.get("url");
        title = (String) extras.get("title");
        date = (String) extras.get("date");
        Log.v("url", url);
        new RetrieveContent().execute(url);


        //WebView webview = new WebView(this);
        //setContentView(webview);
        //webview.loadData(content, "text/html", "UTF-8"); //but don't just load the URL, but load te content div within. Yikes.
    }

    public interface OnTaskCompleted{
        void onTaskCompleted();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
            return true;
        }
        if (id == R.id.action_share) {
            // share intent
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            sendIntent.putExtra(Intent.EXTRA_TEXT, url + " '\n" +date);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
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
                return null;
            }
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            String page = "<h2>" + title + "</h2><hr>"+date+"<hr>" + result;
            webview.loadData(page, "text/html", "UTF-8"); //but don't just
        }
    }

    // Use Jsoup to get the content? This is sloppy
    private String getContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
        //doc.select("story_with_left_panel").first().children().first().before("<a href="+ url +">Watch the Broadcast at democracynow.org</newChild>");
        Element data = doc.getElementsByClass("story_with_left_panel").first();// get the third content div,
        data.getElementsByClass("left_panel").remove();
        // Change the links to absolute!! so that images work
        Elements select = data.select("img");
        select = data.select("a");
        for(Element e:select){e.attr("src", e.absUrl("src"));}
        for(Element e:select){e.attr("href", e.absUrl("href"));}
        String cont = data.toString();
        cont = CSS + cont + "</body>";
        content = cont;
        return cont;
    }
}
