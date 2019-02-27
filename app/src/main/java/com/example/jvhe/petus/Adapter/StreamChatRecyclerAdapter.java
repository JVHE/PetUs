package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jvhe.petus.R;

import java.util.ArrayList;

public class StreamChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final String TAG = "PostAdapter";
    // 컨텍스트
    Context context;
    // 비디오 아이템 어레이 리스트 선언
    ArrayList<String> stringArrayList;

    public StreamChatRecyclerAdapter(Context context, ArrayList<String> stringArrayList) {
        this.context = context;
        this.stringArrayList = stringArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        // 새로운 뷰를 만든다
        switch (viewType) {
            // 채팅
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_chat, parent, false);
                return new MyViewHolder(v);
            // 별풍선
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_chat_balloons, parent, false);
                return new MyStarBalloonViewHolder(v);
        }

        // 예외처리
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_chat, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!방장!!")) {
            ((MyViewHolder) holder).tv_chat.setTextColor(0xFF1D25FF);
            String msg = stringArrayList.get(position).substring(6);
            ((MyViewHolder) holder).tv_chat.setText(msg);
        } else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!본인!!")) {
            ((MyViewHolder) holder).tv_chat.setTextColor(0xFF9136DB);
            String msg = stringArrayList.get(position).substring(6);
            ((MyViewHolder) holder).tv_chat.setText(msg);
        } else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!뷰어!!")) {
            ((MyViewHolder) holder).tv_chat.setTextColor(0xFF242424);
            String msg = stringArrayList.get(position).substring(6);
            ((MyViewHolder) holder).tv_chat.setText(msg);
        } else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!접속!!")) {
            ((MyViewHolder) holder).tv_chat.setTextColor(0xFF9DFF3A);
            String msg = stringArrayList.get(position).substring(6);
            ((MyViewHolder) holder).tv_chat.setText(msg);
        }
        // 별풍선 처리
        else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!별풍!!")) {
            String msg = stringArrayList.get(position).substring(6);
            String number_star_balloons = msg.split("!!")[0];
            String name = msg.split("!!")[1];
            String message = msg.split("!!")[2];

            ((MyStarBalloonViewHolder) holder).tv_number_star_balloons.setText(number_star_balloons);
            ((MyStarBalloonViewHolder) holder).tv_chat.setText(message);
            ((MyStarBalloonViewHolder) holder).tv_name.setText(name);
            String string = "별풍선 " + number_star_balloons + "개 선물!!";
            ((MyStarBalloonViewHolder) holder).tv_desc.setText(string);
        }

    }

//
//    public void onBindViewHolder(@NonNull StreamChatRecyclerAdapter.MyViewHolder holder, int position) {
//    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_chat;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_chat = itemView.findViewById(R.id.tv_chat);
        }
    }

    public class MyStarBalloonViewHolder extends RecyclerView.ViewHolder {

        TextView tv_number_star_balloons, tv_chat, tv_desc, tv_name;

        public MyStarBalloonViewHolder(View itemView) {
            super(itemView);
            tv_number_star_balloons = itemView.findViewById(R.id.tv_number_star_balloons);
            tv_chat = itemView.findViewById(R.id.tv_chat);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }


    @Override
    public int getItemViewType(int position) {
        Log.e(TAG, "뷰타입: " + stringArrayList.get(position));

        if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!방장!!")) {
            return 1;
        } else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!본인!!")) {
            return 1;
        } else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!뷰어!!")) {
            return 1;
        } else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!접속!!")) {
            return 1;
        } else if (stringArrayList.get(position).length() >= 6 && stringArrayList.get(position).substring(0, 6).equals("!!별풍!!")) {
            return 2;
        }

        return -1;
//        return super.getItemViewType(position);
    }
}
