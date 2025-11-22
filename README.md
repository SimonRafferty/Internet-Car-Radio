# Internet Car Radio

A free, open-source internet radio app with support for both web browsers and Android devices (including car head units).

## Overview

After purchasing four different paid radio apps that all turned out to be unreliable and buggy, I built my own solution. This project provides a simple, clean interface for streaming internet radio with no ads, no tracking, and no unnecessary complexity.

## Features

- Stream from 40,000+ internet radio stations via Radio Browser API
- 16 customizable favorite slots with station logos
- Browse stations by genre (Electronic, Rock, Pop, Jazz, Classical, Country, and more)
- Search stations by name
- Automatic reconnection on network interruption
- Resume last played station on app restart
- Responsive design optimized for car head units and mobile phones
- Completely free with no advertisements

## Available Versions

### Web Version

A standalone HTML file that runs in any modern web browser. Perfect for desktop use or testing on mobile devices.

**Quick Start:**
- **Download and run locally:** Download `Internet Car Radio.html` and open in your browser
- **Run from GitHub:** [Click here to run directly](https://htmlpreview.github.io/?https://github.com/SimonRafferty/Internet-Car-Radio/blob/main/Internet%20Car%20Radio.html)

**Features:**
- Single HTML file with embedded CSS and JavaScript
- Works offline once loaded
- LocalStorage for favorites persistence
- Fullscreen mode support
- Big buttons suitable for use while driving
- Dark theme for night use

### Android Version

Native Android app optimized for car head units and mobile phones. Overcomes browser limitations such as autoplay restrictions and fullscreen access.

**Features:**
- Auto-resume last station without user interaction
- True fullscreen mode (no browser chrome)
- Background audio playback
- Works on Android 7.0 (API 24) and above
- Optimized layouts: 4 columns for tablets/head units, 2 columns for phones

**Installation:**
Download the APK from the [Releases](https://github.com/SimonRafferty/Internet-Car-Radio/releases) page and install on your Android device.

## Why This Exists

There are plenty of internet radio apps available, but none worked well on car head units. After wasting money on four paid apps that were buggy messes, the only option was to build my own. This app focuses on simplicity, reliability, and usability while driving.

## Building from Source

### Android APK

**Requirements:**
- Android Studio (Arctic Fox or later)
- JDK 17
- Android SDK with API 34

**Instructions:**

1. Clone this repository:
```bash
git clone https://github.com/SimonRafferty/Internet-Car-Radio.git
cd Internet-Car-Radio
```

2. Open the `android` folder in Android Studio

3. Sync project with Gradle files (File → Sync Project with Gradle Files)

4. Build the APK:
   - Build → Build Bundle(s) / APK(s) → Build APK(s)

5. The APK will be located at:
   ```
   android/app/build/outputs/apk/release/Internet Car Radio.apk
   ```

**Creating a Signed Release Build:**

For publishing to the Google Play Store or distributing to others, you need a signed APK:

1. Build → Generate Signed Bundle / APK
2. Select Android App Bundle (for Play Store) or APK (for direct distribution)
3. Create a new keystore or use an existing one
4. Save your keystore file and passwords securely
5. Select the release build variant
6. The signed APK/AAB will be generated

**Important:** Keep your keystore file and passwords safe. If you lose them, you cannot update your app on the Play Store.

For detailed build instructions, see `android/README.md`.

### Customizing the Web Version

The web version is a single HTML file. To modify:

1. Open `Internet Car Radio.html` in a text editor
2. Make your changes to the HTML, CSS, or JavaScript
3. Save and open in a browser to test
4. No build process required

## Architecture

### Web Version

- **Frontend:** Vanilla HTML5, CSS3, and JavaScript
- **Data Source:** Radio Browser API (de1.api.radio-browser.info)
- **CORS Proxy:** api.allorigins.win
- **Storage:** Browser LocalStorage
- **Audio:** HTML5 Audio API

### Android Version

- **Language:** Kotlin
- **UI Framework:** WebView (wraps the HTML version)
- **Minimum SDK:** API 24 (Android 7.0)
- **Target SDK:** API 34 (Android 14)
- **Dependencies:** AndroidX AppCompat, Core-KTX

The Android app is essentially a native container for the web version, which allows it to bypass browser security restrictions while maintaining the same UI and functionality.

## Project Structure

```
Internet-Car-Radio/
├── Internet Car Radio.html    # Standalone web version
├── InternetCarRadioIcon.png   # App icon (PNG format)
├── InternetCarRadioIcon.jpg   # App icon (JPG format)
├── android/                   # Android Studio project
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/internetcarradio/MainActivity.kt
│   │   │   ├── res/          # Android resources
│   │   │   └── assets/
│   │   │       └── index.html  # Web app embedded in APK
│   │   └── build.gradle
│   ├── build.gradle
│   ├── settings.gradle
│   └── gradle.properties
├── README.md                  # This file
└── .gitignore
```

## Technical Details

### Radio Station Data

Stations are fetched from the Radio Browser Community API:
- Over 40,000 verified internet radio stations
- Organized by genre tags
- Stations filtered to show only those verified within the last 7 days
- Sorted by vote count and logo availability

### Responsive Design

The interface adapts to different screen sizes:
- **Desktop/Head Units (>700px width):** 4-column grid layout
- **Mobile Phones (≤700px width):** 2-column grid layout
- **Top control bar:** Wraps on narrow screens with buttons stacked above station info

### Data Persistence

- **Web Version:** Uses browser LocalStorage to save favorites and last played station
- **Android Version:** Uses WebView's localStorage implementation for the same functionality

### Network Resilience

- Automatic retry on connection loss (10-second intervals)
- Debounced error handling to prevent false positives
- Stall detection (3+ consecutive stalls triggers retry)
- Visual feedback during reconnection attempts

## Browser Compatibility

### Web Version

Tested and working on:
- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Opera 76+

Note: Some browsers may block autoplay or fullscreen requests due to security policies. If you enable audio autoplay in your browser settings, the app will automatically resume the last station on startup.

### Android WebView

The Android version uses the system WebView with:
- JavaScript enabled
- DOM storage enabled
- Media playback without user gesture (bypasses autoplay restrictions)
- Mixed content allowed (for HTTP radio streams)

## Known Limitations

### Web Version
- Browser autoplay policies may prevent automatic playback on page load
- Fullscreen API may be restricted on some devices
- Some browsers limit background audio playback

### Android Version
- Minimum Android version: 7.0 (API 24)
- APK must be signed for installation (debug builds work for testing)
- Some older car head units may not support modern WebView features

## Contributing

Contributions are welcome! Areas for improvement:

- Additional genre categorizations
- Station metadata display (bitrate, codec, country)
- Playlist support
- Sleep timer
- Equalizer integration
- Chromecast support
- Translations to other languages

Please follow these guidelines:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly on both web and Android versions
5. Submit a pull request with a clear description

## License

MIT License

Copyright (c) 2024 Simon Rafferty

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Privacy

This app does not collect, store, or transmit any personal data. All favorites and settings are stored locally on your device. The app accesses public internet radio streaming APIs but does not track your listening habits.

## Credits

- Built by Simon Rafferty
- Radio station data provided by [Radio Browser](https://www.radio-browser.info/)
- CORS proxy provided by [AllOrigins](https://allorigins.win/)

## Support

If you find this app useful and want to support development:

[![Sponsor on GitHub](https://img.shields.io/badge/Sponsor-GitHub-pink)](https://github.com/sponsors/SimonRafferty)

## Links

- GitHub Repository: https://github.com/SimonRafferty/Internet-Car-Radio
- Issue Tracker: https://github.com/SimonRafferty/Internet-Car-Radio/issues
- Radio Browser API: https://www.radio-browser.info/

## Version History

See [Releases](https://github.com/SimonRafferty/Internet-Car-Radio/releases) for version history and downloads.
