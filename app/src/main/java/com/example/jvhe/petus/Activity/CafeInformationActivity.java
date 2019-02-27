package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jvhe.petus.Adapter.CafeImagePagerAdapter;
import com.example.jvhe.petus.Adapter.SocialReviewAdapter;
import com.example.jvhe.petus.Class.CafeItem;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.R;
import com.example.jvhe.petus.Class.ReviewItem;
import com.example.jvhe.petus.Class.StaticData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CafeInformationActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "CafeInformationActivity";

    TextView tv_cafe_name, tv_cafe_loc, tv_cafe_distance;
    ViewPager vp_cafe_image;
    CafeImagePagerAdapter cafeImagePagerAdapter;

    ArrayList<String> arrayList_urls = new ArrayList<>();
    ArrayList<Bitmap> arrayList_image = new ArrayList<>();
    Integer size = -1;

    ArrayList<ReviewItem> arrayList_review = new ArrayList<>();
    RecyclerView rcv_social_review;
    SocialReviewAdapter socialReviewAdapter;
    NestedScrollView scrollView;

    CafeItem cafeItem;

    //지도 관련
    private GoogleMap map;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_information);


        tv_cafe_distance = findViewById(R.id.tv_cafe_distance);
        tv_cafe_name = findViewById(R.id.tv_cafe_name);
        tv_cafe_loc = findViewById(R.id.tv_cafe_loc);
        vp_cafe_image = findViewById(R.id.vp_cafe_image);

        findViewById(R.id.rcv_social_review).setFocusable(false);
        findViewById(R.id.temp).requestFocus();

        // 인텐트 가져와서 처리
        Intent intent = getIntent();
        cafeItem = (CafeItem) intent.getSerializableExtra("cafe");
        tv_cafe_name.setText(cafeItem.getName());
        tv_cafe_loc.setText(cafeItem.getLocation());
        double dist = getDistance(cafeItem.getLatitude(), cafeItem.getLongitude(), StaticData.my_lat, StaticData.my_lng);
        dist /= 1000;
        tv_cafe_distance.setText(Double.parseDouble(String.format("%.2f", dist)) + "km");

        if (!cafeItem.getImage_url().equals("")) {
            // 액티비티가 열릴 때 구글에서 이미지 요청을 한다. 1번 이미지는 썸네일을 차용. 이미지는 뷰페이저에 약 4~5개정도를 더 넣어 보여준다.
            // place_id로 구글에 json http 요청 -> 결과값 받아옴 json -> url에 하나하나 다 집어넣음 -> 뷰페이저에서 글라이드로 이미지 뿌려주기
            String data = "";
            String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + cafeItem.getPlace_id() + "&fields=photo,website,formatted_phone_number&key=..&language=ko";
            HttpAsync httpasync = new HttpAsync(data, url, handler);
            httpasync.execute();

            arrayList_image.add((Bitmap) intent.getExtras().get("image"));
            cafeImagePagerAdapter = new CafeImagePagerAdapter(getApplicationContext(), arrayList_image);
            vp_cafe_image.setAdapter(cafeImagePagerAdapter);
        } else
            vp_cafe_image.setBackgroundResource(R.drawable.dog_cafe_6);

        // 액션바 감추기 말고 이번엔 제목 넣자
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(cafeItem.getTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.hide();
        }
        // 리뷰 가져오기
        String data = "";
        String url = StaticData.url + "blog_naver_request.php?name=" + cafeItem.getTitle();
        HttpAsync httpAsync = new HttpAsync(data, url, handler_blog);
        httpAsync.execute();

        //리스트뷰 관련 작업들
        rcv_social_review = findViewById(R.id.rcv_social_review);
        socialReviewAdapter = new SocialReviewAdapter(this, arrayList_review);
        rcv_social_review.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rcv_social_review.setAdapter(socialReviewAdapter);
        scrollView = findViewById(R.id.scrollView);
        scrollView.setNestedScrollingEnabled(true);
