Democracy Droid
===============

#Disclaimer
I have no affiliation with Democracy Now! (They won't even return my emails).

## Democracy Droid
This program parses the [Democracy Now!](http://democracynow.org) RSS-feed and it links to the video broadcasts. The application 
sends the intent to open the video in either the Gallery viewer (or something of its kind), VLC (recommended), or the Browser. If you don't 
feel like compiling from the source, look in the build folder for an app-release.apk and install Democracy Droid on your phone.

Find v.1.1 on the Play Store

## License
- Democracy Droid GPLv3
- SimplisticRss library Apache 2 - [ShirwaM](https://github.com/ShirwaM/Simplistic-RSS)
- Picasso Apache 2 - http://square.github.io/picasso/
- Democracy Now! CC BY-NC-ND 3.0 US
- Jsoup MIT

## Bug Fixes for 1.5.2
- Unicode support for viewing Transcripts
- Downloads list includes post-show podcasts
- Broadcast listview doesn't cut off titles
- Remove broken blog feed tab
- Fix transcript 'headline' formatting confusion...

## New Features 1.5.2 
- Add alert dialog for clear all

## Bugs
- When swiping through tabs, listviews duplicate
   - But website isn't getted 
- When scrolling up, list position isn't absolute
  - When formatting 'Headlines', it gets confused...

## TODO
- Stories Thumbnail
- Blog Activity/ like Stories
- (More) Parse Downloaded File Label
- settings
   - No Images
   - Notifications
- Fix Live Stream Link?   

### Listviews TODO:
- Add download button
- Add stream button
- Delete/see progress of downloads

### Settings TODO:
- Default Stream Secondary Stream
- Notifications (default no)
- Language
