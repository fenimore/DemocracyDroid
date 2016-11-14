Democracy Droid
===============

## Democracy Droid
This program parses the [Democracy Now!](http://democracynow.org) RSS-feed and it streams or downloads video, audio, or individual story broadcasts. It is a fully featured app :). Look in the app folder for an app-release.apk and install Democracy Droid on your phone.

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Get it on Google Play"
      height="80">](https://play.google.com/store/apps/details?id=com.workingagenda.democracydroid)
      
## Disclaimer
I have no affiliation with Democracy Now!

## License
- Democracy Droid GPLv3
- SimplisticRss library Apache 2 - [ShirwaM](https://github.com/ShirwaM/Simplistic-RSS)
- Picasso Apache 2 - http://square.github.io/picasso/
- Democracy Now! CC BY-NC-ND 3.0 US
- Jsoup MIT

## Latest Release
2.4.2

### What's new?
- Swipe to refresh the broadcast tab.
- So there's a huge bug when streaming Video within the app. 
Basically, it doesn't work after a few minutes of watching. 
So I've made the default stream stream in the browser/video player. I hope
this fixes your problems!?


### What's new in 2.4.1
- Rotating screen doesn't refresh video/audio stream!
- Fix: No silly margins in download list
- Fix: When returning to list, selected item is re-clickable
- Fix: No more double loading story feed
- Fix: Status bar and Media controller toggles better
- Add: Headlines special formatting in stories tab

## Bugs
- **Issues streaming Video within the application**.
- Can load, and crash stories before Jsoup does it's job.

## TODO
- Stream while downloading
- Fix the Bugs.
- Add listview buttons
- ~~Stories Thumbnail~~ maybe not?
- Add Stories downloads?
- (More) Parse Downloaded File Label
- settings
   - Notifications
- Make stories Headlines item a bit nicer...
- Pull down to refresh (For other tabs than broadcast)
	- https://developer.android.com/training/swipe/add-swipe-interface.html

### Listviews TODO:
- Add download button
- Add stream button
- Delete/see progress of downloads

### Settings TODO:
- Notifications (default none)
- Language
