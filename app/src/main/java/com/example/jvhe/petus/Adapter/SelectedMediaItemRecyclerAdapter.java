package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jvhe.petus.Activity.UploadActivity;
import com.example.jvhe.petus.R;

import java.net.URLConnection;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


// 어댑터 클래스긴 하지만, 업로드 액티비티에서 다른 리사이클러뷰와 함께 사용될 것이므로, 해당 액티비티에서 이 클래스를 선언하여 사용할 예정이다.
public class SelectedMediaItemRecyclerAdapter extends RecyclerView.Adapter<SelectedMediaItemRecyclerAdapter.MyViewHolder> {

    private static final String TAG = "CafeRecyclerAdapter";
    Context context;
    ArrayList<String> data;


    // 인플레이터 선언
    LayoutInflater inflater;

    ImageView image_view;
    VideoView video_view;

    MediaController mediaController;

    public int last_pos = -1;

    public SelectedMediaItemRecyclerAdapter(Context context, ArrayList<String> data, ImageView image_view, VideoView video_view, MediaController mediaController) {
        super();
        this.context = context;
        this.data = data;
        this.image_view = image_view;
        this.video_view = video_view;
        this.mediaController = mediaController;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 새로운 뷰를 만든다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_media, parent, false);
        return new MyViewHolder(v);
    }

    // url을 통해 이미지파일인지 확인하는 메소드
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    // url을 통해 비디오파일인지 확인하는 메소드
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context)
                .load(data.get(position))
                .apply(new RequestOptions()
                        .override(140, 140)
                        .centerCrop()
//                        .dontAnimate()
                        .skipMemoryCache(true))
                .transition(withCrossFade())
                .into(holder.iv_thumbnail);
        if (isImageFile(data.get(position))) {
            holder.iv_type.setImageResource(R.drawable.ic_image);
        } else {
            holder.iv_type.setImageResource(R.drawable.ic_video);
        }

//        final int fipos = position;
//        holder.iv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                userInfoArrayList.remove(fipos);
//                notifyDataSetChanged();
//            }
//        });
//        holder.iv_thumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 클릭한 썸네일이 이미지인지 비디오인지 확인 후 이미지라면 이미지 뷰 위에 띄우고
//                // 비디오라면 비디오 뷰 위에 해당 파일을 띄운다.
//                // 이 때 framelayout의 특징을 이용한다.
//                if (isImageFile(userInfoArrayList.get(fipos))) {
//                    (Activity) context
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_thumbnail, iv_cancel, iv_type;


        public MyViewHolder(View v) {
            super(v);
            iv_cancel = v.findViewById(R.id.iv_cancel);
            iv_thumbnail = v.findViewById(R.id.iv_thumbnail);
            iv_type = v.findViewById(R.id.iv_type);

            iv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(getAdapterPosition());
//                        notifyDataSetChanged();//지워봄
                    if (getAdapterPosition() != RecyclerView.NO_POSITION)
                        notifyItemChanged(getAdapterPosition());
                    ((TransformationAdapter) ((UploadActivity) context).rv_transformations.getAdapter()).clear();

//                    if (last_pos == getAdapterPosition()) {
//                        if (!userInfoArrayList.isEmpty()) {
//                            last_pos = 0;
//
//                        } else {
//
//                        }
                    last_pos = -1;

                    if (video_view.isPlaying()) {
                        video_view.stopPlayback();
                    }
                    video_view.setVisibility(View.INVISIBLE);
                    image_view.setVisibility(View.INVISIBLE);
                }

//                }
            });
            // 썸네일 이미지를 클릭하면 상단 뷰에 해당 이미지나 비디오를 볼 수 있게 한다.
            iv_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (last_pos == getAdapterPosition()) {
                        return;
                    }
                    last_pos = getAdapterPosition();

                    if (video_view.isPlaying()) {
                        video_view.stopPlayback();
                        Log.e(TAG, "비디오 재생 중지");
//                        mediaController.hide();
//                        Log.e(TAG, "컨트롤러 숨겨짐");
                    }
                    if (isImageFile(data.get(getAdapterPosition()))) {

                        Glide.with(context)
                                .load(data.get(getAdapterPosition()))
                                .into(image_view);

                        ((TransformationAdapter) ((UploadActivity) context).rv_transformations.getAdapter()).setURL(data.get(getAdapterPosition()));

//                        Bitmap newBitmap = null;
//                        try {
//                            newBitmap = BitmapFilter.changeStyle(MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://"+userInfoArrayList.get(getAdapterPosition()))), BitmapFilter.GRAY_STYLE);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        image_view.setImageBitmap(newBitmap);

//                        video_view.setBackgroundColor(0xffffffff);
                        image_view.setVisibility(View.VISIBLE);
                        video_view.setVisibility(View.GONE);
                    } else {

                        ((TransformationAdapter) ((UploadActivity) context).rv_transformations.getAdapter()).clear();

                        // 비디오 썸네일 설정
                        video_view.setBackground(
                                new BitmapDrawable(ThumbnailUtils.createVideoThumbnail(data.get(getAdapterPosition())
                                        , MediaStore.Images.Thumbnails.MINI_KIND)
                                ));
                        // 다른 썸네일 지정 방법인데 이건 시작점 기준으로 썸네일 만드는 것이다.
//                        video_view.seekTo(100);

                        video_view.setVisibility(View.GONE);
                        // 썸네일 설정 이후
                        video_view.setZOrderOnTop(true);
//                        video_view.setZOrderOnTop(false);
                        image_view.setVisibility(View.GONE);
                        video_view.setVisibility(View.VISIBLE);
                        video_view.setVideoPath("file://" + data.get(getAdapterPosition()));

                        // 미디어 컨트롤러 보여주기
                        mediaController.show(0);
                        Log.e(TAG, "컨트롤러 켜짐");
//                        }

                    }
                }
            });


        }

        public ImageView getIv_thumbnail() {
            return iv_thumbnail;
        }
    }

}
