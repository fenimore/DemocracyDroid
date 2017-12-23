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


public class RssItem {
    private String title;
    private String description;
    private String link;
    private String imageUrl;
    private String videoUrl;
    private String pubDate;
    private String contentEnc; // story feed image_urls are stored here

    public RssItem(String title, String description, String link, String imageUrl, String videoUrl, String pubDate, String contentEnc) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.pubDate = pubDate;
        this.contentEnc = contentEnc;
    }


    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    private String test;

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {return videoUrl;}


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void addToDescription(String addition){
        if(this.description == null){
            this.description = addition;
        } else {
            this.description.concat(addition);
        }
    }

    public String getContentEnc() {
        return contentEnc;
    }

    public void setContentEnc(String contentEnc) {
        this.contentEnc = contentEnc;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}