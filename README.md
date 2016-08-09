Democracy Droid
===============

#Disclaimer
I have no affiliation with Democracy Now!

## Democracy Droid
This program parses the [Democracy Now!](http://democracynow.org) RSS-feed and it links to the video broadcasts. The application 
sends the intent to open the video in either the Gallery viewer (or something of its kind), VLC or Firefox (recommended), or the Browser. If you don't
feel like compiling from the source, look in the build folder for an app-release.apk and install Democracy Droid on your phone.

Find v.1.1 on the Play Store

## License
- Democracy Droid GPLv3
- SimplisticRss library Apache 2 - [ShirwaM](https://github.com/ShirwaM/Simplistic-RSS)
- Picasso Apache 2 - http://square.github.io/picasso/
- Democracy Now! CC BY-NC-ND 3.0 US
- Jsoup MIT

## Bug Fixes for 1.6.0
- Today's Broadcast (updating the feed before the official RSS feed does) actually works
- Audio broadcasts are no longer offset

## New Features 1.6.0
- New Preferences have been implemented
- A progressbar indicates loading
- Even fewer crashes!

## Bugs
- Can load feed multiple times
- When scrolling up, list position isn't absolute
  - When formatting 'Headlines', it gets confused...

## TODO
- Stories Thumbnail
- (More) Parse Downloaded File Label
- settings
   - Notifications
- Fix Live Stream Link?   

### Listviews TODO:
- Add download button
- Add stream button
- Delete/see progress of downloads

### Settings TODO:
- Notifications (default no)
- Language
