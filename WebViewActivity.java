package com.example.travelplane;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

/**
 * WebView Activity
 * Opens travel websites inside the app
 */
public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private MaterialButton btnBooking, btnGoogleMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Travel Websites");
        }

        // Initialize views
        initViews();

        // Setup WebView
        setupWebView();

        // Load incoming URL if provided
        String incomingUrl = getIntent().getStringExtra("url");
        String incomingTitle = getIntent().getStringExtra("title");
        if (incomingTitle != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(incomingTitle);
        }
        if (incomingUrl != null && !incomingUrl.isEmpty()) {
            webView.loadUrl(incomingUrl);
        } else {
            webView.loadUrl("https://www.booking.com");
        }

        // Setup button listeners
        setupListeners(incomingTitle);
    }

    /**
     * Initialize views
     */
    private void initViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        btnBooking = findViewById(R.id.btnBooking);
        btnGoogleMaps = findViewById(R.id.btnGoogleMaps);
    }

    /**
     * Setup WebView settings
     */
    private void setupWebView() {
        // Enable JavaScript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        // Stay in-app
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Progress handling
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(android.view.View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(android.view.View.GONE);
                }
            }
        });
    }

    /**
     * Setup button click listeners
     */
    private void setupListeners(String title) {
        btnBooking.setOnClickListener(v -> webView.loadUrl("https://www.booking.com"));

        // Use Maps search endpoint for reliability
        btnGoogleMaps.setOnClickListener(v -> {
            String query = (title != null && !title.isEmpty()) ? title : "travel destinations";
            String url = "https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(query);
            webView.loadUrl(url);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle back button in WebView
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
