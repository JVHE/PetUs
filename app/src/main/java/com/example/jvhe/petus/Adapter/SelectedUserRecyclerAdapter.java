package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

import java.util.ArrayList;

// 초대 후보 리스트에서 선택된 유저들을 보여주는 리사이클러 뷰
// 유저 프로필 위에 표시된 취소 버튼을 누르면 선택된 유저 목록에서 빠진다.
// 어댑터 클래스긴 하지만, 초대하기 액티비티에서 다른 리사이클러뷰와 함께 사용될 것이므로, 해당 액티비티에서 이 클래스를 선언하여 사용할 예정이다.

public class SelectedUserRecyclerAdapter extends RecyclerView.Adapter<SelectedUserRecyclerAdapter.MyViewHolder>{

    final private static String TAG = "FriendListRecyclerAdapter";

    Context context;
    ArrayList<UserInfo> userInfoArrayList;

    public SelectedUserRecyclerAdapter(Context context, ArrayList<UserInfo> userInfoArrayList) {
        this.context = context;
        this.userInfoArrayList = userInfoArrayList;
    }

    @NonNull
    @Override
    public SelectedUserRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_name, tv_follower_count, tv_desc;
        //        Button btn_follow;
        ImageView iv_profile;

        public MyViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            tv_follower_count = v.findViewById(R.id.tv_follower_count);
            tv_desc = v.findViewById(R.id.tv_number_star_balloons);
            iv_profile = v.findViewById(R.id.iv_profile);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            // 아이템을 클릭하면 아이템의 체크박스가 활성화 되면서 초대할 유저 리스트에 해당 유저를 추가하게 된다.

        }
    }
}
