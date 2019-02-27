package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jvhe.petus.Class.ChatItem;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ChatRecyclerAdapter";
    Context context;
    ArrayList<ChatItem> data;

//    HashMap<String, Bitmap> profileMap = new HashMap<>();


    public ChatRecyclerAdapter(Context context, ArrayList<ChatItem> data) {
        this.context = context;
        this.data = data;
    }

    // 뷰타입 1: 내가 쓴 채팅
    // 뷰타입 2: 나 말고 다른 누군가가 쓴 채팅
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        // 새로운 뷰를 만든다
        switch (viewType) {
            // 내 채팅
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_chat, parent, false);
                return new MyChatViewHolder(v);
            // 상대 채팅
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
                return new MyChatViewHolder(v);
            // 내 미디어
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_image, parent, false);
                return new MyImageViewHolder(v);
            // 상대 미디어
            case 4:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
                return new MyImageViewHolder(v);
            // 에러생기면 뻑가니 나중에 수정 필요
            default:
                return new MyChatViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

//        holder.tv_name.setText(data.get(position).getName());
//        holder.tv_time.setText(data.get(position).getMsg_datetime());
//        holder.tv_unread.setText("" + data.get(position).getCnt_unread());
//        // 프로필 사진 처리 나중에 필요하다.
//        Glide.with(context)
//                .load(StaticData.url + data.get(position).getLink_profile())
//                .apply(StaticData.requestOptions_rounded)
//                .into(holder.iv_profile);
//
//
////        final int finalpos = position;
//        if (!profileMap.containsKey(data.get(position).getLink_profile())) {
//            Glide.with(context)
//                    .load(StaticData.url + data.get(position).getLink_profile())
//                    .apply(StaticData.requestOptions_rounded)
//                    .into(holder.iv_profile);
//            Glide.with(context)
//                    .asBitmap()
//                    .load(StaticData.url + data.get(position).getLink_profile())
//                    .apply(StaticData.requestOptions_rounded)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            profileMap.put(data.get(finalpos).getLink_profile(), resource);
//                        }
//                    });
//        } else {
//            Glide.with(context)
//                    .load(profileMap.get(data.get(position).getLink_profile()))
//                    .apply(StaticData.requestOptions_rounded)
//                    .into(holder.iv_profile);
//        }
        Log.e(TAG, "메세지 내용: "+data.get(position).getMsg());
        if (holder.getItemViewType() <= 2) {
            ((MyChatViewHolder) holder).tv_msg.setText(data.get(position).getMsg());

            ((MyChatViewHolder) holder).tv_name.setText(data.get(position).getName());
            ((MyChatViewHolder) holder).tv_time.setText(data.get(position).getMsg_datetime());
            ((MyChatViewHolder) holder).tv_unread.setText("" + data.get(position).getCnt_unread());
            // 프로필 사진 처리 나중에 필요하다.
            Glide.with(context)
                    .load(StaticData.url + data.get(position).getLink_profile())
                    .apply(StaticData.requestOptions_rounded)
                    .into(((MyChatViewHolder) holder).iv_profile);

        } else if (holder.getItemViewType() > 2) {

            Glide.with(context)
                    .load(StaticData.url + data.get(position).getMsg())
                    .apply(StaticData.requestOptions)
                    .into(((MyImageViewHolder) holder).iv_image);

//            Glide.with(context)
//                    .asBitmap()
//                    .load(StaticData.url + data.get(position).getMsg())
//                    .apply(StaticData.requestOptions)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            profileMap.put(data.get(finalpos).getLink_profile(), resource);
//                        }
//                    });

            ((MyImageViewHolder) holder).tv_name.setText(data.get(position).getName());
            ((MyImageViewHolder) holder).tv_time.setText(data.get(position).getMsg_datetime());
            ((MyImageViewHolder) holder).tv_unread.setText("" + data.get(position).getCnt_unread());
            // 프로필 사진 처리 나중에 필요하다.
            Glide.with(context)
                    .load(StaticData.url + data.get(position).getLink_profile())
                    .apply(StaticData.requestOptions_rounded)
                    .into(((MyImageViewHolder) holder).iv_profile);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_name, tv_time, tv_unread;

        public MyViewHolder(View v) {
            super(v);
            iv_profile = v.findViewById(R.id.iv_profile);
            tv_name = v.findViewById(R.id.tv_name);
            tv_time = v.findViewById(R.id.tv_time);
            tv_unread = v.findViewById(R.id.tv_unread);

        }

    }

    public class MyChatViewHolder extends RecyclerView.ViewHolder {
        TextView tv_msg;
        ImageView iv_profile;
        TextView tv_name, tv_time, tv_unread;
        public MyChatViewHolder(View v) {
            super(v);
            tv_msg = v.findViewById(R.id.tv_msg);
            iv_profile = v.findViewById(R.id.iv_profile);
            tv_name = v.findViewById(R.id.tv_name);
            tv_time = v.findViewById(R.id.tv_time);
            tv_unread = v.findViewById(R.id.tv_unread);
        }
    }

    public class MyImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;

        ImageView iv_profile;
        TextView tv_name, tv_time, tv_unread;
        public MyImageViewHolder(View v) {
            super(v);
            iv_image = v.findViewById(R.id.iv_image);
            iv_profile = v.findViewById(R.id.iv_profile);
            tv_name = v.findViewById(R.id.tv_name);
            tv_time = v.findViewById(R.id.tv_time);
            tv_unread = v.findViewById(R.id.tv_unread);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.e(TAG, "뷰타입: "+data.get(position).getChat_type());
        if (data.get(position).getUser_id() == context.getSharedPreferences("current_user", Context.MODE_PRIVATE).getInt("id_db", -100)) {
            // 뷰타입 1: 내가 쓴 채팅
            if (data.get(position).getChat_type() == 1)
                return 1;
            // 뷰타입 3: 내가 업로드한 이미지
            else if (data.get(position).getChat_type() == 3)
                return 3;
        }
        else {
            // 뷰타입 2: 나 말고 다른 누군가가 쓴 채팅
            if (data.get(position).getChat_type() == 1)
                return 2;
                // 뷰타입 3: 나 말고 다른 누군가가 업로드한 이미지
            else if (data.get(position).getChat_type() == 3)
                return 4;
        }

        return -1;
//        return super.getItemViewType(position);
    }
}
