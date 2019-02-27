package com.example.jvhe.petus.Activity;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    UserInfo user_info;
    ImageView iv_profile, iv_background;
    TextView tv_name;
    LinearLayout action_private_chat, action_call, action_video_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(this);

        tv_name = findViewById(R.id.tv_name);

        action_private_chat = findViewById(R.id.action_private_chat);
//        action_private_chat.setBackground(new ShapeDrawable(new OvalShape()));
        action_private_chat.setOnClickListener(this);
//        action_call = findViewById(R.id.action_call);
////        action_call.setBackground(new ShapeDrawable(new OvalShape()));
//        action_call.setOnClickListener(this);
        action_video_call = findViewById(R.id.action_video_call);
//        action_video_call.setBackground(new ShapeDrawable(new OvalShape()));
        action_video_call.setOnClickListener(this);


        Intent intent = getIntent();
        user_info = (UserInfo) intent.getSerializableExtra("user_info");

        tv_name.setText(user_info.getName());
        if (!user_info.getLink_profile().equals("") && !user_info.getLink_profile().equals("null"))
            Glide.with(this)
                    .load(StaticData.url + user_info.getLink_profile())
                    .apply(StaticData.requestOptions_rounded)
                    .into(iv_profile);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_profile:
                intent = new Intent(this, ProfileImageActivity.class);
                intent.putExtra("link_profile", user_info.getLink_profile());
                startActivity(intent);
                break;
            case R.id.action_private_chat:
                intent = new Intent(this, ChatRoomActivity.class);
//                intent.putExtra("user_info_target", user_info);
                intent.putExtra("is_private_or_group", "private");
                intent.putExtra("group_or_user_id", user_info.getUser_id());
                intent.putExtra("name_or_title", user_info.getName());


                intent.putExtra("starting_point", "UserProfileActivity");
                startActivity(intent);
                finish();
                break;
            //영상 통화 버튼 관련 작업
            case R.id.action_video_call:
                intent = new Intent(this, VideoCallActivity.class);
                intent.putExtra("user_info", user_info);
                intent.putExtra("is_send", true);
                startActivity(intent);
        }
    }
}
