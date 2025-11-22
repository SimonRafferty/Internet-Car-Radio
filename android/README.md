# Internet Car Radio - Android App

This is the Android version of the Internet Car Radio web app, packaged as a native Android application using WebView.

## Features

- **No Browser Restrictions**: Runs in a native WebView, bypassing browser security limitations
- **Fullscreen Support**: True fullscreen mode works on Android Car head units
- **Auto-Resume**: Automatically resumes the last played station on app launch
- **Background Audio**: Continues playing when app is in background
- **Keep Screen On**: Prevents screen from sleeping while app is active
- **Offline Storage**: Favorites persist using WebView's localStorage

## Prerequisites

To build this project, you need:

1. **Android Studio** (Arctic Fox or later recommended)
   - Download from: https://developer.android.com/studio

2. **Java Development Kit (JDK) 17**
   - Usually bundled with Android Studio
   - Or download from: https://adoptium.net/

3. **Android SDK** with the following components:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools

## Building the APK

### Option 1: Using Android Studio (Recommended)

1. **Open the Project**
   - Launch Android Studio
   - Click "Open" and select the `android` folder
   - Wait for Gradle sync to complete (may take several minutes on first run)

2. **Build the Debug APK**
   - Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**
   - Wait for the build to complete
   - The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

3. **Build the Release APK** (for production use)
   - Click **Build → Generate Signed Bundle / APK**
   - Select **APK** and click **Next**
   - Create a new keystore or use an existing one
   - Fill in the keystore details and click **Next**
   - Select **release** build variant
   - Click **Finish**
   - The signed APK will be in: `app/build/outputs/apk/release/`

### Option 2: Using Command Line

1. **Build Debug APK**
   ```bash
   cd android
   ./gradlew assembleDebug
   ```
   The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

2. **Build Release APK** (unsigned)
   ```bash
   cd android
   ./gradlew assembleRelease
   ```
   The APK will be at: `app/build/outputs/apk/release/app-release-unsigned.apk`

3. **Sign the Release APK** (required for installation)
   ```bash
   # Create a keystore (first time only)
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias

   # Sign the APK
   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore my-release-key.jks app/build/outputs/apk/release/app-release-unsigned.apk my-key-alias

   # Align the APK
   zipalign -v 4 app/build/outputs/apk/release/app-release-unsigned.apk app/build/outputs/apk/release/app-release.apk
   ```

## Installing the APK

### On Windows (via USB)

1. Enable **Developer Options** on your Android device
2. Enable **USB Debugging** in Developer Options
3. Connect your device via USB
4. Run:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### On Android Device Directly

1. Copy the APK to your device
2. Open the APK file
3. Allow installation from unknown sources if prompted
4. Install the app

### On Android Car Head Unit

1. Copy the APK to a USB drive
2. Insert USB drive into car head unit
3. Use a file manager app to navigate to the APK
4. Install the app
5. (Alternative) Use `adb` over WiFi if your head unit supports it

## Configuration

### Updating the Web App

To update the web app content:

1. Edit `app/src/main/assets/index.html`
2. Rebuild the APK
3. Install the new APK (it will update the existing app)

### Changing App Name or Icon

**App Name:**
- Edit `app/src/main/res/values/strings.xml`
- Change the `app_name` value

**App Icon:**
- Replace the icon files in `app/src/main/res/mipmap-*/`
- Use Android Studio's Image Asset Studio for best results:
  - Right-click `res` → New → Image Asset
  - Select your icon image
  - Generate icons for all densities

### Changing Package Name

If you want to change the package name (e.g., to publish to Play Store):

1. Edit `app/build.gradle` - change `applicationId`
2. Edit `AndroidManifest.xml` - change `package` attribute
3. Rename the Java package folder: `com/internetcarradio` → `your/package/name`
4. Update package declaration in `MainActivity.kt`

## Troubleshooting

### Build Errors

**"Failed to sync Gradle"**
- Check your internet connection
- Wait for Gradle to finish downloading dependencies
- Try **File → Invalidate Caches / Restart**

**"SDK location not found"**
- Create `local.properties` file in the `android` folder:
  ```
  sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
  ```
  (Use your actual SDK path)

### Runtime Issues

**"App won't install"**
- Make sure you've uninstalled any previous version
- Check that the APK is signed (required for release builds)
- Enable "Install from Unknown Sources" on your device

**"Audio won't play"**
- Check internet connection
- Verify the Radio Browser API is accessible
- Try a different station
- Check device volume settings

**"Fullscreen doesn't work"**
- This is expected in debug mode on some devices
- Use the fullscreen button in the app's UI instead

**"App crashes on startup"**
- Check Android Studio's Logcat for error messages
- Ensure your device is running Android 7.0 (API 24) or higher
- Try clearing app data: Settings → Apps → Internet Car Radio → Storage → Clear Data

## Technical Details

- **Minimum Android Version**: 7.0 (API 24)
- **Target Android Version**: 14.0 (API 34)
- **Programming Language**: Kotlin
- **UI Framework**: Native WebView
- **Build System**: Gradle 8.2

## Permissions

The app requests the following permissions:

- **INTERNET**: Required to stream radio stations
- **ACCESS_NETWORK_STATE**: Check network connectivity
- **ACCESS_WIFI_STATE**: Check WiFi status
- **WAKE_LOCK**: Keep device awake during playback
- **FOREGROUND_SERVICE**: Allow background audio playback

## License

Same as the parent Internet Car Radio project.
