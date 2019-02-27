package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MakeChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "MakeChatRoomActivity";

    ActionBar actionBar;

    EditText et_title;
    String title = "";  // 방제목

    int user_id;

    ArrayList<UserInfo> userArrayList;
    FriendListRecyclerAdapter friendListRecyclerAdapter;
    RecyclerView rv_user;

    ArrayList<UserInfo> selectedUserArrayList;
    SelectedUserRecyclerAdapter selectedUserRecyclerAdapter;
    RecyclerView rv_selected_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_chat_room);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("그룹 채팅");
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_title = findViewById(R.id.et_title);

        user_id = getSharedPreferences("current_user", MODE_PRIVATE).getInt("id_db", -100);

        // 유저 목록 리사이클러뷰 관련 선언
        userArrayList = new ArrayList<>();
        friendListRecyclerAdapter = new FriendListRecyclerAdapter(this, userArrayList);
        rv_user = findViewById(R.id.rv_user);
        rv_user.setLayoutManager(new LinearLayoutManager(this));
        rv_user.setAdapter(friendListRecyclerAdapter);

        // 후보 목록 리사이클러뷰 관련 선언
        selectedUserArrayList = new ArrayList<>();
        selectedUserRecyclerAdapter = new SelectedUserRecyclerAdapter(this, selectedUserArrayList);
        rv_selected_user = findViewById(R.id.rv_selected_user);
        rv_selected_user.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_selected_user.setAdapter(selectedUserRecyclerAdapter);

        // 초대할 수 있는 유저 목록을 불러온다.
        HttpAsync httpAsync = new HttpAsync("user_id=" + user_id + "&type=following", StaticData.url + "request_user.php", handler_user);
        httpAsync.execute();

    }

// 그룹 채팅에 초대할 수 있는 사람들을 표시해 주는 리사이클러뷰의 어댑터
// 리스트에 나열된 아이템을 클릭하게 되면 초대할 유저 리스트에 해당 유저를 추가하게 된다.
// 어댑터 클래스긴 하지만, 초대하기 액티비티에서 다른 리사이클러뷰와 함께 사용될 것이므로, 해당 액티비티에서 이 클래스를 선언하여 사용할 예정이다.

    public class FriendListRecyclerAdapter extends RecyclerView.Adapter<FriendListRecyclerAdapter.MyViewHolder> {

        Context context;
        ArrayList<UserInfo> userInfoArrayList;

        public FriendListRecyclerAdapter(Context context, ArrayList<UserInfo> userInfoArrayList) {
            this.context = context;
            this.userInfoArrayList = userInfoArrayList;
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 새로운 뷰를 만든다
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendListRecyclerAdapter.MyViewHolder holder, int position) {
            holder.tv_name.setText(userInfoArrayList.get(position).getName());
            if (!userInfoArrayList.get(position).getLink_profile().equals("") && !userInfoArrayList.get(position).getLink_profile().equals("null"))
                Glide.with(context)
                        .load(StaticData.url + userInfoArrayList.get(position).getLink_profile())
                        .apply(StaticData.requestOptions_rounded)
                        .into(holder.iv_profile);
            else holder.iv_profile.setImageResource(R.drawable.profile);
            if (userInfoArrayList.get(position).isIs_selected()) {
                holder.itemView.setBackgroundColor(0xb5b0f3ff);
            } else {
                holder.itemView.setBackgroundColor(0xffffff);
            }
            holder.cb_add.setChecked(userInfoArrayList.get(position).isIs_selected());
        }

        @Override
        public int getItemCount() {
            return userInfoArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv_name, tv_follower_count, tv_desc;
            //        Button btn_follow;
            ImageView iv_profile;
            CheckBox cb_add;

            public MyViewHolder(View v) {
                super(v);
                tv_name = v.findViewById(R.id.tv_name);
                tv_follower_count = v.findViewById(R.id.tv_follower_count);
                tv_desc = v.findViewById(R.id.tv_number_star_balloons);
                iv_profile = v.findViewById(R.id.iv_profile);
                cb_add = v.findViewById(R.id.cb_add);
                cb_add.setOnClickListener(this);
//            btn_follow = v.findViewById(R.id.btn_follow);
//            btn_follow.setOnClickListener(this);
                v.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                // 아이템을 클릭하면 아이템의 체크박스가 활성화 되면서 초대할 유저 리스트에 해당 유저를 추가하게 된다.
                if (userInfoArrayList.get(getAdapterPosition()).isIs_selected()) {
                    userInfoArrayList.get(getAdapterPosition()).setIs_selected(false);
                    selectedUserArrayList.remove(userInfoArrayList.get(getAdapterPosition()));
//                    if (!userInfoArrayList.isEmpty())
//                        selectedUserRecyclerAdapter.notifyItemRemoved(userInfoArrayList.size() - 1);
                    selectedUserRecyclerAdapter.notifyDataSetChanged();
                } else {
                    userInfoArrayList.get(getAdapterPosition()).setIs_selected(true);
                    selectedUserArrayList.add(userInfoArrayList.get(getAdapterPosition()));
                    selectedUserRecyclerAdapter.notifyItemInserted(userInfoArrayList.size() - 1);
                }
                notifyItemChanged(getAdapterPosition());
            }
        }
    }

