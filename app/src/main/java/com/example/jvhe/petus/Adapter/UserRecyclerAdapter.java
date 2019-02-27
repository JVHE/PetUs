package com.example.jvhe.petus.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jvhe.petus.Activity.UserProfileActivity;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

import java.io.Serializable;
import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.MyViewHolder> {

    final private static String TAG = "UserRecyclerAdapter";

    Context context;
    ArrayList<UserInfo> userInfoArrayList;
    int user_id;

    public UserRecyclerAdapter(Context context, ArrayList<UserInfo> userInfoArrayList) {
        this.context = context;
        this.userInfoArrayList = userInfoArrayList;
        this.user_id = context.getSharedPreferences("current_user", Context.MODE_PRIVATE).getInt("id_db", -100);
    }

    @NonNull
    @Override
    public UserRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 새로운 뷰를 만든다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerAdapter.MyViewHolder holder, int position) {
        holder.tv_name.setText(userInfoArrayList.get(position).getName());
        if (!userInfoArrayList.get(position).getLink_profile().equals("") && !userInfoArrayList.get(position).getLink_profile().equals("null"))
            Glide.with(context)
                    .load(StaticData.url + userInfoArrayList.get(position).getLink_profile())
                    .apply(StaticData.requestOptions_rounded)
                    .into(holder.iv_profile);
        else holder.iv_profile.setImageResource(R.drawable.profile);
        if (userInfoArrayList.get(position).isIs_following() || userInfoArrayList.get(position).getIs_friend() == 1) {
            holder.btn_follow.setText("언팔로우");
        } else {
            holder.btn_follow.setText("팔로우");
        }
//        if (userInfoArrayList.get(position).getIs_friend() == 1) {
//            holder.btn_follow.setText("언팔로우");
//        } else {
//            holder.btn_follow.setText("팔로우");
//        }
    }

    @Override
    public int getItemCount() {
        return userInfoArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name, tv_follower_count, tv_desc;
        Button btn_follow;
        ImageView iv_profile;

        public MyViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            tv_follower_count = v.findViewById(R.id.tv_follower_count);
            tv_desc = v.findViewById(R.id.tv_number_star_balloons);
            iv_profile = v.findViewById(R.id.iv_profile);
            btn_follow = v.findViewById(R.id.btn_follow);
            btn_follow.setOnClickListener(this);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_follow:
                    if (btn_follow.getText().toString().equals("팔로우")) {
                        // 팔로우 할 때 작업 요청 httpasync로? 나중에 확인
                        HttpAsync httpAsync = new HttpAsync("follower=" + user_id + "&followee=" + userInfoArrayList.get(getAdapterPosition()).getUser_id(),
                                StaticData.url + "follow.php", handler);
                        httpAsync.execute();
                        userInfoArrayList.get(getAdapterPosition()).setIs_following(true);
//                        btn_follow.setText("언팔로우");
                    } else {
                        // 팔로우 할 때 작업 요청 httpasync로? 나중에 확인
                        HttpAsync httpAsync = new HttpAsync("follower=" + user_id + "&followee=" + userInfoArrayList.get(getAdapterPosition()).getUser_id(),
                                StaticData.url + "unfollow.php", handler);
                        httpAsync.execute();
                        userInfoArrayList.get(getAdapterPosition()).setIs_following(false);
                        userInfoArrayList.get(getAdapterPosition()).setIs_friend(0);
//                        btn_follow.setText("팔로우");
                    }
                    notifyItemChanged(getAdapterPosition());
                    break;
                default:
                    Log.e(TAG, "아이템 클릭됨");
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("user_info", (Serializable) userInfoArrayList.get(getAdapterPosition()));
                    // 메인 액티비티의 화면을 두 번째 프래그먼트(채팅방 목록 프래그먼트)로 가게 만들고 싶다.
                    context.startActivity(intent);
//                    ((MainActivity) context).navigation.setSelectedItemId(R.id.navigation_dashboard);
//                    ((MainActivity) context).navigation.performClick();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(context, "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                if (data != null && data.contains("!!fail!!")) {
                    Toast.makeText(context, "팔로우 실패. 관리자에게 문의해 주세요", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "팔로우 실패");
                } else if (data != null && data.contains("!!follow!!")) {
                    Toast.makeText(context, "팔로우!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "팔로우 성공");

                } else if (data != null && data.contains("!!unfollow!!")) {
                    Toast.makeText(context, "언팔로우!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "언팔로우 성공");
                }
            }
        }
    };
}
