package com.example.jvhe.petus.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jvhe.petus.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final ImageView iv_loading_gif = findViewById(R.id.iv_loading_gif);
//        SimpleTarget gifImage = new GlideDrawableImageViewTarget(iv_loading_gif);
//        Glide.with(this).load(R.drawable.loading).into(gifImage);

//        Glide.with(this)
//                .load(R.drawable.loading)
//                .apply( new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
//                .into(
//                        new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                        iv_loading_gif.setImageDrawable(resource);
//                    }
//                });
        Glide.with(this)
                .load(R.drawable.loading)
                .into(iv_loading_gif);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, 1000);// 1.5 초
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.loadfadein, R.anim.loadfadeout);

    }
}
