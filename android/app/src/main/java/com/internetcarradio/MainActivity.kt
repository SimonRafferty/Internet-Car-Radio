package com.internetcarradio

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private lateinit var mediaSession: MediaSessionCompat
    private var isPlaying = false
    private var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on while app is active
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Acquire wake lock for background audio
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "InternetCarRadio:AudioPlayback"
        )

        // Initialize media session for steering wheel controls
        setupMediaSession()

        // Create and configure WebView
        webView = WebView(this)
        setContentView(webView)

        // Add JavaScript interface for media control communication
        webView.addJavascriptInterface(MediaControlInterface(), "AndroidMediaControl")

        // Configure WebView settings
        webView.settings.apply {
            // Enable JavaScript (required for the app)
            javaScriptEnabled = true

            // Enable DOM storage for localStorage
            domStorageEnabled = true

            // Enable media playback without user gesture
            mediaPlaybackRequiresUserGesture = false

            // Enable mixed content (HTTP audio streams on HTTPS page)
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            // Cache settings for better performance
            cacheMode = WebSettings.LOAD_DEFAULT
            databaseEnabled = true

            // Enable viewport settings
            useWideViewPort = true
            loadWithOverviewMode = true

            // Disable zoom controls
            builtInZoomControls = false
            displayZoomControls = false

            // Allow file access for assets
            allowFileAccess = true
            allowContentAccess = true
        }

        // Configure WebViewClient to handle navigation
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Keep all navigation within the WebView
                return false
            }
        }

        // Configure WebChromeClient for fullscreen support
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                // Enter fullscreen mode
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                customView = view
                customViewCallback = callback

                // Hide system UI for true fullscreen
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

                setContentView(customView)
            }

            override fun onHideCustomView() {
                // Exit fullscreen mode
                customView = null
                setContentView(webView)

                // Restore system UI
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
            }

            override fun getDefaultVideoPoster(): android.graphics.Bitmap? {
                // Return null to avoid showing default video poster
                return null
            }
        }

        // Load the web app from assets
        webView.loadUrl("file:///android_asset/index.html")
    }

    private fun setupMediaSession() {
        // Create media session for handling media button events (steering wheel controls)
        mediaSession = MediaSessionCompat(this, "InternetCarRadio")

        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        // Set up media session callbacks
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                // Play button pressed on steering wheel
                webView.evaluateJavascript("window.handleMediaPlay();", null)
                updatePlaybackState(true)
            }

            override fun onPause() {
                // Pause button pressed on steering wheel
                webView.evaluateJavascript("window.handleMediaPause();", null)
                updatePlaybackState(false)
            }

            override fun onSkipToNext() {
                // Next button pressed on steering wheel
                webView.evaluateJavascript("window.handleMediaNext();", null)
            }

            override fun onSkipToPrevious() {
                // Previous button pressed on steering wheel
                webView.evaluateJavascript("window.handleMediaPrevious();", null)
            }

            override fun onStop() {
                // Stop button pressed
                webView.evaluateJavascript("window.handleMediaPause();", null)
                updatePlaybackState(false)
            }
        })

        // Activate the media session
        mediaSession.isActive = true
        updatePlaybackState(false)
    }

    private fun updatePlaybackState(playing: Boolean) {
        isPlaying = playing
        val state = if (playing) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_STOP
            )
            .setState(state, 0, 1.0f)
            .build()

        mediaSession.setPlaybackState(playbackState)

        // Manage foreground service and wake lock based on playback state
        if (playing) {
            startMediaService()
            wakeLock?.acquire(10*60*1000L) // 10 minutes timeout
        } else {
            stopMediaService()
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        }
    }

    private fun startMediaService() {
        val intent = Intent(this, MediaService::class.java).apply {
            action = "START_FOREGROUND"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopMediaService() {
        val intent = Intent(this, MediaService::class.java).apply {
            action = "STOP_FOREGROUND"
        }
        startService(intent)
    }

    // JavaScript interface for WebView to communicate playback state
    inner class MediaControlInterface {
        @JavascriptInterface
        fun notifyPlaying() {
            runOnUiThread {
                updatePlaybackState(true)
            }
        }

        @JavascriptInterface
        fun notifyPaused() {
            runOnUiThread {
                updatePlaybackState(false)
            }
        }
    }

    override fun onBackPressed() {
        // If in fullscreen, exit fullscreen first
        if (customView != null) {
            webView.webChromeClient?.onHideCustomView()
            return
        }

        // If WebView can go back, go back
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }

        // Otherwise, exit the app
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        webView.resumeTimers()
        mediaSession.isActive = true
    }

    override fun onPause() {
        super.onPause()
        // Don't call webView.onPause() - allows audio to continue in background
        // Don't call pauseTimers() - keeps JavaScript timers running
    }

    override fun onStop() {
        super.onStop()
        // Still don't pause - allow background playback
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMediaService()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        mediaSession.release()
        webView.destroy()
    }

    // Handle media button key events (steering wheel controls)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_MEDIA_NEXT,
            KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                // Let media session handle these
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
