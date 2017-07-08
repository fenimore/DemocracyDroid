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

import android.util.Xml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RssReader {
    private String rssUrl;
    private String nameSpace;

    public RssReader(String url) {
        rssUrl = url;
    }


    private List getRssItems(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser= Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
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
        // TODO: default not null?
        String title = null;
        String description = null;
        String link = null;
        String imageUrl = null;
        String videoUrl = null;
        String pubDate = null;
        String contentEnc = null;
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title"))
                title = readTitle(parser);
            else if (name.equals("link"))
                link = readLink(parser);
            else if (name.equals("description"))
                description = readDescription(parser);
            else if (name.equals("pubDate"))
                pubDate = readPubDate(parser);
            else if (name.equals("media:thumbnail"))
                imageUrl = readImageUrl(parser);
            else if (name.equals("media:content"))
                videoUrl = readVideoUrl(parser);
            else if (name.equals("content:encoded"))
                contentEnc = readContentEnc(parser);
            else {
                skip(parser);
            }
        }
        return new RssItem(title, description, link, imageUrl, videoUrl, pubDate, contentEnc);
    }

    private String readVideoUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "media:content");
        String result = "";
        result = parser.getAttributeValue(this.nameSpace, "url");
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
               continue;
            }
            skip(parser);
        }

        parser.require(XmlPullParser.END_TAG, this.nameSpace, "media:content");
        return result;
    }

    private String readImageUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "media:thumbnail");
        String result = "";
        result = parser.getAttributeValue(this.nameSpace, "url");
        parser.nextTag();
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
        result = result.trim();
        return result;
    }

    private String readContentEnc(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, this.nameSpace, "content:encoded");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            if (result.contains("src=")) {
                result = result.substring(result.indexOf("src=") + 5 );
                result = result.substring(0, result.indexOf("\""));
            }
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, this.nameSpace, "content:encoded");
        result = result.trim();
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
        if (parser.getName().equals("channel")) {
            return;
        }
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

    public List<RssItem> getItems() throws Exception {
        this.nameSpace = null;//"http://www.w3.org/2005/Atom";//null;//"http://www.w3.org/2005/Atom";
        InputStream stream;
        List<RssItem> items;
        Document doc = Jsoup.connect(rssUrl).get();
        stream = new ByteArrayInputStream(doc.toString().replaceAll("&nbsp", " ").getBytes(Charset.forName("UTF-8")));
        items = getRssItems(stream);
         return items;
    }
}