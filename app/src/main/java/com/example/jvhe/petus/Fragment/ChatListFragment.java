package com.example.jvhe.petus.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.jvhe.petus.Activity.ChatRoomActivity;
import com.example.jvhe.petus.Activity.MakeChatRoomActivity;
import com.example.jvhe.petus.Adapter.ChatRoomRecyclerAdapter;
import com.example.jvhe.petus.Class.ChatItem;
import com.example.jvhe.petus.Class.ChatRoomItem;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class ChatListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ChatListFragment";
    RecyclerView rv_chat_list;
    ChatRoomRecyclerAdapter chatRoomRecyclerAdapter;
    ArrayList<ChatRoomItem> chatListArrayList;
    LinearLayoutManager linearLayoutManager;

    int user_id;

    HttpAsync httpAsync;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;


    public static ChatListFragment newInstance() {
        ChatListFragment fragment = new ChatListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatListArrayList = new ArrayList<>();
        chatRoomRecyclerAdapter = new ChatRoomRecyclerAdapter(getContext(), chatListArrayList);

        user_id = getContext().getSharedPreferences("current_user", MODE_PRIVATE).getInt("id_db", -100);

    }

    @Override
    public void onResume() {
        super.onResume();
        // 방목록 불러오기
        // 채팅 기록 불러오기
        httpAsync = new HttpAsync(
                "user_id=" + user_id
                , StaticData.url + "request_chat_room.php"
                , handler_chat_room);
        httpAsync.execute();
    }

    @SuppressLint("HandlerLeak")
    Handler handler_chat_room = new Handler() {
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
                Log.e(TAG, data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    chatListArrayList.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
//                            final UserInfo userInfo = new UserInfo();

                            ChatRoomItem chatRoomItem = new ChatRoomItem();

                            chatRoomItem.setIs_private_or_group(jObject.getInt("is_private_or_group"));
                            Log.e(TAG, "쿼리 요청 결과 \n" + jObject);

                            if (jObject.getInt("is_private_or_group") == 1) {   // 개인채팅인 경우
                                chatRoomItem.setName_or_title(jObject.getString("name"));
                                chatRoomItem.setLink_profile(jObject.getString("link_profile"));
                                chatRoomItem.setGroup_or_user_id(jObject.getInt("group_or_user_id"));


//                                UserInfo userInfo = new UserInfo();
//                                userInfo.setUser_id(jObject.getInt("id"));
//                                // 임시로 지움
////                            userInfo.setEmail(jObject.getString("email"));
//                                userInfo.setName(jObject.getString("name"));
//                                userInfo.setLink_profile(jObject.getString("link_profile"));
//                                chatRoomItem.setUserInfo(userInfo);


                            } else  if (jObject.getInt("is_private_or_group") == 2) {   // 단체채팅인 경우
                                chatRoomItem.setName_or_title(jObject.getString("title"));
                                // 그룹 프로필사진 아직은 아무것도 없는 것으로. 나중에 처리 필요
                                chatRoomItem.setLink_profile("");
                                chatRoomItem.setGroup_or_user_id(jObject.getInt("group_or_user_id"));
                            }

                            chatListArrayList.add(chatRoomItem);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    chatRoomRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        rv_chat_list = view.findViewById(R.id.rv_chat_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rv_chat_list.setLayoutManager(linearLayoutManager);
        rv_chat_list.setAdapter(chatRoomRecyclerAdapter);

//        Button btn_chat_room;
//        btn_chat_room = view.findViewById(R.id.btn_chat_room);
//        btn_chat_room.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), ChatRoomActivity.class));
//            }
//        });
        // 플로팅 액션 버튼 관련 선언
        // 플로팅 액션 버튼에서 일반 단체 채팅 및 오픈채팅을 만들 수 있게 한다.
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        fab = view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
             //   Toast.makeText(getContext(), "방 만들기", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab1:
                anim();
            //    Toast.makeText(getContext(), "일반 채팅", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), MakeChatRoomActivity.class);
                startActivity(intent);
                break;
            case R.id.fab2:
                anim();
             //   Toast.makeText(getContext(), "오픈 채팅", Toast.LENGTH_SHORT).show();
                break;
        }


    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}
