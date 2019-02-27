package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.PostItem;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PostActivity";

    ActionBar bar;
    PostItem postItem;

    TextView tv_title, tv_name, tv_follow, tv_desc;
    ImageView iv_profile;

    LinearLayout ll_media;
    LayoutInflater inflater;


    MediaController mediaController;
    WindowManager windowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ll_media = findViewById(R.id.ll_media);
        inflater = LayoutInflater.from(getApplicationContext());
        mediaController = new MediaController(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Intent intent = getIntent();

        postItem = new PostItem();
        if ((PostItem) intent.getSerializableExtra("post") != null) {
            postItem = (PostItem) intent.getSerializableExtra("post");

            Log.e(TAG, "널값 아님");
        } else {
            postItem.setPost_id(intent.getIntExtra("post_id", -1));
            postItem.setTitle(intent.getStringExtra("title"));
            postItem.setContent(intent.getStringExtra("content"));
            postItem.setPost_datetime(intent.getStringExtra("post_datetime"));
            postItem.setPost_comment_count(intent.getIntExtra("post_comment_count", -1));
            postItem.setPost_like(intent.getIntExtra("post_like", -1));
            postItem.setPost_dislike(intent.getIntExtra("post_dislike", -1));
            postItem.setPath(intent.getStringExtra("path"));
            postItem.setName(intent.getStringExtra("name"));
            postItem.setLink_profile(intent.getStringExtra("link_profile"));
        }

        // 액션바 객체 정의
        bar = getSupportActionBar();

        // 액션바 속성 정의
        assert bar != null;
        bar.setDisplayShowTitleEnabled(true);   // 액션바 노출 유무
        bar.setTitle(postItem.getName() + "님의 게시물");   // 액션바 타이틀 라벨
        bar.setDisplayHomeAsUpEnabled(true);    // 뒤로가기 버튼

        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(postItem.getTitle());
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(postItem.getName());
        tv_follow = findViewById(R.id.tv_follow);
        // 리스너 설정 필요
        tv_follow.setOnClickListener(this);
        tv_desc = findViewById(R.id.tv_desc);
        tv_desc.setText(postItem.getContent());

        iv_profile = findViewById(R.id.iv_profile);
        if (!postItem.getLink_profile().equals("") && !postItem.getLink_profile().equals("null"))
        Glide.with(this)
                .setDefaultRequestOptions(StaticData.requestOptions)
                .load(StaticData.url + postItem.getLink_profile())
                .into(iv_profile);
        else iv_profile.setImageResource(R.drawable.profile);

        String data = "post_id=" + postItem.getPost_id();
        HttpAsync httpAsync = new HttpAsync(data, StaticData.url + "post_image_load.php", handler);
        httpAsync.execute();

    }

    @Override
    public void onClick(View v) {

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String url = StaticData.url + jsonArray.getString(i);
                            Log.e(TAG, "url: "+url);
                            if (url.contains(".jpg") |
                                    url.contains(".jpeg") |
                                    url.contains(".png") |
                                    url.contains(".gif")) {
                                View inflatedView = inflater.inflate(R.layout.post_image, ll_media, false);
                                ImageView iv = inflatedView.findViewById(R.id.iv);
                                Glide.with(getApplicationContext())
                                        .load(url)
                                        .into(iv);
                                ll_media.addView(inflatedView);
                            } else {

                                View inflatedView = inflater.inflate(R.layout.post_video, ll_media, false);
                                final VideoView vv = inflatedView.findViewById(R.id.vv);
                                ll_media.addView(inflatedView);
                                vv.setVideoPath(url);
                                vv.setMediaController(mediaController);
//                                vv.start();
// 비디오 썸네일 설정
                                vv.setBackground(
                                        new BitmapDrawable(retriveVideoFrameFromVideo(url)
                                        ));
                                // 다른 썸네일 지정 방법인데 이건 시작점 기준으로 썸네일 만드는 것이다.
//                        video_view.seekTo(100);


                                // 썸네일 설정 이후
                                vv.setZOrderOnTop(true);
//                                vv.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mediaController.show(0);
//                                        vv.pause();
//                                    }
//                                }, 100);
                                mediaController.setAnchorView(vv);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
//                    Log.e(TAG, "새로고침 결과 " + data);

                }
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
