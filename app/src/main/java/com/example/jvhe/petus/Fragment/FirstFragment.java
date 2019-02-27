package com.example.jvhe.petus.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jvhe.petus.Adapter.PostAdapter;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.PostItem;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FirstFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FirstFragment";

    //    Button btn_refresh;
    PostAdapter postAdapter;
    ArrayList<PostItem> postItemArrayList;
    RecyclerView rv_post;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int page = 0;                           // 페이징변수. 초기 값은 0 이다.
    private final int OFFSET = 2;                  // 한 페이지마다 로드할 데이터 갯수.
    private ProgressBar progressBar;                // 데이터 로딩중을 표시할 프로그레스바
    private boolean lock = false;          // 데이터 불러올때 중복안되게 하기위한 변수

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postItemArrayList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postItemArrayList);

        // 임시로 지움
//        getItem();


//        HttpAsync httpAsync = new HttpAsync("page=0", StaticData.url + "post_list.php", handler);
//        httpAsync.execute();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        // 프로그레스바 숨기기
        progressBar.setVisibility(View.GONE);

        rv_post = view.findViewById(R.id.rv_post);
        rv_post.setLayoutManager(new LinearLayoutManager(getContext()));
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


        return view;
    }

    private void getItem() {
        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 lock을 true로 설정한다.
        lock = true;

        // 페이징 코드
        HttpAsync httpAsync = new HttpAsync("page=" + page, StaticData.url + "post_list.php", handler_paging);
        httpAsync.execute();
    }

    @Override
    public void onRefresh() {
        // 새로고침 코드
        HttpAsync httpAsync = new HttpAsync("page=0", StaticData.url + "post_list.php", handler);
        httpAsync.execute();
    }

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
                            postItem.setLink_profile(jObject.getString("link_profile"));

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
                    postItemArrayList.clear();//지워봄
//                    postAdapter.notifyDataSetChanged();
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
                            postItem.setLink_profile(jObject.getString("link_profile"));

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

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        return fragment;
    }
}
