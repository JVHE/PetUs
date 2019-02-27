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
import com.example.jvhe.petus.Activity.StreamRoomActivity;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.StreamRoomItem;
import com.example.jvhe.petus.R;

import java.io.Serializable;
import java.util.ArrayList;

public class StreamRecyclerAdapter extends RecyclerView.Adapter<StreamRecyclerAdapter.MyViewHolder> {

    private static final String TAG = "PostAdapter";
    // 컨텍스트
    Context context;
    // 비디오 아이템 어레이 리스트 선언
    ArrayList<StreamRoomItem> streamRoomItemArrayList;

    public StreamRecyclerAdapter(Context context, ArrayList<StreamRoomItem> streamRoomItemArrayList) {
        this.context = context;
        this.streamRoomItemArrayList = streamRoomItemArrayList;
    }

    @NonNull
    @Override
    public StreamRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 새로운 뷰를 만든다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_room, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamRecyclerAdapter.MyViewHolder holder, int position) {
        holder.tv_title.setText(streamRoomItemArrayList.get(position).getTitle());
        holder.tv_desc.setText(streamRoomItemArrayList.get(position).getDesc());
        holder.tv_name.setText(streamRoomItemArrayList.get(position).getStreamer_info().getName());
        if (!streamRoomItemArrayList.get(position).getStreamer_info().getLink_profile().equals("") && !streamRoomItemArrayList.get(position).getStreamer_info().getLink_profile().equals("null"))
            Glide.with(context)
                    .load(StaticData.url + streamRoomItemArrayList.get(position).getStreamer_info().getLink_profile())
                    .into(holder.iv_profile);
        else holder.iv_profile.setImageResource(R.drawable.profile);
    }

    @Override
    public int getItemCount() {
        return streamRoomItemArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // 제목, 설명
        TextView tv_title, tv_desc, tv_name;
        ImageView iv_profile;

        public MyViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            tv_desc = v.findViewById(R.id.tv_number_star_balloons);
            tv_name = v.findViewById(R.id.tv_name);
            iv_profile = v.findViewById(R.id.iv_profile);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            int id = v.getId();
//            switch (id) {
//                case R.id.ll_post:
            Intent intent = new Intent(context, StreamRoomActivity.class);
            intent.putExtra("stream_room", streamRoomItemArrayList.get(getAdapterPosition()));
            intent.putExtra("is_streamer", false);
            context.startActivity(intent);
//                    break;
//            }
        }
    }

}
