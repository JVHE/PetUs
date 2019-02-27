package com.example.jvhe.petus.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

public class ProfileImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);

        // 인텐트에서 이미지 정보를 받아온다.
        Intent intent = getIntent();

        ImageView iv_profile = findViewById(R.id.iv_profile);

        Glide.with(this)
                .load(StaticData.url+intent.getStringExtra("link_profile"))
                .apply(StaticData.requestOptions)
                .into(iv_profile);

    }
}
