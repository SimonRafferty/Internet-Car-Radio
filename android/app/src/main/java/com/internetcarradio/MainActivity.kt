package com.internetcarradio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on while app is active
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Create and configure WebView
        webView = WebView(this)
        setContentView(webView)

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
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
        // Don't pause timers so audio keeps playing in background
        // webView.pauseTimers()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}
