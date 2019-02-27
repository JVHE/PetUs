package com.example.jvhe.petus.KaKaoPay;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

public class KakaoActivity extends Activity {

    private WebView mainWebView;
    private final String APP_SCHEME = "petus://";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao);
        sharedPreferences = getSharedPreferences("current_user", MODE_PRIVATE);

        mainWebView = findViewById(R.id.mainWebView);
//        mainWebView.setWebViewClient(new KakaoWebViewClient(this));
        mainWebView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = mainWebView.getSettings();
        settings.setJavaScriptEnabled(true);

//        mainWebView.loadUrl("http://www.iamport.kr/demo");
        Intent intent = getIntent();
        int count = intent.getIntExtra("count", 10);

        mainWebView.loadUrl(StaticData.url + "kakaopay_test2.php?count=" + count + "&user_id=" + sharedPreferences.getInt("id_db", -100));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null) {
            Uri intentData = intent.getData();

            if (intentData != null) {
                //카카오페이 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();

                if (url.startsWith(APP_SCHEME)) {
                    String path = url.substring(APP_SCHEME.length());
                    if ("process".equalsIgnoreCase(path)) {
                        mainWebView.loadUrl("javascript:IMP.communicate({result:'process'})");
                    } else {
                        mainWebView.loadUrl("javascript:IMP.communicate({result:'cancel'})");
                    }
                }
            }
        }
//        System.out.println("쒸~벌");
//        mainWebView.loadUrl("javascript:IMP.communicate()");
    }
}