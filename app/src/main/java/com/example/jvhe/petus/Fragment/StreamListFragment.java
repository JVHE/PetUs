package com.example.jvhe.petus.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jvhe.petus.Activity.StreamRoomActivity;
import com.example.jvhe.petus.Adapter.StreamRecyclerAdapter;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.StreamRoomItem;
import com.example.jvhe.petus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


public class StreamListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "StreamListFragment";

    Button btn_start_stream;
    RecyclerView rv_stream;
    StreamRecyclerAdapter streamRecyclerAdapter;
    ArrayList<StreamRoomItem> streamRoomItemArrayList;

    // 새로고침용
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        streamRoomItemArrayList = new ArrayList<>();
        streamRecyclerAdapter = new StreamRecyclerAdapter(getContext(), streamRoomItemArrayList);

    }

    public static StreamListFragment newInstance() {
        StreamListFragment fragment = new StreamListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_stream, container, false);

        btn_start_stream = view.findViewById(R.id.btn_start_stream);
        rv_stream = view.findViewById(R.id.rv_stream);

        rv_stream.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_stream.setAdapter(streamRecyclerAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        btn_start_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 컨텍스트 집어넣을 때 getContext가 느낌이 쎄하다.
                AlertDialog.Builder ad = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                Context context = getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(10, 0, 10, 0);

// Add a TextView here for the "Title" label, as noted in the comments
                final EditText et_title = new EditText(context);
                layout.addView(et_title); // Notice this is an add method

                final TextView tv_desc = new TextView(context);
                tv_desc.setText("   설명");
                tv_desc.setTextSize(15);
                layout.addView(tv_desc);

                // Add another TextView here for the "Description" label
                final EditText et_desc = new EditText(context);
                layout.addView(et_desc); // Another add method

                final TextView tv_reaction = new TextView(context);
                tv_reaction.setText("   별풍선 반응 설정\n    ㅇㅇㅇ님 별풍선 **개");
                tv_reaction.setTextSize(15);
                layout.addView(tv_reaction);
                // Add another TextView here for the "Description" label
                final EditText et_reaction = new EditText(context);
                et_reaction.setText("를 선물하셨습니다.");
                layout.addView(et_reaction); // Another add method
                ad.setView(layout); // Again this is a set method, not add

                ad.setTitle("방송 제목");       // 제목 설정

                // 확인 버튼 설정
                ad.setPositiveButton("시작하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "Yes Btn Click");

                        // Text 값 받아서 로그 남기기
                        String title = et_title.getText().toString();
                        if (title.equals("")) {
                            Toast.makeText(getContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        } else {

                            String desc = et_desc.getText().toString();
                            String reaction = et_reaction.getText().toString();
                            Log.e(TAG, "title: " + title + " desc: " + desc+ " reaction: " + reaction);

                            dialog.dismiss();     //닫기
                            // Event
                            Intent intent = new Intent(getContext(), StreamRoomActivity.class);
                            intent.putExtra("title", title);
                            intent.putExtra("desc", desc);
                            intent.putExtra("reaction", reaction);
                            intent.putExtra("is_streamer", true);
                            startActivity(intent);
                        }
                    }
                });
                // 취소 버튼 설정
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "No Btn Click");
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                ad.show();// 창 띄우기

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

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
                    streamRoomItemArrayList.clear();//지워봄
//                    postAdapter.notifyDataSetChanged();
                    try {

                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            final StreamRoomItem streamRoomItem = new StreamRoomItem();
                            // 필요한건 나중에 더 추가할 것
                            streamRoomItem.setTitle(jObject.getString("title"));
                            streamRoomItem.setDesc(jObject.getString("description"));
                            streamRoomItem.setRoom_name(jObject.getString("room_name"));
                            streamRoomItem.setReaction(jObject.getString("reaction"));
                            streamRoomItem.getStreamer_info().setName(jObject.getString("name"));
                            streamRoomItem.getStreamer_info().setUser_id(jObject.getInt("id"));
                            streamRoomItem.getStreamer_info().setLink_profile(jObject.getString("link_profile"));

//                            streamRoomItem.setPost_like(jObject.getInt("post_like"));
//                            streamRoomItem.setPost_dislike(jObject.getInt("post_dislike"));
//                            streamRoomItem.setPath(jObject.getString("path"));
//                            streamRoomItem.setName(jObject.getString("name"));
//                            streamRoomItem.setPost_id(jObject.getInt("post_id"));
                            streamRoomItemArrayList.add(streamRoomItem);
                            Log.e(TAG, "배열 추가됨 " + streamRoomItem.getTitle());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    streamRecyclerAdapter.notifyDataSetChanged();
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    public void onRefresh() {
        HttpAsync httpAsync = new HttpAsync("", StaticData.url + "stream_list.php", handler);
        httpAsync.execute();
    }
}
