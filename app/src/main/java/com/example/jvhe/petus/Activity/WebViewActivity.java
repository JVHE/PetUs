package com.example.jvhe.petus.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jvhe.petus.R;

public class WebViewActivity extends AppCompatActivity {

    WebView webview;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webview = findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // 인텐는 샵검색인지 아닌지에 대한 변수를 가지고 샵검색인지 아니면 url을 검색한건지 판단한다.
        Intent intent = getIntent();
        if (intent.getBooleanExtra("is_sharp_search", false)) {
            webview.loadUrl("https://m.search.naver.com/search.naver?query=" + intent.getStringExtra("data"));
        }
    }
}
