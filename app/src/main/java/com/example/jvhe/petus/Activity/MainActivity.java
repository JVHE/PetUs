package com.example.jvhe.petus.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jvhe.petus.Adapter.MyFragmentPagerAdapter;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Fragment.CafeListFragment;
import com.example.jvhe.petus.Fragment.FirstFragment;
import com.example.jvhe.petus.Fragment.StreamListFragment;
import com.example.jvhe.petus.Fragment.UserPageFragment;
import com.example.jvhe.petus.R;
import com.example.jvhe.petus.Fragment.ChatListFragment;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int MAP_SELECT = 7001;


    private ViewPager mViewPager;
    android.support.v7.app.ActionBar bar;
    private android.support.v4.app.FragmentManager fm;
    private ArrayList<Fragment> fList;

    // 유저 리사이클러 뷰에서 호출됨
    public BottomNavigationView navigation;

    private MenuItem prevMenuItem;


    boolean is_login = false;


    // 로그인 정보 확인
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.e(TAG, "onCreate");



        sharedPreferences = getSharedPreferences("current_user", MODE_PRIVATE);

//        // 스플래시 액티비티를 실행한다.
        startActivity(new Intent(getApplicationContext(), SplashActivity.class));

        // 스와이프할 뷰페이저를 정의
        mViewPager = findViewById(R.id.pager);

        // 프라그먼트 매니져 객체 정의
        fm = getSupportFragmentManager();

        // 하단 네비게이션바 연결
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // 액션바 객체 정의
        bar = getSupportActionBar();

        // 액션바 속성 정의
        assert bar != null;
        bar.setDisplayShowTitleEnabled(true);   // 액션바 노출 유무
        bar.setTitle("PetUs");   // 액션바 타이틀 라벨

        // 각 탭에 들어갈 프라그먼트 생성 및 추가
        fList = new ArrayList<Fragment>();
        fList.add(FirstFragment.newInstance());
        fList.add(ChatListFragment.newInstance());
        fList.add(StreamListFragment.newInstance());
        fList.add(CafeListFragment.newInstance());
        fList.add(UserPageFragment.newInstance());

        // 스와이프로 탭간 이동할 뷰페이저의 리스너 설정
        mViewPager.addOnPageChangeListener(viewPagerListener);

        // 뷰페이져의 아답터 생성 및 연결
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(fm, fList);
        mViewPager.setAdapter(adapter);


//        // 토큰 지금은 잠시 주석처리
//        FirebaseInstanceId.getInstance().getToken();
//        if (FirebaseInstanceId.getInstance().getToken() != null) {
//            // 받아온 토큰을 현재 로그인하고 있는 유저의 계정에 집어넣어 준다.
//            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//            Log.d(TAG, "token = " + refreshedToken);
//            sendRegistrationToServer(refreshedToken);
//        }

        //어플 킬 때 토큰 리프레시 한 번 해주자.
        // 토큰을 새로 만들어 준다.
        // 받아온 토큰을 현재 로그인하고 있는 유저의 계정에 집어넣어 준다.
        sharedPreferences = getSharedPreferences("current_user", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
        editor.putString("device_token", refreshedToken);
        editor.apply();

        // 알림에 의해 열린 인텐트인지 확인
        Intent intent = getIntent();
        if (intent.getStringExtra("starting_point") != null && intent.getStringExtra("starting_point").equals("notification")) {
            Log.e(TAG, "노티에서 실행");
            Log.e(TAG, "is_private_or_group: "+intent.getStringExtra("is_private_or_group"));
            Log.e(TAG, "group_or_user_id: "+intent.getIntExtra("group_or_user_id", -102));
            Log.e(TAG, "name_or_title: "+intent.getStringExtra("name_or_title"));
            Intent startIntent = new Intent(this, ChatRoomActivity.class);
            startIntent.putExtra("is_private_or_group", intent.getStringExtra("is_private_or_group"));
            startIntent.putExtra("group_or_user_id", intent.getIntExtra("group_or_user_id", -102));
            startIntent.putExtra("name_or_title", intent.getStringExtra("name_or_title"));
            startActivity(startIntent);
        }
    }

    // 디바이스에서 생성된 토큰 정보를 서버에 전송한다.
    private void sendRegistrationToServer(String token) {
        HttpAsync httpAsync = new HttpAsync("user_id=" + sharedPreferences.getInt("id_db", -100) + "&token=" + token, StaticData.url + "save_token.php");
        httpAsync.execute();
    }

//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e(TAG, "onPrepareOptionsMenu");

        is_login = sharedPreferences.getBoolean("is_login", false);

        // 업로드 버튼 아이템 변수로 가져오기
        MenuItem action_upload = menu.findItem(R.id.action_upload);
        // 로그인 된 경우
        if (is_login) {
            // 로그인 상태가 아닐 경우 비디오 업로드 버튼을 숨긴다.
            action_upload.setVisible(true);
        }
        // 로그인되지 않은 경우
        else {
            // 로그인 상태가 아닐 경우 비디오 업로드 버튼을 숨긴다.
            action_upload.setVisible(false);
        }
