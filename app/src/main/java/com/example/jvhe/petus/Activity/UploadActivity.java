package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.example.jvhe.petus.Adapter.TransformationAdapter;
import com.example.jvhe.petus.Class.FileUploader;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.HttpAsyncPost;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.format;


public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UploadActivity";

    static final int OPEN_MEDIA_PICKER = 1;  // The request code

    ActionBar bar;

    Button select;

    ArrayList<String> data_arr = new ArrayList<>();
    RecyclerView rv_selected;
    public RecyclerView rv_transformations;
    SelectedMediaItemRecyclerAdapter selectedMediaItemRecyclerAdapter;

    ImageView image_view;
    VideoView video_view;

    ProgressDialog dialog;

    // 미디어 컨트롤러 설정 일단 임시로.
    MediaController mediaController;

    // 업로드 버튼
    Button action_upload;

    TextInputEditText et_title, et_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // 액션바 객체 정의
        bar = getSupportActionBar();

        // 액션바 속성 정의
        assert bar != null;
        bar.setDisplayShowTitleEnabled(true);   // 액션바 노출 유무
        bar.setTitle("업로드");   // 액션바 타이틀 라벨
        bar.setDisplayHomeAsUpEnabled(true);    // 뒤로가기 버튼 보이기

        select = findViewById(R.id.select);
        select.setOnClickListener(this);
//        select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Gallery.class);
//                // Set the title
//                intent.putExtra("title", "Select media");
//                // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
//                intent.putExtra("mode", 1);
//                intent.putExtra("maxSelection", 5); // Optional
//                startActivityForResult(intent, OPEN_MEDIA_PICKER);
//            }
//        });

