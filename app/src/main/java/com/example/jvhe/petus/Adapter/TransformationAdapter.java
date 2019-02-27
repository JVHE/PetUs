package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jvhe.petus.R;

import cn.Ragnarok.BitmapFilter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class TransformationAdapter extends RecyclerView.Adapter<TransformationAdapter.MyViewHolder> {

    private static final String TAG = "TransformationAdapter";

    // 인플레이터 선언
    LayoutInflater inflater;
    // 비디오 아이템 어레이 선언
    String[] filters = {"원본", "흑백", "릴리프", "블러", "오일 페인팅", "네온사인", "모자이크", "티브이", "네가티브", "블록", "옛 사진", "강조", "라이트", "토이카메라", "HDR", "뿌옇게", "소프트 글로우", "스케치", "움직이는", "고담"};
    // 컨텍스트
    Context context;


    String url;
    ImageView image_view;
    int selected_index = 0;

    RequestOptions requestOptions = new RequestOptions()
            .override(80, 80)
            .centerCrop()
            .dontAnimate()
            .skipMemoryCache(true);

    Bitmap bitmap;

    SparseArray<Bitmap> images = new SparseArray<>();

    public int getSelected_index() {
        return selected_index;
    }

    public void clear() {
        images.clear();
        notifyDataSetChanged();
    }

    public void setURL(String url) {
        this.url = url;
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions()
                        .override((int) convertPixelsToDp(image_view.getWidth(), context), (int) convertPixelsToDp(image_view.getHeight(), context))
                        .fitCenter()
                        .skipMemoryCache(true))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                    }
                });
        images.clear();
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(requestOptions)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        images.append(0, resource);
                        notifyItemChanged(0);
                    }
                });
        for (int i = 1; i <= 19; i++) {
            final int finalI = i;
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            images.append(finalI, BitmapFilter.changeStyle(resource, finalI));//지워봄
//                            notifyDataSetChanged();
                            if (images.size() == 20) {
                                notifyDataSetChanged();
                            }
                        }
                    });
        }
//        notifyDataSetChanged();
    }

    public TransformationAdapter(Context context, String url, ImageView image_view) {
        this.context = context;
        this.url = url;
        this.image_view = image_view;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public TransformationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 새로운 뷰를 만든다
        View v = inflater.inflate(R.layout.item_filter, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransformationAdapter.MyViewHolder holder, int position) {

        if (images.get(position) != null) {
            Glide.with(context)
                    .load(images.get(position))
                    .into(holder.iv_filter);
        }

        holder.tv_filter.setText(filters[position]);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_filter;
        TextView tv_filter;

        public MyViewHolder(View v) {
            super(v);
            iv_filter = v.findViewById(R.id.iv_filter);
            tv_filter = v.findViewById(R.id.tv_filter);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (selected_index != getAdapterPosition()) {
                selected_index = getAdapterPosition();
                Log.e(TAG, "가로 세로 이미지뷰 사이즈: " + image_view.getWidth() + "  " + image_view.getHeight());
                Log.e(TAG, "가로 세로 이미지뷰 dp사이즈: " + convertPixelsToDp(image_view.getWidth(), context) + "  " + convertPixelsToDp(image_view.getHeight(), context));

                if (selected_index != 0) {
                    Glide.with(context)
                            .load(BitmapFilter.changeStyle(bitmap, getAdapterPosition()))
                            .into(image_view);
                } else {
                    Glide.with(context)
                            .load(url)
                            .into(image_view);
                }

//                Toast.makeText(context, "클릭됨 " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
