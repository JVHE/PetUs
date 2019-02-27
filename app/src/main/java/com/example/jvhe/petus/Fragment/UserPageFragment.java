package com.example.jvhe.petus.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jvhe.petus.Activity.StarBalloonPaymentActivity;
import com.example.jvhe.petus.Adapter.PostAdapter;
import com.example.jvhe.petus.Adapter.UserRecyclerAdapter;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.PostItem;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class  UserPageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = "UserPageFragment";


    TextView tv_email, tv_name, tv_number_star_balloons;
    Button btn_friends, btn_following, btn_follower, btn_all_user, btn_search,btn_charge_star_balloon;
    LinearLayout ll_user_info, ll_search, ll_post;
    EditText et_name;
    ImageView iv_cancel, iv_profile;

    int user_id;

    InputMethodManager inputMethodManager;

    SharedPreferences sharedPreferences;


    ArrayList<PostItem> postItemArrayList;
    PostAdapter postAdapter;
    String user_name;
    RecyclerView rv_post;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ArrayList<UserInfo> userInfoArrayList;
    UserRecyclerAdapter userRecyclerAdapter;
    RecyclerView rv_user;


    private int page = 0;                           // 페이징변수. 초기 값은 0 이다.
    private final int OFFSET = 4;                  // 한 페이지마다 로드할 데이터 갯수.
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private boolean lock = false;          // 데이터 불러올때 중복안되게 하기위한 변수


    public static UserPageFragment newInstance() {
        UserPageFragment fragment = new UserPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("current_user", Context.MODE_PRIVATE);
        postItemArrayList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postItemArrayList);
        user_name = sharedPreferences.getString("name", "");
        user_id = sharedPreferences.getInt("id_db", -100);

        userInfoArrayList = new ArrayList<>();
        userRecyclerAdapter = new UserRecyclerAdapter(getContext(), userInfoArrayList);