//        til_title = findViewById(R.id.til_title);
//        til_title.setCounterEnabled(true);
//        til_title.setCounterMaxLength(100);

        image_view = findViewById(R.id.image_view);
        video_view = findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        video_view.setMediaController(mediaController);

        // 필터 관련 리사이클러뷰 선언
        rv_transformations = findViewById(R.id.rv_transformations);
        TransformationAdapter transformationAdapter = new TransformationAdapter(this, "", image_view);
        rv_transformations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_transformations.setAdapter(transformationAdapter);

        // 앨범에서 선택한 이미지 리사이클러뷰 선언
        rv_selected = findViewById(R.id.rv_selected);
        selectedMediaItemRecyclerAdapter = new SelectedMediaItemRecyclerAdapter(this, data_arr, image_view, video_view, mediaController);
        rv_selected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_selected.setAdapter(selectedMediaItemRecyclerAdapter);


        et_title = findViewById(R.id.et_title);
        et_desc = findViewById(R.id.et_desc);

        action_upload = findViewById(R.id.action_upload);
        action_upload.setOnClickListener(this);

    }

    public Bitmap createVideoThumbNail(String path) {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == OPEN_MEDIA_PICKER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                for (int i = 0; i < selectionResult.size(); i++) {
                    Log.e(TAG, selectionResult.get(i));
                }

                data_arr.clear();
                data_arr.addAll(selectionResult);
                selectedMediaItemRecyclerAdapter.notifyDataSetChanged();
//
//                File file = new File( data_arr.get(0));
////                int size = (int) file.length();
//                try {
//                    byte bytes[] = FileUtils.readFileToByteArray(file);
//                    Log.e(TAG,"파일 써짐");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                File file = new File(data_arr.get(0));
//                try {
//                    Log.e(TAG, "파일 써짐: " + FileUtils.readFileToString(file, "UTF-8"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                if (data_arr.size() > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                        rv_selected.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.iv_thumbnail).performClick();
                            final SelectedMediaItemRecyclerAdapter.MyViewHolder holder =
                                    (SelectedMediaItemRecyclerAdapter.MyViewHolder) rv_selected.findViewHolderForAdapterPosition(0);
                            holder.getIv_thumbnail().performClick();
                        }
                    }, 1);
                } else {
                    if (video_view.isPlaying()) {
                        video_view.stopPlayback();
                    }
                    video_view.setVisibility(View.INVISIBLE);
                    image_view.setVisibility(View.INVISIBLE);

                    selectedMediaItemRecyclerAdapter.last_pos = -1;
                }

            }
        }
    }

    // url을 통해 이미지파일인지 확인하는 메소드
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    // 액션바 뒤로가기 관련 설정
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select:
                Intent intent = new Intent(getApplicationContext(), Gallery.class);
                // Set the title
                intent.putExtra("title", "Select media");
                // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
                intent.putExtra("mode", 1);
                intent.putExtra("maxSelection", 10); // Optional
                startActivityForResult(intent, OPEN_MEDIA_PICKER);
                break;
            case R.id.action_upload:
                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                if (et_title.length() == 0) {
                    et_title.startAnimation(shake);
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else if (et_desc.length() == 0) {
                    et_desc.startAnimation(shake);
                    Toast.makeText(getApplicationContext(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();

                } else {

                    final String url = StaticData.url + "upload_post.php";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    @SuppressLint("StaticFieldLeak") AsyncTask asyncTask = new AsyncTask<Object, Integer, Void>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
//                            dialog = ProgressDialog.show(UploadActivity.this, "게시물 업로드", "업로드 중 입니다.", true, true);
                            dialog = new ProgressDialog(UploadActivity.this);
                            dialog.setTitle("게시물 업로드");
                            dialog.setMessage("업로드 중입니다");
                            dialog.setIndeterminate(false);
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            dialog.setProgress(0);
                            dialog.show();
                        }

                        @Override
                        protected Void doInBackground(Object[] objects) {
                            try {
                                FileUploader fileUploader = new FileUploader(url);
                                fileUploader.addFormField("title", et_title.getText().toString());
                                fileUploader.addFormField("desc", et_desc.getText().toString());
                                SharedPreferences pref = getSharedPreferences("current_user", 0);
                                fileUploader.addFormField("user_id", "" + pref.getInt("id_db", -100));

                                // 파일 가져오기
                                File[] uploadFileArray = new File[data_arr.size()];
                                for (int i = 0; i < data_arr.size(); i++) {
                                    uploadFileArray[i] = new File(data_arr.get(i));
                                }

                                for (int i = 0; i < data_arr.size(); i++) {
//                                    dialog.setProgress(i*100/data_arr.size());
//                                    dialog.setMessage("파일 업로드중... ( " + i + " / " + data_arr.size() + " )");
                                    fileUploader.addFilePart("uploaded_file[]", uploadFileArray[i], dialog);
                                    Log.e(TAG, "파일 업로드함 " + data_arr.get(i));
                                }
                                Log.e(TAG, "체크1");
                                publishProgress(100);
                                Log.e(TAG, "체크2");
                                List<String> response = fileUploader.finish();

                                Log.e(TAG, "체크3");
                                System.out.println("SERVER REPLIED:");

                                Log.e(TAG, "체크4");
                                for (String line : response) {
//                                    Log.e(TAG, line);
                                    System.out.println(line);
                                }

                                Log.e(TAG, "체크5");
                                dialog.dismiss();
                                finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(Integer... progress) {
                            dialog.setProgress(progress[0]);
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    asyncTask.execute();
//                    String data = "title=" + et_title.getText().toString() + "&desc=" + et_desc.getText().toString();
//                    String twoHyphens = "--";
////            String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
//                    String lineEnd = "\r\n";
//
//                    String boundary = "*****";
//                    for (int i = 0; i < data_arr.size(); i++) {
//
//                        File file = new File(data_arr.get(i));
//                        try {
////                            data += "&file" + i + "=" + FileUtils.readFileToString(file);
//                            data += twoHyphens + boundary + lineEnd;
//                            data += "Content-Disposition: form-data; name=\"file" + i + "\"; filename=\"file" + i + "." + getExtension(data_arr.get(i)) + "\"" + lineEnd;
//                            data += "Content-Type: text/plain" + lineEnd;
//                            data += lineEnd;
////                            data += URLEncoder.encode(FileUtils.readFileToString(file), "UTF-8");
//                            data += FileUtils.readFileToString(file);
//                            data += lineEnd;
//                            Log.e(TAG, "파일 써짐");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    data += twoHyphens + boundary + twoHyphens + lineEnd;
//                    Log.e(TAG, "데이터: " + data);
//                    String url = StaticData.url + "upload_post.php";
//                    HttpAsyncPost httpAsyncPost = new HttpAsyncPost(data, url, handler);
//                    httpAsyncPost.execute();
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "업로드 실패.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "결과 " + data, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    /**
     * 파일 확장자 가져오기
     *
     * @param fileStr 경로나 파일이름
     * @return
     */
    public static String getExtension(String fileStr) {
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }

    /**
     * 파일 이름 가져오기
     *
     * @param fileStr     파일 경로
     * @param isExtension 확장자 포함 여부
     * @return
     */
    public static String getFileName(String fileStr, boolean isExtension) {
        String fileName = null;
        if (isExtension) {
            fileName = fileStr.substring(fileStr.lastIndexOf("/"), fileStr.lastIndexOf("."));
        } else {
            fileName = fileStr.substring(fileStr.lastIndexOf("/") + 1);
        }
        return fileName;
    }
    public class SelectedMediaItemRecyclerAdapter extends RecyclerView.Adapter<SelectedMediaItemRecyclerAdapter.MyViewHolder> {

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
        public boolean isImageFile(String path) {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("image");
        }

        // url을 통해 비디오파일인지 확인하는 메소드
        public boolean isVideoFile(String path) {
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

}
