package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jvhe.petus.Activity.CafeInformationActivity;
import com.example.jvhe.petus.Class.CafeItem;
import com.example.jvhe.petus.R;

import java.util.ArrayList;

public class CafeRecyclerAdapter extends RecyclerView.Adapter<CafeRecyclerAdapter.MyViewHolder> {

    private static final String TAG = "CafeRecyclerAdapter";
    Context context;
    ArrayList<CafeItem> data;

    // 인플레이터 선언
    LayoutInflater inflater;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    AsyncTask asyncTask;
    // 이미지 저장용 SparseArray
    SparseArray<Bitmap> sparseArray_image;
    // AsyncTask 저장용 SparseArray
    SparseArray<AsyncTask> sparseArray_async;
    SparseBooleanArray sparseArray_glide;

    public CafeRecyclerAdapter(Context context, ArrayList<CafeItem> data) {
        super();
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sparseArray_image = new SparseArray<>();
        this.sparseArray_async = new SparseArray<>();
        this.sparseArray_glide = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 새로운 뷰를 만든다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cafe_grid, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

//        ViewGroup.LayoutParams params = holder.iv_cafe_thumbnail.getLayoutParams();
//        params.height = params.width;
//        holder.iv_cafe_thumbnail.setLayoutParams(params);
        final int finalpos = position;
        // 이미지 처리
        if (sparseArray_image.get(position) != null) {
//            thumbnail.setImageBitmap(sparseArray_image.get(i));
            holder.iv_cafe_thumbnail.setImageBitmap(sparseArray_image.get(position));
        } else if (!sparseArray_glide.get(position)) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=200&photoreference=" + data.get(position).getImage_url() + "&key=..";
            Glide.with(context).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    sparseArray_image.append(finalpos, resource);

                    if (data.size() > finalpos)
                        notifyItemChanged(finalpos);
                    //지워봄
//                    notifyDataSetChanged();
                }
            });
            sparseArray_glide.append(position, true);

        } else {
            holder.iv_cafe_thumbnail.setImageResource(R.drawable.dog_cafe_6);
        }


//        holder.iv_cafe_thumbnail.setImageResource(data_arr.get(position).getImage_url());
        holder.tv_cafe_name.setText(data.get(position).getName());
        StringBuilder st = new StringBuilder();
        String[] starr = data.get(position).getLocation().split(" ");
        for (int i = 0; i < 2; i++) {
            st.append(starr[i]).append(" ");
        }
        holder.tv_cafe_loc.setText(st.toString());
        // 거리 자동 계산
//        double dist = getDistance(userInfoArrayList.get(position).getLatitude(), userInfoArrayList.get(position).getLongitude(), StaticData.my_lat, StaticData.my_lng);
        double dist = data.get(position).getDistance();
        dist /= 1000;
        holder.tv_cafe_distance.setText(Double.parseDouble(String.format("%.2f", dist)) + "km");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CafeInformationActivity.class);
                intent.putExtra("cafe", data.get(finalpos));
                if (sparseArray_image.get(finalpos) != null) {
                    intent.putExtra("image", sparseArray_image.get(finalpos));
                }
                context.startActivity(intent);
            }
        });

//        holder.tv_cafe_distance.setText(data_arr.get(position).get());
    }


//    public class MyAsyncTask extends AsyncTask {
//
//        int position;
//
//        public MyAsyncTask(int position) {
//            this.position = position;
//        }
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            // 임시로 썸네일을 가져온다. 나중에 데이터베이스로 이미지와 이미지 링크를 저장하면 그땐 썸네일 이미지를 링크로 가져올 것이다.
//            // 썸네일 이미지 만들기
////            thumbnail_bitmap = ThumbnailUtils.createVideoThumbnail(data_arr.get(position).getLink_video(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
//
//            String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=230&photoreference=" + data_arr.get(position).getImage_url() + "&key=..";
////
////            RequestOptions options = new RequestOptions()
////                    .centerCrop();
//////                .error(R.mipmap.ic_launcher_round);
//////                .placeholder(R.mipmap.ic_launcher_round)
////            Bitmap bitmap;
//            Glide.with(context).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                    sparseArray_image.append(position, resource);
//                    notifyDataSetChanged();
//                }
//            });
//
////        // 썸네일 이미지뷰 레이아웃과 매칭
////        thumbnail = view.findViewById(R.id.thumbnail);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
////                    if (position == MyClass.pos) {
////                        thumbnail.setImageBitmap((Bitmap) o);
////                    ((ImageView)ViewHolder.get(finalView,R.id.thumbnail)).setImageBitmap((Bitmap)o);
////                    }
////                    title.setText("현재 포지션!: " + MyClass.pos + " position: " + position);
//            //   notifyDataSetChanged();
////            notifyDataSetChanged();
//            Log.e(TAG, "" + data_arr.size());
//        }
//
//    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_cafe_thumbnail;
        TextView tv_cafe_name, tv_cafe_loc, tv_cafe_distance;

        public MyViewHolder(View v) {
            super(v);
            iv_cafe_thumbnail = v.findViewById(R.id.iv_cafe_thumbnail);
            tv_cafe_name = v.findViewById(R.id.tv_cafe_name);
            tv_cafe_loc = v.findViewById(R.id.tv_cafe_loc);
            tv_cafe_distance = v.findViewById(R.id.tv_cafe_distance);
        }
    }

    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        Bitmap bmImage;
//
//        public DownloadImageTask(Bitmap bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                //            LogUtil.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            bmImage = mIcon11;
//            return mIcon11;
//        }
//
//        protected void onPostExecute(ImageView iv) {
////            iv.setImageBitmap(bmImage);
//        }
//    }

}