//        HttpAsync httpAsync = new HttpAsync("", StaticData.url + "request_user.php", handler_user);
//        httpAsync.execute();

        getItem();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_page, container, false);

        Log.e(TAG, "onCreateView is called");

        sharedPreferences = getActivity().getSharedPreferences("current_user", Context.MODE_PRIVATE);

        ll_user_info = view.findViewById(R.id.ll_user_info);
        iv_cancel = view.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(this);

        iv_profile = view.findViewById(R.id.iv_profile);
        tv_email = view.findViewById(R.id.tv_email);
        tv_name = view.findViewById(R.id.tv_name);
        tv_number_star_balloons = view.findViewById(R.id.tv_number_star_balloons);

        btn_friends = view.findViewById(R.id.btn_friends);
        btn_friends.setOnClickListener(this);
        btn_following = view.findViewById(R.id.btn_following);
        btn_following.setOnClickListener(this);
        btn_follower = view.findViewById(R.id.btn_follower);
        btn_follower.setOnClickListener(this);
        btn_all_user = view.findViewById(R.id.btn_all_user);
        btn_all_user.setOnClickListener(this);
        btn_charge_star_balloon = view.findViewById(R.id.btn_charge_star_balloon);
        btn_charge_star_balloon.setOnClickListener(this);

        ll_search = view.findViewById(R.id.ll_search);
        // 검색칸 숨기기
        ll_search.setVisibility(View.GONE);

        et_name = view.findViewById(R.id.et_name);

        btn_search = view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);


        ll_post = view.findViewById(R.id.ll_post);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        // 프로그레스바 숨기기
        progressBar.setVisibility(View.GONE);

        rv_post = view.findViewById(R.id.rv_post);
        rv_post.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rv_post.setAdapter(postAdapter);
        rv_post.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (!lock) {
                        Log.e(TAG, "스크롤 최하단. 페이징 시작");
                        // 화면이 바닦에 닿을때 처리
                        // 로딩중을 알리는 프로그레스바를 보인다.
                        progressBar.setVisibility(View.VISIBLE);

                        // 다음 데이터를 불러온다.
                        getItem();
                    }

                }
            }
        });

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        rv_user = view.findViewById(R.id.rv_user);
        rv_user.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_user.setAdapter(userRecyclerAdapter);
        rv_user.setVisibility(View.GONE);

        inputMethodManager = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume is called");

        sharedPreferences = getActivity().getSharedPreferences("current_user", Context.MODE_PRIVATE);

        user_name = sharedPreferences.getString("name", "");
        user_id = sharedPreferences.getInt("id_db", -100);

        HttpAsync httpAsync = new HttpAsync("user_id="+user_id, StaticData.url+"request_number_star_balloons.php", handler_number_star_balloons);
        httpAsync.execute();

        if (!sharedPreferences.getString("link_profile", "").equals("") && !sharedPreferences.getString("link_profile", "").equals("null"))
            Glide.with(getContext())
                    .load(StaticData.url + sharedPreferences.getString("link_profile", ""))
                    .apply(StaticData.requestOptions_rounded)
                    .into(iv_profile);

        String email = "이메일: " + sharedPreferences.getString("email", "");
        tv_email.setText(email);

        String name = "닉네임: " + sharedPreferences.getString("name", "");
        tv_name.setText(name);
    }

    @SuppressLint("HandlerLeak")
    Handler handler_user = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 메세지를 받아오지 못한 경우
            if (msg.what == 0) {
                Toast.makeText(getContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            // 메세지를 받아온 경우
            else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    userInfoArrayList.clear();
                    // 지워봄
//                    userRecyclerAdapter.notifyDataSetChanged();
                    try {
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            final UserInfo userInfo = new UserInfo();
                            // 자신의 유저 정보는 유저 리스트에 보여주지 않는다.
                            if (jObject.getInt("id") == sharedPreferences.getInt("id_db", -1)) {
                                continue;
                            }
                            userInfo.setUser_id(jObject.getInt("id"));
                            userInfo.setEmail(jObject.getString("email"));
                            userInfo.setName(jObject.getString("name"));
                            userInfo.setLink_profile(jObject.getString("link_profile"));

                            if (!jObject.getString("follower").equals("null") && jObject.getString("follower").equals("" + user_id)) {
                                userInfo.setIs_following(true);
                                Log.e(TAG, "테스트33" + jObject.get("follower"));
                            } else if (jObject.getString("is_friend").equals("0")) {
                                userInfo.setIs_following(false);
                            } else {
                                userInfo.setIs_following(false);
                                Log.e(TAG, "테스트44" + jObject.get("follower"));
                            }
                            if (!jObject.getString("is_friend").equals("null")) {
                                Log.e(TAG, "테스트11" + jObject.get("is_friend"));
//                                userInfo.setIs_friend(jObject.getInt("is_friend"));
                                userInfo.setIs_friend(1);
                            } else {
                                Log.e(TAG, "테스트22" + jObject.get("is_friend"));
                                userInfo.setIs_friend(0);
                            }

                            // 프로필 링크 작업 나중에
//                            userInfo.setEmail(jObject.getString("email"));
                            userInfoArrayList.add(userInfo);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    userRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler_user_follower = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 메세지를 받아오지 못한 경우
            if (msg.what == 0) {
                Toast.makeText(getContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            // 메세지를 받아온 경우
            else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    userInfoArrayList.clear();
                    // 지워봄
//                    userRecyclerAdapter.notifyDataSetChanged();
                    try {
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            final UserInfo userInfo = new UserInfo();
                            // 자신의 유저 정보는 유저 리스트에 보여주지 않는다.
                            if (jObject.getInt("id") == sharedPreferences.getInt("id_db", -1)) {
                                continue;
                            }
                            userInfo.setUser_id(jObject.getInt("id"));
                            userInfo.setEmail(jObject.getString("email"));
                            userInfo.setName(jObject.getString("name"));
                            userInfo.setLink_profile(jObject.getString("link_profile"));

//                            if (!jObject.getString("follower").equals("null") && jObject.getString("follower").equals("" + user_id)) {
//                                userInfo.setIs_following(true);
//                                Log.e(TAG, "테스트33" + jObject.get("follower"));
//                            } else if (jObject.getString("is_friend").equals("0")) {
//                                userInfo.setIs_following(false);
//                            } else {
//                                userInfo.setIs_following(false);
//                                Log.e(TAG, "테스트44" + jObject.get("follower"));
//                            }
                            if (!jObject.getString("is_friend").equals("1")) {
                                userInfo.setIs_following(false);
                                userInfo.setIs_friend(0);
                                Log.e(TAG, "친구아님" );
                            }
                            else {

                                userInfo.setIs_following(true);
                                userInfo.setIs_friend(1);
                                Log.e(TAG, "친구임" );
                            }

                            // 프로필 링크 작업 나중에
//                            userInfo.setEmail(jObject.getString("email"));
                            userInfoArrayList.add(userInfo);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    userRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler_paging = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(data);

                        if (jsonArray.length() == 0) {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "더 이상 페이징 할 것이 없음");
                            Toast.makeText(getContext(), "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            final PostItem postItem = new PostItem();

                            postItem.setPost_id(jObject.getInt("post_id"));
                            postItem.setTitle(jObject.getString("title"));
                            postItem.setContent(jObject.getString("content"));
                            postItem.setPost_datetime(jObject.getString("post_datetime"));
                            postItem.setPost_comment_count(jObject.getInt("post_comment_count"));
                            postItem.setPost_like(jObject.getInt("post_like"));
                            postItem.setPost_dislike(jObject.getInt("post_dislike"));
                            postItem.setPath(jObject.getString("path"));
                            postItem.setName(jObject.getString("name"));

                            postItemArrayList.add(postItem);
                            Log.e(TAG, "배열 추가됨 " + postItem.getTitle());

                        }

                        page++;
                        postAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        lock = false;


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getContext(), "새로고침 결과 " + data, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "페이징 요청 결과 " + data);
                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    postItemArrayList.clear();
                    // 지워봄
                    postAdapter.notifyDataSetChanged();
                    try {

                        JSONArray jsonArray = new JSONArray(data);


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            final PostItem postItem = new PostItem();
                            postItem.setPost_id(jObject.getInt("post_id"));
                            postItem.setTitle(jObject.getString("title"));
                            postItem.setContent(jObject.getString("content"));
                            postItem.setPost_datetime(jObject.getString("post_datetime"));
                            postItem.setPost_comment_count(jObject.getInt("post_comment_count"));
                            postItem.setPost_like(jObject.getInt("post_like"));
                            postItem.setPost_dislike(jObject.getInt("post_dislike"));
                            postItem.setPath(jObject.getString("path"));
                            postItem.setName(jObject.getString("name"));

                            postItemArrayList.add(postItem);
                            Log.e(TAG, "배열 추가됨 " + postItem.getTitle());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    postAdapter.notifyDataSetChanged();
                    page = 1;
                    lock = false;
//                    Toast.makeText(getContext(), "새로고침 결과 " + data, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "새로고침 결과 " + data);
// 새로고침 완료
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler_number_star_balloons = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    String string = "별풍선: "+data+"개";
                    tv_number_star_balloons.setText(string);
                }
            }
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }


    private void getItem() {
        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 lock을 true로 설정한다.
        lock = true;

        // 페이징 코드
        HttpAsync httpAsync = new HttpAsync("page=" + page + "&user=" + user_name, StaticData.url + "post_list.php", handler_paging);
        httpAsync.execute();
    }

    @Override
    public void onRefresh() {
        // 새로고침 코드
        HttpAsync httpAsync = new HttpAsync("page=0&user=" + user_name, StaticData.url + "post_list.php", handler);
        httpAsync.execute();
    }

    @Override
    public void onClick(View v) {
        HttpAsync httpAsync;
        switch (v.getId()) {
            case R.id.btn_friends:
                btn_friends.setBackgroundResource(R.drawable.edge_clicked);
                btn_following.setBackgroundResource(R.drawable.edge);
                btn_follower.setBackgroundResource(R.drawable.edge);
                btn_all_user.setBackgroundResource(R.drawable.edge);
                ll_user_info.setVisibility(View.GONE);
                ll_post.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                rv_user.setVisibility(View.VISIBLE);
                httpAsync = new HttpAsync("user_id=" + user_id + "&type=friends", StaticData.url + "request_user.php", handler_user);
                httpAsync.execute();
                break;
            case R.id.btn_following:
                btn_friends.setBackgroundResource(R.drawable.edge);
                btn_following.setBackgroundResource(R.drawable.edge_clicked);
                btn_follower.setBackgroundResource(R.drawable.edge);
                btn_all_user.setBackgroundResource(R.drawable.edge);
                ll_user_info.setVisibility(View.GONE);
                ll_post.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                rv_user.setVisibility(View.VISIBLE);
                httpAsync = new HttpAsync("user_id=" + user_id + "&type=following", StaticData.url + "request_user.php", handler_user);
                httpAsync.execute();
                break;
            case R.id.btn_follower:
                btn_friends.setBackgroundResource(R.drawable.edge);
                btn_following.setBackgroundResource(R.drawable.edge);
                btn_follower.setBackgroundResource(R.drawable.edge_clicked);
                btn_all_user.setBackgroundResource(R.drawable.edge);
                ll_user_info.setVisibility(View.GONE);
                ll_post.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                rv_user.setVisibility(View.VISIBLE);
                httpAsync = new HttpAsync("user_id=" + user_id + "&type=follower", StaticData.url + "request_user.php", handler_user_follower);
                httpAsync.execute();
                break;
            case R.id.btn_all_user:
                btn_friends.setBackgroundResource(R.drawable.edge);
                btn_following.setBackgroundResource(R.drawable.edge);
                btn_follower.setBackgroundResource(R.drawable.edge);
                btn_all_user.setBackgroundResource(R.drawable.edge_clicked);
                ll_user_info.setVisibility(View.GONE);
                ll_post.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                rv_user.setVisibility(View.VISIBLE);
                httpAsync = new HttpAsync("user_id=" + user_id + "&type=all", StaticData.url + "request_user.php", handler_user);
                httpAsync.execute();
                break;
            case R.id.iv_cancel:
                et_name.setText("");
                btn_friends.setBackgroundResource(R.drawable.edge);
                btn_following.setBackgroundResource(R.drawable.edge);
                btn_follower.setBackgroundResource(R.drawable.edge);
                btn_all_user.setBackgroundResource(R.drawable.edge);
                ll_user_info.setVisibility(View.VISIBLE);
                ll_post.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
                rv_user.setVisibility(View.GONE);
                inputMethodManager.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
                break;
            case R.id.btn_charge_star_balloon:
                Intent intent = new Intent(getContext(), StarBalloonPaymentActivity.class);
                startActivity(intent);
                break;
        }
    }
}
