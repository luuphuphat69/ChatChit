package com.example.chatchit.login_signup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.chatchit.R;

public class NameRuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_rule);

        WebView webViewNameRule = findViewById(R.id.webViewNameRule);
        WebSettings settings = webViewNameRule.getSettings();
        settings.setAllowFileAccess(true);
        webViewNameRule.loadUrl("file:///android_asset/rule.html");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  true;
    }
}