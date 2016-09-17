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
2.4.0

### Bug Fixes
- Rotating screen doesn't refresh video/audio stream!
- Fix: No silly margins in download list
- Fix: When returning to list, selected item is re-clickable
- Fix: No more double loading story feed
- Fix: Status bar and Media controller toggles better

## Bugs
- When scrolling up, list position isn't absolute
  - When formatting 'Headlines', it gets confused...
- Can load, and crash stories before Jsoup does it's job.

## TODO
- Add listview buttons
- ~~Stories Thumbnail~~ maybe not?
- Add Stories downloads?
- (More) Parse Downloaded File Label
- settings
   - Notifications
- Fix Live Stream Link?

### Listviews TODO:
- Add download button
- Add stream button
- Delete/see progress of downloads

### Settings TODO:
- Notifications (default none)
- Language