//        lv_social_review.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                // TODO Auto-generated method stub
////                scrollView.requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.action_wayfinding).setOnClickListener(this);
        findViewById(R.id.action_navigate).setOnClickListener(this);
        findViewById(R.id.action_call).setOnClickListener(this);
        findViewById(R.id.action_copy_address).setOnClickListener(this);
    }

    // 뒤로가기 버튼 활성화
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

    // http통신 관련 처리하는 핸들러
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 메세지를 받아오지 못한 경우
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다. handler", Toast.LENGTH_SHORT).show();
            }
            // 메세지를 받아온 경우
            else if (msg.what == 1) {
                JSONObject jsonObject;
                String status = "";
                try {
                    jsonObject = new JSONObject(msg.getData().getString("data_arr"));
                    status = jsonObject.getString("status");
                    if (status.equals("OK")) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONArray photos = result.getJSONArray("photos");
                        Log.e(TAG, "photo size: " + photos.length());
                        cafeImagePagerAdapter.size = photos.length();
                        cafeImagePagerAdapter.notifyDataSetChanged();
                        cafeImagePagerAdapter.chk(photos.length());
                        for (int i = 1; i < photos.length(); i++) {
                            JSONObject jObject = photos.getJSONObject(i);
                            arrayList_urls.add(jObject.getString("photo_reference"));
                            String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=300&photoreference=" + jObject.getString("photo_reference") + "&key=..";
                            Glide.with(getApplicationContext()).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    arrayList_image.add(resource);
                                    cafeImagePagerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    // http통신 관련 처리하는 핸들러
    @SuppressLint("HandlerLeak")
    private Handler handler_blog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 메세지를 받아오지 못한 경우
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다 handler_blog", Toast.LENGTH_SHORT).show();
            }
            // 메세지를 받아온 경우
            else if (msg.what == 1) {
                JSONObject jsonObject;
//                String status = "";
                try {
                    jsonObject = new JSONObject(msg.getData().getString("data_arr"));
                    // 데이터 처리 순서
                    // 결과값 받아와서 링크 하나씩 정리 -> 소셜리뷰 리스트뷰에 아이템 하나하나 추가

                    if (jsonObject.has("items")) {
                        JSONArray items = jsonObject.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject jObject = items.getJSONObject(i);
                            Log.e(TAG, jObject.toString() + "\n---------------------------------------------------------------------------------");
                            String title = jObject.getString("title")
                                    .replace("<b>", "").replace("</b>", "")
                                    .replace("&quot;", "\"").replace("&lt;", "<")
                                    .replace("&gt;", ">");
                            String desc = jObject.getString("description")
                                    .replace("<b>", "").replace("</b>", "")
                                    .replace("&quot;", "\"").replace("&lt;", "<")
                                    .replace("&gt;", ">");
                            ReviewItem reviewItem = new ReviewItem(title, desc, jObject.getString("link"));
                            arrayList_review.add(reviewItem);

                            socialReviewAdapter.notifyItemChanged(arrayList_review.size() - 1);
                        }
                    }
//
//
//                        JSONObject result = jsonObject.getJSONObject("result");
//                        JSONArray photos = result.getJSONArray("photos");
//                        cafeImagePagerAdapter.size = photos.length();
//                        cafeImagePagerAdapter.notifyDataSetChanged();
//                        for (int i = 0; i < photos.length(); i++) {
//                            JSONObject jObject = photos.getJSONObject(i);
//                            arrayList_urls.add(jObject.getString("photo_reference"));
//                            String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=300&photoreference=" + jObject.getString("photo_reference") + "&key=..";
//                            Glide.with(getApplicationContext()).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                                    arrayList_image.add(resource);
//                                    cafeImagePagerAdapter.notifyDataSetChanged();
//                                }
//                            });
//
//
//                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                assert data_arr != null;
//                if (data_arr.contains("ZERO_RESULTS")) {
//                    Toast.makeText(getContext(), "(구글)검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(data_arr);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }


//                if (data_arr.contains("!!fail!!")) {
////                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(data_arr);
//                        SharedPreferences pref = getSharedPreferences("current_user", 0);
//                        SharedPreferences.Editor edit = pref.edit();
//                        edit.putBoolean("is_login", true);
//                        edit.putInt("id_db", Integer.parseInt(obj.getString("id")));
//                        edit.putString("email", obj.getString("email"));
//                        edit.putString("name", obj.getString("name"));
//                        edit.putString("password", obj.getString("password"));
//                        edit.commit();
//                        updateUI();
//                        Toast.makeText(getApplicationContext(), "결과 " + data_arr, Toast.LENGTH_SHORT).show();
//                        finish();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    };

    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng cafe_location = new LatLng(cafeItem.getLatitude(), cafeItem.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(cafe_location)
                .title(cafeItem.getName())
                .snippet(cafeItem.getLocation());
        googleMap.addMarker(markerOptions).showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cafe_location, 15));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_wayfinding:
                Toast.makeText(getApplicationContext(), "길찾기", Toast.LENGTH_SHORT).show();
//                Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr="++"&daddr=도착지주소&hl=ko");
//
//                Intent it = new Intent(Intent.ACTION_VIEW,URI);
//
//                startActivity(it);
                break;
            case R.id.action_navigate:
                Toast.makeText(getApplicationContext(), "네비게이션", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_call:
                Toast.makeText(getApplicationContext(), "전화걸기", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_copy_address:
                Toast.makeText(getApplicationContext(), "주소 복사", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