// 초대 후보 리스트에서 선택된 유저들을 보여주는 리사이클러 뷰
// 유저 프로필 위에 표시된 취소 버튼을 누르면 선택된 유저 목록에서 빠진다.
// 어댑터 클래스긴 하지만, 초대하기 액티비티에서 다른 리사이클러뷰와 함께 사용될 것이므로, 해당 액티비티에서 이 클래스를 선언하여 사용할 예정이다.

    public class SelectedUserRecyclerAdapter extends RecyclerView.Adapter<SelectedUserRecyclerAdapter.MyViewHolder> {


        Context context;
        ArrayList<UserInfo> userInfoArrayList;

        public SelectedUserRecyclerAdapter(Context context, ArrayList<UserInfo> userInfoArrayList) {
            this.context = context;
            this.userInfoArrayList = userInfoArrayList;
        }

        @NonNull
        @Override
        public SelectedUserRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 새로운 뷰를 만든다
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_user, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tv_name.setText(userInfoArrayList.get(position).getName());
            if (!userInfoArrayList.get(position).getLink_profile().equals("") && !userInfoArrayList.get(position).getLink_profile().equals("null"))
                Glide.with(context)
                        .load(StaticData.url + userInfoArrayList.get(position).getLink_profile())
                        .apply(StaticData.requestOptions_rounded)
                        .into(holder.iv_profile);
            else holder.iv_profile.setImageResource(R.drawable.profile);
        }

        @Override
        public int getItemCount() {
            return userInfoArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv_name;
            //        Button btn_follow;
            ImageView iv_profile, iv_cancel;

            public MyViewHolder(View v) {
                super(v);
                tv_name = v.findViewById(R.id.tv_name);
                iv_profile = v.findViewById(R.id.iv_profile);
                iv_cancel = v.findViewById(R.id.iv_cancel);
                iv_cancel.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                // 취소 이미지 버튼을 누르게 되면 선택된 유저 목록에서 빠진다.
                // 초대 후보 목록의 체크박스 또한 비활성화를 시킬 것이다.
                switch (v.getId()) {
                    case R.id.iv_cancel:
                        userInfoArrayList.get(getAdapterPosition()).setIs_selected(false);
                        userInfoArrayList.remove(getAdapterPosition());
                        // 지워봄
//                        friendListRecyclerAdapter.notifyDataSetChanged();
                        friendListRecyclerAdapter.notifyDataSetChanged();

//                        notifyDataSetChanged();//지워봄
                        notifyItemChanged(getAdapterPosition());
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler_user = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 메세지를 받아오지 못한 경우
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            // 메세지를 받아온 경우
            else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    userArrayList.clear();//지워봄
//                    friendListRecyclerAdapter.notifyDataSetChanged();
                    try {
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            final UserInfo userInfo = new UserInfo();
                            // 자신의 유저 정보는 유저 리스트에 보여주지 않는다.
                            if (jObject.getInt("id") == user_id) {
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
//
//                            if (!jObject.getString("is_friend").equals("null")) {
//                                Log.e(TAG, "테스트11" + jObject.get("is_friend"));
////                                userInfo.setIs_friend(jObject.getInt("is_friend"));
//                                userInfo.setIs_friend(1);
//                            } else {
//                                Log.e(TAG, "테스트22" + jObject.get("is_friend"));
//                                userInfo.setIs_friend(0);
//                            }

                            // 프로필 링크 작업 나중에
//                            userInfo.setEmail(jObject.getString("email"));
                            userArrayList.add(userInfo);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    friendListRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    };


    // 액션바의 옵션 메뉴칸을 선언하는 메소드. 해당 버튼을 통해 단체 채팅방을 만들 수 있다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_make_room, menu);
        return true;
    }

    // 액션바의 뒤로가기 버튼을 눌렀을 때, 이전 화면(채팅방 목록)으로 돌아갈 수 있다.
    // 만들기 버튼을 눌렀을 때, 채팅방이 만들어진다. 예외처리(선택된 사람이 2명 이상일 때만 방을 만들 수 있도록 하는 등)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:  //toolbar의 back키 눌렀을 때 동작
                finish();
                break;
            case R.id.make_room:
                // 방만들기 버튼을 눌렀을 때 동작
                // 방을 만든 후, 채팅방에 입장하도록 한다
                // 방만들기 요청 코드에서는 함꼐 방을 만들고자 하는 유저들의 목록과 방 제목을 서버에 보내고,
                // 생성된 방 번호를 가져온다.
                if (selectedUserArrayList.size() >= 2) {

                    if (et_title.length() != 0) {
                        title = et_title.getText().toString();
                    } else {
                        for (int i = 0; i < selectedUserArrayList.size() && title.length() < 30; i++) {
                            title += selectedUserArrayList.get(i).getName();
                            if (i < selectedUserArrayList.size() - 1) {
                                title += ", ";
                            }
                            if (title.length() >= 30) {
                                title += "...";
                            }
                        }
                    }
                    // 참여자 번호를 서버에 String형태로 전송한다.
                    // 0번에는 방을 만드는 사람의 user_id,
                    // 그리고 차례차례대로 초대하고자 하는 사람의 user_id를 가져온다
                    String user_numbers = "&user_id0=" + user_id;
                    for (int i = 0; i < selectedUserArrayList.size(); i++) {
                        user_numbers += "&user_id" + (i + 1) + "=" + selectedUserArrayList.get(i).getUser_id();
                    }
                    HttpAsync httpAsync = new HttpAsync("title=" + title + user_numbers, StaticData.url + "make_group_chat_room.php", handler);
                    httpAsync.execute();
                } else {
                    Toast.makeText(this, "최소 두 명 이상의 유저를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                }


        }
        return true;
//        return super.onOptionsItemSelected(item);
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
                // Log.e("!!!!!!!!!!!!!", data_arr);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "그룹 채팅방 만들기 실패", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    intent.putExtra("is_private_or_group", "group");
                    intent.putExtra("group_or_user_id", Integer.parseInt(data.trim()));
                    intent.putExtra("name_or_title", title);
                    startActivity(intent);
                    finish();
                }
            }
        }
    };

}
