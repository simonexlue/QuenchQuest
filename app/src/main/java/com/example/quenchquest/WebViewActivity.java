package com.example.quenchquest;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());

        if (url != null && !url.isEmpty()) {
            // Load the URL into the WebView
            webView.loadUrl(url);
        } else {
            // Handle the case when URL is not provided
            // For example, display an error message
            webView.loadData("Error: URL not provided", "text/html", "UTF-8");
        }
    }
}