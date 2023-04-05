package com.example.chatchit.fragment.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.chatchit.R;

import java.io.IOException;
import java.io.InputStream;

public class TeamsActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        webView = findViewById(R.id.teamsWebView);
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccess(true);
        webView.loadUrl("file:///android_asset/dsthanhvien.html");
    }
}