//        this.invalidateOptionsMenu();

        return super.onPrepareOptionsMenu(menu);
    }

    // 액션바 인플레이트 시키기.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        Log.e(TAG, "onCreateOptionsMenu");
        return true;
    }


    // 액션바 메뉴 선택에 따른 반응 리스너
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 업로드 버튼 눌렸을 경우
            case R.id.action_upload:
                sharedPreferences = getSharedPreferences("current_user", MODE_PRIVATE);

                if (!is_login) {
                    Toast.makeText(getApplicationContext(), "로그인을 하시면 동영상을 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();
//                    startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_CODE_LOGIN);
                } else {
                    //Toast.makeText(getApplicationContext(), "업로드 버튼 눌림!", Toast.LENGTH_SHORT).show();
                    // 업로드 버튼을 누를 경우 업로드 액티비티로 넘어간다.
                    // 업로드 액티비티에선 화면을 녹화 후 특정 폴더에 자동 저장, 동영상 제목, 내용 등을 기록할 수 있다.
//                    startActivityForResult(new Intent(getApplicationContext(), UploadActivity.class), REQUEST_CODE_UPLOAD);
//                    startActivityForResult(new Intent(getApplicationContext(), UploadActivity.class), 10011);
                    startActivityForResult(new Intent(getApplicationContext(), UploadActivity.class), 10011);
                }
                return true;
            // 검색 버튼 눌렸을 경우
            case R.id.action_search:
//                Toast.makeText(getApplicationContext(), "검색 버튼 눌림!", Toast.LENGTH_SHORT).show();
//                // 검색을 누를 경우 검색 액티비티로 넘어간다.
//                // 검색 액티비티에선 지난 검색 기록들을 볼 수 있다.
//                // 검색 기록에서 결과를 반환하면 검색 결과를 메인 액티비티에 띄워준다.
//                startActivityForResult(new Intent(getApplicationContext(), Search.class), 1);

                return true;
            // 회원 버튼 눌렀을 경우
            case R.id.action_user:
//                Toast.makeText(getApplicationContext(), "로그인 버튼 눌림!", Toast.LENGTH_SHORT).show();
//                // 지금은 로그인 액티비티로 이동하도록 처리한다.
//                // 추후에 SP를 이용한 로그인 상태 확인으로 로그인 액티비티로 이동하는 반응과, 개인정보 액티비티로 이동하는 반응을 나눌 것이다.
//                // 우선 startActivityForResult의 리퀘스트 코드 1은 임시로 집어넣고 추후에 의미를 부여할 것.
//                if (is_login) {
//                    startActivityForResult(new Intent(getApplicationContext(), UserInfoActivity.class), REQUEST_CODE_LOGOUT);
//                }
//                // 로그인 되어있지 않은 경우
//                else if (!is_login) {
//                    startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_CODE_LOGIN);
//                }
                startActivity(new Intent(this, LoginActivity.class));
                return true;

            //여긴 이 어플에서 안쓰인다.
//            // 옵션 버튼 눌렀을 경우
//            case R.id.action_option:
//                Toast.makeText(getApplicationContext(), "옵션 버튼 눌림", Toast.LENGTH_SHORT).show();
//                startActivityForResult(new Intent(getApplicationContext(), OptionActivity.class), REQUEST_CODE_OPTION);
//
//                // default 옵션
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");

        // 로그인 상태 확인
        // 유저 이름이 ""가 아닐 경우 로그인 상태라고 간주한다.
        sharedPreferences = getSharedPreferences("current_user", MODE_PRIVATE);
        if (!sharedPreferences.getString("email", "").equals("")) {
            is_login = true;
        } else {
            is_login = false;
        }
        invalidateOptionsMenu();


    }


    ViewPager.SimpleOnPageChangeListener viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // 페이지가 선택되었을 때, 반응 처리
            super.onPageSelected(position);
            if (prevMenuItem != null) {
                prevMenuItem.setChecked(false);
            } else {
                navigation.getMenu().getItem(0).setChecked(false);
            }
            navigation.getMenu().getItem(position).setChecked(true);
            prevMenuItem = navigation.getMenu().getItem(position);

        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_chat:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_streaming:
                    mViewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_map:
                    mViewPager.setCurrentItem(3);
                    return true;
                case R.id.navigation_notifications:
                    mViewPager.setCurrentItem(4);
                    return true;
            }

//            mViewPager.setCurrentItem(item.getItemId());
//            Toast.makeText(getApplicationContext(), item.getGroupId() + "번째 아이템 클릭", Toast.LENGTH_SHORT).show();
//            return true;
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data_arr);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == MAP_SELECT) {
//
//            }
//        }


//        int request = requestCode & 0xffff;
//
//        // 프래그먼트에서 결과값을 받아야 한다면 아래와 같이...
//        Fragment fragment =  fList.get(2);
//        fragment.onActivityResult(request, resultCode, data_arr);

    }

    //뒤로가기 버튼을 두번 연속으로 눌러야 종료되게끔 하는 메소드
    private long time = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }

}
