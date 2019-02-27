package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jvhe.petus.Activity.ChatRoomActivity;
import com.example.jvhe.petus.Class.ChatRoomItem;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

import java.util.ArrayList;

public class ChatRoomRecyclerAdapter extends RecyclerView.Adapter<ChatRoomRecyclerAdapter.MyViewHolder> {

    private static final String TAG = "ChatRoomRecyclerAdapter";
    Context context;
    ArrayList<ChatRoomItem> data;

    public ChatRoomRecyclerAdapter(Context context, ArrayList<ChatRoomItem> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_name.setText(data.get(position).getName_or_title());
//        holder.tv_msg.setText(data.get(position).getMsg());
//        holder.tv_time.setText(data.get(position).getMsg_datetime());
        // 프로필 사진 처리 나중에 필요하다.
//        Glide.with(context)
//                .load(StaticData.url+data.get(position).getLink_profile())
//                .apply(StaticData.requestOptions_rounded)
//                .into(holder.iv_profile);
        if (!data.get(position).getLink_profile().equals("") && !data.get(position).getLink_profile().equals("null"))
            Glide.with(context)
                    .load(StaticData.url + data.get(position).getLink_profile())
                    .apply(StaticData.requestOptions_rounded)
                    .into(holder.iv_profile);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_profile;
        TextView tv_name, tv_msg, tv_time;

        public MyViewHolder(View v) {
            super(v);
            iv_profile = v.findViewById(R.id.iv_profile);
            tv_name = v.findViewById(R.id.tv_name);
            tv_msg = v.findViewById(R.id.tv_msg);
            tv_time = v.findViewById(R.id.tv_time);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, ChatRoomActivity.class);
            if (data.get(getAdapterPosition()).getIs_private_or_group() == 1) {
                intent.putExtra("is_private_or_group", "private");
//                intent.putExtra("user_info_target", data.get(getAdapterPosition()).getUserInfo());
                intent.putExtra("group_or_user_id", data.get(getAdapterPosition()).getGroup_or_user_id());
                intent.putExtra("name_or_title", data.get(getAdapterPosition()).getName_or_title());
            } else if (data.get(getAdapterPosition()).getIs_private_or_group() == 2) {
                intent.putExtra("is_private_or_group", "group");
                intent.putExtra("group_or_user_id", data.get(getAdapterPosition()).getGroup_or_user_id());
                intent.putExtra("name_or_title", data.get(getAdapterPosition()).getName_or_title());

            }
            context.startActivity(intent);
        }
    }
}
