/*
 * Copyright (C) 2014 Shirwa Mohamed <shirwa99@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.workingagenda.democracydroid.Feedreader;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RssReader {
    private String rssUrl;
    private URL url;
    private String nameSpace;

    public RssReader(String url) {
        rssUrl = url;
    }


    public List getRssItems(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser= Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            //parser.setInput(in, XmlPullPa);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                entries.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private RssItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "item");
        String title = null;
        String description = null;
        String link = null;
        String imageUrl = null;
        String videoUrl = null;
        String pubDate = null;
        while(parser.next() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            if (name.equals("title"))
                title = readTitle(parser);
            else if (name.equals("link"))
                link = readLink(parser);
            else if (name.equals("pubDate"))
                pubDate = readPubDate(parser);
            else if (name.equals("description"))
                description = readDescription(parser);
            else if (name.equals("media:thumbnail") || name.equals("image")) {
                if (parser.getAttributeValue(this.nameSpace, "url") != null){
                    imageUrl = readImageUrl(parser);
                }
            }
            else if (name.equals("media:content")){
                if (parser.getAttributeValue(this.nameSpace, "url") != null){
                    videoUrl = readVideoUrl(parser);
                }
            }
        }
        return new RssItem(title, description, link, imageUrl, videoUrl, pubDate);
    }

    private String readVideoUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "media:content");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getAttributeValue(this.nameSpace, "url");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, this.nameSpace, "media:content");
        return result;
    }

    private String readImageUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "media:thumbnail");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getAttributeValue(this.nameSpace, "url");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, this.nameSpace, "media:thumbnail");
        return result;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "description");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, this.nameSpace, "description");
        return result;
    }

    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "pubDate");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, this.nameSpace, "pubDate");
        return result;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "link");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, this.nameSpace, "link");
        return result;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "title");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, this.nameSpace, "title");
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public List<RssItem> getItems() throws Exception {
        //SAXParserFactory factory = SAXParserFactory.newInstance();
        //SAXParser saxParser = factory.newSAXParser();
    //    XMLReader xr = saxParser.getXMLReader();
        //Creates a new RssHandler which will do all the parsing.
        //RssHandler handler = new RssHandler();
        //xr.setContentHandler(handler);
        //InputSource inStream =new InputSource();
        ///purl = new URL(rssUrl);
        //HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        //inStream.setCharacterStream(new InputStreamReader(httpconn.getInputStream()));
        //xr.parse(inStream);
        //Pass SaxParser the RssHandler that was created.
        //inStream.setEncoding(String.valueOf(Xml.Encoding.UTF_8));
        //Log.d("Encoding", httpconn.getContentEncoding());y
        //saxParser.parse(rssUrl, handler);
        //saxParser.parse(inStream, handler);
        //
        //HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        //Xml.parse(url.openStream(), Xml.Encoding.UTF_8, handler);
        this.nameSpace = null;//"http://www.w3.org/2005/Atom";//null;//"http://www.w3.org/2005/Atom";
        InputStream stream =null;
        List<RssItem> items = null;
        URL url = new URL(rssUrl);
        Document doc = Jsoup.connect(rssUrl).get();

        //Log.d("JSOUP", doc.toString());
        //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //conn.setReadTimeout(10000);
        //conn.setConnectTimeout(15000);
        //conn.setRequestMethod("GET");
        //conn.setDoInput(true);
        //conn.connect();
        try {
            //stream = conn.getInputStream();
            //Log.d("TO STRING", conn.getResponseMessage());
            //Log.d("TO STRING", conn.getContent().toString());
            stream = new ByteArrayInputStream(doc.toString().getBytes(StandardCharsets.UTF_8));
            items = getRssItems(stream);
        } finally {
            //if (stream != null) {
                //stream.close();
            //}
        }
         return items;
        //return handler.getRssItemList();
    }
}