Democracy Droid
===============

## Democracy Droid!

This is a [Democracy Now!](http://democracynow.org) Android app which 
streams and manages downloads for the video and audio broadcast of Democracy Now! 
It also streams the live-stream (at 8am Eastern) and acts as a transcript 
reader for individual stories and headlines. It is a fully featured app :). 

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Get it on Google Play"
      height="80">](https://play.google.com/store/apps/details?id=com.workingagenda.democracydroid)

## Disclaimer

I have no affiliation with Democracy Now!

## License

- Democracy Droid GPLv3
- Democracy Now! CC BY-NC-ND 3.0 US
- Picasso Apache 2 - http://square.github.io/picasso/
- ExoPlayer Apache 2
- Jsoup MIT

## Latest Release

2.7.0

## Change Log

### 2.7.0

- Fix: Streaming issue
- Add: Listen with app in the background

### 2.6.1

- Fix: null pointer reference crashing on some phones

### 2.6.0

- Add: Now when switching to another app (like for taking a phone call or texting), and then returning the Democracy Droid! The video/audio playback saves your place and starts where you left off.
- Fix: I've fixed a few small bugs that led to crashes for certain users (ehem, including myself :S)

### 2.5.0

- New! Totally new parser, the previous version are broken.
- Add: Swipe to refresh the broadcast tab.
- Add: Change the default stream for Video in settings. (in external application).

### 2.4.1

- Add: Rotating screen doesn't refresh video/audio stream!
- Fix: No silly margins in download list
- Fix: When returning to list, selected item is re-clickable
- Fix: No more double loading story feed
- Fix: Status bar and Media controller toggles better
- Add: Headlines special formatting in stories tab

## Bugs

- **Issues streaming Video within the application**.
- Can load, and crash stories before Jsoup does it's job.

## Todo list:

- [ ] Add Spanish Language Feed
- [ ] Add empty screen for podcast feed
- [ ] Stream while downloading
- [ ] Fix the Bugs.
- [ ] Add listview buttons
- [ ] Add Stories downloads?
- [ ] (More) Parse Downloaded File Label
- [ ] Download cancel/progress in notifications
- [ ] settings
   - [ ] Notifications
- [x] Make stories Headlines item a bit nicer...
- [x] Pull down to refresh
    - https://developer.android.com/training/swipe/add-swipe-interface.html
