package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jvhe.petus.Activity.PostActivity;
import com.example.jvhe.petus.Class.PostItem;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

import java.io.Serializable;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private static final String TAG = "PostAdapter";

    // 인플레이터 선언
//    LayoutInflater inflater;
    // 비디오 아이템 어레이 리스트 선언
    ArrayList<PostItem> postItemArrayList;
    // 컨텍스트
    Context context;

    public PostAdapter(Context context, ArrayList<PostItem> postItemArrayList) {
        this.context = context;
        this.postItemArrayList = postItemArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 새로운 뷰를 만든다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(postItemArrayList.get(position).getTitle());
        holder.tv_desc.setText(postItemArrayList.get(position).getContent());
        holder.tv_name.setText(postItemArrayList.get(position).getName());
        holder.tv_time.setText(postItemArrayList.get(position).getPost_datetime());
        if (!postItemArrayList.get(position).getLink_profile().equals("") && !postItemArrayList.get(position).getLink_profile().equals("null"))
            Glide.with(context)
                    .load(StaticData.url + postItemArrayList.get(position).getLink_profile())
                    .into(holder.iv_profile);
        else holder.iv_profile.setImageResource(R.drawable.profile);
        Glide.with(context)
                .setDefaultRequestOptions(StaticData.requestOptions)
                .load(StaticData.url + postItemArrayList.get(position).getPath())
                .into(holder.iv_thumbnail);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return postItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // 제목, 설명
        TextView tv_title, tv_desc, tv_name, tv_follow, tv_time;
        ImageView iv_thumbnail, iv_profile;


        LinearLayout ll_post;

        public MyViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            tv_desc = v.findViewById(R.id.tv_number_star_balloons);
            tv_name = v.findViewById(R.id.tv_name);
            iv_profile = v.findViewById(R.id.iv_profile);
            iv_thumbnail = v.findViewById(R.id.iv_thumbnail);
            tv_follow = v.findViewById(R.id.tv_follow);
            tv_time = v.findViewById(R.id.tv_time);
            ll_post = v.findViewById(R.id.ll_post);
            ll_post.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ll_post:
                    Intent intent = new Intent(context, PostActivity.class);
                    intent.putExtra("post", (Serializable) postItemArrayList.get(getAdapterPosition()));
//                    intent.putExtra("post_id", userInfoArrayList.get(getAdapterPosition()).getPost_id());
//                    intent.putExtra("title", userInfoArrayList.get(getAdapterPosition()).getTitle());
//                    intent.putExtra("content", userInfoArrayList.get(getAdapterPosition()).getContent());
//                    intent.putExtra("post_datetime", userInfoArrayList.get(getAdapterPosition()).getPost_datetime());
//                    intent.putExtra("post_comment_count", userInfoArrayList.get(getAdapterPosition()).getPost_comment_count());
//                    intent.putExtra("post_like", userInfoArrayList.get(getAdapterPosition()).getPost_like());
//                    intent.putExtra("post_dislike", userInfoArrayList.get(getAdapterPosition()).getPost_dislike());
//                    intent.putExtra("path", userInfoArrayList.get(getAdapterPosition()).getPath());
//                    intent.putExtra("name", userInfoArrayList.get(getAdapterPosition()).getName());
//                    intent.putExtra("link_profile", userInfoArrayList.get(getAdapterPosition()).getLink_profile());

                    context.startActivity(intent);
                    break;
            }
        }
    }


//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.item_social_review, null);
//        }
//
//        // 제목을 뷰의 레이아웃과 연결한다.
//        tv_title = ViewHolder.get(convertView, R.id.tv_title);
//        // 제목 설정
//        tv_title.setText(data_arr.get(position).getTitle());
//
//        // 설명을 뷰의 레이아웃과 연결한다.
//        tv_desc = ViewHolder.get(convertView, R.id.tv_desc);
//        // 제목 설정
//        tv_desc.setText(data_arr.get(position).getDesc());
//
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data_arr.get(position).getUrl())));
//            }
//        });
//
//        return convertView;
//    }

}
