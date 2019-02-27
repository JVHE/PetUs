package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jvhe.petus.R;
import com.example.jvhe.petus.Class.ReviewItem;

import java.util.ArrayList;

import cn.Ragnarok.BitmapFilter;

public class SocialReviewAdapter extends RecyclerView.Adapter<SocialReviewAdapter.MyViewHolder> {

    private static final String TAG = "SocialReviewAdapter";

    // 인플레이터 선언
    LayoutInflater inflater;
    // 비디오 아이템 어레이 리스트 선언
    ArrayList<ReviewItem> data;
    // 컨텍스트
    Context context;

BitmapFilter bitmapFilter;
    public SocialReviewAdapter(Context context, ArrayList<ReviewItem> data) {
        super();
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 새로운 뷰를 만든다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social_review, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(data.get(position).getTitle());
        holder.tv_desc.setText(data.get(position).getDesc());
        final int finalI = position;
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(finalI).getUrl().replace("\\","").replace("amp;",""))));
            }
        });
//        Glide.with(context)
//                .load(Uri.parse(data_arr.get(position).getUrl()))
//                .into(holder.iv_thumbnail);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // 제목, 설명
        TextView tv_title, tv_desc;
        LinearLayoutCompat ll_item;
        ImageView iv_thumbnail;

        public MyViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            tv_desc = v.findViewById(R.id.tv_number_star_balloons);
            ll_item = v.findViewById(R.id.ll_item);
            iv_thumbnail = v.findViewById(R.id.iv_thumbnail);
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
