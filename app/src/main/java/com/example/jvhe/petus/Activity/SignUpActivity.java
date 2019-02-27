package com.example.jvhe.petus.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.example.jvhe.petus.Adapter.TransformationAdapter;
import com.example.jvhe.petus.Class.FileUploader;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.R;
import com.example.jvhe.petus.Class.StaticData;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.Ragnarok.BitmapFilter;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    Uri mImageCaptureUri = null;
    static String uploadImageFilePath = "";


    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수


    //사진 선택하기 버튼을 눌렀을 경우 카메라에서 가져온다면 PICK_FROM_CAMERA, 앨범에서 가져온다면 PICK_FROM_ALBUM
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
//    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수

    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름


    public RecyclerView rv_transformations;
    TransformationAdapter transformationAdapter;

    ImageView iv_profile;


    // 이메일 중복체크, 닉네임 중복체크 여부 확인 변수
    boolean flag_duplication_check_email, flag_duplication_check_name;


    // 버튼 변수 선언
    // btn_duplication_check_email: 이메일 중복 체크 버튼
    // btn_duplication_check_nickname: 닉네임 중복 체크 버튼
    // btn_register: 회원가입 버튼
    // btn_image_upload: 이미지 업로드 버튼
    // btn_cancel: 이미지 선택 취소 버튼
    Button btn_duplication_check_email, btn_duplication_check_name, btn_register, btn_image_upload, btn_cancel;

    // 텍스트 입력칸 변수 선언
    // 단축어 설명 - et: edit text
    // et_email: email id 입력란
    // et_password: password 입력란
    // et_nickname: nickname 입력란
    TextInputEditText et_email, et_password, et_name;

    // 비밀번호 보기 체크박스, 비밀번호 보기 텍스트뷰 선언
    CheckBox chk_show_password;
    TextView tv_show_password;

    // 이메일과 닉네임을 감싸는 레이아웃
    LinearLayout layout_nickname, layout_email;

    // 알림창 생성 클래스와 알림창 클래스. 텍스트 입력 도중 어떤 사유로 restart 되었을 때 알림을 통해서 이전에 작성한 내용을 이어서 작성할지 말지 물어본다.
    AlertDialog.Builder alert_builder;

    //    버튼 처리를 담당하는 BtnOnClickListener 클래스 선언
    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String data;
            String url = StaticData.url + "sign_up_android.php";
            HttpAsync httpasync;
            switch (view.getId()) {
                // 이미지 업로드 버튼
                case R.id.btn_image_upload:
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(SignUpActivity.this);
//
//                    AlertDialog.Builder alert_load = new AlertDialog.Builder(SignUpActivity.this);
//                    alert_load.setTitle("업로드할 이미지 선택")
//                            .setPositiveButton("사진 촬영", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
////                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//////                                    startActivityForResult(intent, PICK_FROM_CAMERA);
////                                    if (intent.resolveActivity(getPackageManager()) != null) {
//////                                        mImageCaptureUri = Uri.parse("file:///sdcard/temp_photo.jpg");
//////                                        mImageCaptureUri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/temp_photo.jpg");
//////                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
////                                        startActivityForResult(intent, PICK_FROM_CAMERA);
////
////                                    }
//                                    selectPhoto();
//                                }
//                            })
//                            .setNeutralButton("취소", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
////                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                                    String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
////                                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
////                                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
////                                    startActivityForResult(intent, 3);
//                                }
//                            })
//                            .setNegativeButton("앨범 선택", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(getApplicationContext(), Gallery.class);
//                                    // Set the title
//                                    intent.putExtra("title", "프로필 이미지 선택");
//                                    // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
//                                    intent.putExtra("mode", 2);
//                                    intent.putExtra("maxSelection", 1); // Optional
//                                    startActivityForResult(intent, PICK_FROM_ALBUM);
//                                }
//                            });
//                    AlertDialog alert = alert_load.create();
//                    alert.show();
                    break;
                case R.id.btn_cancel:
                    currentPhotoPath = "";
                    uploadImageFilePath = "";
                    transformationAdapter.clear();
                    iv_profile.setImageResource(R.drawable.profile);
                    btn_cancel.setVisibility(View.GONE);
                    break;
                // 이메일 중복 체크
                case R.id.btn_duplication_check_email:
                    data = "email=" + et_email.getText().toString() + "&type=duplication_check_email";
                    httpasync = new HttpAsync(data, url, handler);
                    httpasync.execute();
                    break;
                // 이름 중복 체크
                case R.id.btn_duplication_check_name:
                    data = "name=" + et_name.getText().toString() + "&type=duplication_check_name";
                    url = StaticData.url + "sign_up_android.php";
                    httpasync = new HttpAsync(data, url, handler);
                    httpasync.execute();
                    break;
                // 회원 가입 버튼
                case R.id.btn_register:
                    // 입력 조건 확인
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    if (et_email.length() == 0) {
                        et_email.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (!flag_duplication_check_email) {
                        btn_duplication_check_email.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "아이디 중복체크를 해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (et_password.length() == 0) {
                        et_password.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (et_name.length() == 0) {
                        et_name.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (!flag_duplication_check_name) {
                        btn_duplication_check_name.startAnimation(shake);
                        Toast.makeText(getApplicationContext(), "닉네임 중복체크를 해 주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (uploadImageFilePath.equals("")) {
                        Toast.makeText(getApplicationContext(), "가입 조건 성공 " + et_email.getText().toString() + et_password.getText().toString() + et_name.getText().toString(), Toast.LENGTH_SHORT).show();
                        data = "email=" + et_email.getText().toString() + "&password=" + et_password.getText().toString() + "&name=" + et_name.getText().toString() + "&type=register";
                        url = StaticData.url + "sign_up_android.php";
                        httpasync = new HttpAsync(data, url, handler);
                        httpasync.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "가입 조건 성공 (이미지 추가) " + et_email.getText().toString() + et_password.getText().toString() + et_name.getText().toString(), Toast.LENGTH_SHORT).show();
                        url = StaticData.url + "sign_up_android.php";
                        final String finalUrl = url;
                        Glide.with(SignUpActivity.this)
                                .asBitmap()
                                .load(uploadImageFilePath)
                                .apply(StaticData.requestOptions)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        saveBitmaptoJpeg(BitmapFilter.changeStyle(resource, transformationAdapter.getSelected_index()), "path", "temp_profile");
                                        Thread thread = new Thread(new Runnable() {
                                            Handler handler = new Handler();

                                            @Override
                                            public void run() {

                                                try {
                                                    FileUploader fileUploader = new FileUploader(finalUrl);
                                                    fileUploader.addFormField("email", et_email.getText().toString());
                                                    fileUploader.addFormField("password", et_password.getText().toString());
                                                    fileUploader.addFormField("name", et_name.getText().toString());
                                                    fileUploader.addFormField("type", "register_with_profile");
                                                    fileUploader.addFilePart("profile_image", new File(uploadImageFilePath));

                                                    final List<String> response = fileUploader.finish();
                                                    StringBuilder data = new StringBuilder();
                                                    System.out.println("SERVER REPLIED:");

                                                    for (String line : response) {
                                                        System.out.println(line);
                                                        data.append(line);
                                                    }
                                                    final String data2 = data.toString();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (data2.contains("!!register!!")) {
                                                                if (data2.contains("success")) {
                                                                    Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                                                                    alert_builder = new AlertDialog.Builder(SignUpActivity.this);
                                                                    alert_builder.setTitle("회원가입 완료!")
                                                                            .setCancelable(false)
                                                                            .setMessage("회원 가입이 완료되었습니다. 가입된 아이디와 비밀번호로 로그인 하시겠습니까?")
                                                                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    Intent data = new Intent();
                                                                                    data.putExtra("flag_login", true);
                                                                                    data.putExtra("email", et_email.getText().toString());
                                                                                    data.putExtra("password", et_password.getText().toString());
                                                                                    setResult(RESULT_OK, data);
                                                                                    finish();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                    Intent data = new Intent();
                                                                                    data.putExtra("flag_login", false);
                                                                                    setResult(RESULT_OK, data);
                                                                                    finish();
                                                                                }
                                                                            });
                                                                    alert_builder.create().show();
                                                                } else {

                                                                }
                                                            }
                                                        }
                                                    });
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        thread.start();

                                    }
                                });
                    }

                    break;
            }
        }
    }

    // 체크박스 처리를 담당하는 ChkOnClickListener 클래스 선언
    class ChkOnClickListener implements CheckBox.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // 비밀번호 보기 체크박스
                case R.id.chk_show_password:
                    // 체크박스가 체크되어 있을 경우 입력한 비밀번호를 볼 수 있다.
                    if (((CheckBox) view).isChecked()) {
                        et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                    // 체크박스가 체크되어 있지 않을 경우 패스워드 입력칸의 비밀번호를 볼 수 없다.
                    else {
                        et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

//        checkPermissions();

        // BtnOnClickListener 객체 생성
        BtnOnClickListener btnOnClickListener = new BtnOnClickListener();
        // ChkOnClickListener 객체 생성
        ChkOnClickListener chkOnClickListener = new ChkOnClickListener();

        // 버튼들과 레이아웃 매칭 그리고 리스너 설정
        btn_duplication_check_email = (Button) findViewById(R.id.btn_duplication_check_email);
        btn_duplication_check_email.setOnClickListener(btnOnClickListener);
        btn_duplication_check_name = (Button) findViewById(R.id.btn_duplication_check_name);
        btn_duplication_check_name.setOnClickListener(btnOnClickListener);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(btnOnClickListener);
        btn_image_upload = findViewById(R.id.btn_image_upload);
        btn_image_upload.setOnClickListener(btnOnClickListener);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(btnOnClickListener);
        btn_cancel.setVisibility(View.GONE);

        iv_profile = findViewById(R.id.iv_profile);

        // email, password, nickname 입력란 레이아웃 매칭
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_password = (TextInputEditText) findViewById(R.id.et_password);
        et_name = (TextInputEditText) findViewById(R.id.et_name);


        // 체크박스와 텍스트뷰 매칭
        chk_show_password = (CheckBox) findViewById(R.id.chk_show_password);
        tv_show_password = (TextView) findViewById(R.id.tv_show_password);
        // 체크박스 리스너 설정
        chk_show_password.setOnClickListener(chkOnClickListener);

        // 이메일 중복체크, 닉네임 중복체크 초기화
        flag_duplication_check_email = false;
        flag_duplication_check_name = false;

        // 이메일과 닉네임 감싸는 레이아웃 매칭
        layout_email = findViewById(R.id.layout_email);
        layout_nickname = findViewById(R.id.layout_name);

        // 필터 관련 리사이클러뷰 선언
        rv_transformations = findViewById(R.id.rv_transformations);
        transformationAdapter = new TransformationAdapter(this, "", iv_profile);
        rv_transformations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_transformations.setAdapter(transformationAdapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//
//        // 이메일과 패스워드, 닉네임 입력창에 내용이 하나라도 있을 경우 이어서 작성할 것인지 묻는 알림창을 띄우게 된다.
//        if (!(et_email == null && et_password == null && et_name == null)) {
//            // 다른 액티비티에 있다 올 경우 입력중이던 정보를 계속 입력할지 확인해 주는 알림
//            alert_builder = new AlertDialog.Builder(SignUpActivity.this);
//            alert_builder.setTitle("작성중이던 정보가 있습니다. 불러오시겠습니까?");
//            // 예일 경우 이전에 기입한 내용 그대로 진행한다.
//            alert_builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    return;
//                }
//            });
//            // 아니오일 경우 작성중이던 내용을 모두 지운다.
//            alert_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    layout_email.setBackgroundColor(0xffffff);
//                    et_email.setText("");
//                    et_email.setEnabled(true);
//                    btn_duplication_check_email.setEnabled(true);
//                    et_password.setText("");
//                    layout_nickname.setBackgroundColor(0xffffff);
//                    et_name.setText("");
//                    et_name.setEnabled(true);
//                    btn_duplication_check_name.setEnabled(true);
//                    flag_duplication_check_email = false;
//                    flag_duplication_check_name = false;
//                }
//            });
//            alert_builder.create().show();
//        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                // 이메일 중복체크 반응
                if (data.contains("!!duplication_check_email!!")) {
                    if (data.contains("impossible")) {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this).
                                setMessage("이메일이 중복되었습니다. 다른 이메일을 입력해 주세요.").setPositiveButton("확인", null);
                        alert_builder.create().show();
                    } else {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this);
                        alert_builder.setMessage("사용 가능한 이메일 주소입니다. 이 주소를 사용하시겠습니까?\n* 확인을 누르면 더 이상 변경할 수 없습니다.");
                        alert_builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                layout_email.setBackgroundColor(0x6b70f3ff);
                                et_email.setEnabled(false);
                                flag_duplication_check_email = true;
                                btn_duplication_check_email.setEnabled(false);
                            }
                        });
                        alert_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        alert_builder.create().show();
                    }
                }
                // 닉네임 중복체크 반응
                else if (data.contains("!!duplication_check_name!!")) {
                    if (data.contains("impossible")) {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this).
                                setMessage("이름이 중복되었습니다. 다른 이메일을 입력해 주세요.").setPositiveButton("확인", null);
                        alert_builder.create().show();
                    } else {
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this);
                        alert_builder.setMessage("사용 가능한 이름입니다. 이 이름을 사용하시겠습니까?\n* 확인을 누르면 더 이상 변경할 수 없습니다.");
                        alert_builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                layout_nickname.setBackgroundColor(0x6b70f3ff);
                                et_name.setEnabled(false);
                                flag_duplication_check_name = true;
                                btn_duplication_check_name.setEnabled(false);
                            }
                        });
                        alert_builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        alert_builder.create().show();
                    }
                }
                // 회원가입 실행
                else if (data.contains("!!register!!")) {
                    if (data.contains("success")) {
                        Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                        alert_builder = new AlertDialog.Builder(SignUpActivity.this);
                        alert_builder.setTitle("회원가입 완료!")
                                .setCancelable(false)
                                .setMessage("회원 가입이 완료되었습니다. 가입된 아이디와 비밀번호로 로그인 하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent data = new Intent();
                                        data.putExtra("flag_login", true);
                                        data.putExtra("email", et_email.getText().toString());
                                        data.putExtra("password", et_password.getText().toString());
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent data = new Intent();
                                        data.putExtra("flag_login", false);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                });
                        alert_builder.create().show();
                    } else {

                    }
                }


//
//                String data_arr = msg.getData().getString("data_arr");
//                Log.e("!!!!!!!!!!!!!", data_arr);
//                if (data_arr.contains("!!fail!!")) {
////                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(data_arr);
//                        SharedPreferences pref = getSharedPreferences("current_user", 0);
//                        SharedPreferences.Editor edit = pref.edit();
//                        edit.putString("id_db", obj.getString("id"));
//                        edit.putString("email", obj.getString("email"));
//                        edit.putString("name", obj.getString("name"));
//                        edit.putString("password", obj.getString("password"));
//                        edit.commit();
//
////                        deleteDatabase(MySQLiteOpenHelper.DBFILE_NAME);
////                        MySQLiteOpenHelper mydbHelper = new MySQLiteOpenHelper(getApplicationContext());
////                        mydbHelper.syncRecord( obj.getString("primarykey") );
//
////                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        Toast.makeText(getApplicationContext(),"결과 "+data_arr,Toast.LENGTH_SHORT).show();
//                        finish();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }


            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.e(TAG, "1111111111111");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.e(TAG, "22222222222  " + resultCode);

            if (resultCode == RESULT_OK) {
                Log.e(TAG, "3333333333333");
                Uri resultUri = result.getUri();
                Log.e(TAG, "44444444444");
                currentPhotoPath = resultUri.toString();
                uploadImageFilePath = currentPhotoPath;
                Log.e(TAG, "5555555555");
                Glide.with(this)
                        .load(resultUri)
                        .into(iv_profile);

                btn_cancel.setVisibility(View.VISIBLE);

                ((TransformationAdapter) rv_transformations.getAdapter()).setURL(currentPhotoPath);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "6666666666666");
                Exception error = result.getError();
                error.printStackTrace();
            }
            Log.e(TAG, "77777777777777");
        }


        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_CAMERA:
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                image.setImageBitmap(photo);
//                image_string = getBase64String(photo);
//                Uri uri = data.getData();
//                Log.e(TAG, "uri: " + uri);
//                Log.e(TAG, "mImageCaptureUri: " + mImageCaptureUri);
//                Log.e(TAG, "data: " + data);
//                Glide.with(this)
//                        .load(mImageCaptureUri)
//                        .into(iv_profile);
//                Bundle extras = data.getExtras();
//                Log.e(TAG, "extras: " + extras.get("data"));
//                File file = new File(Environment.getExternalStorageDirectory().getPath(), "/temp_photo.jpg");
//                Uri uri = Uri.fromFile(file);
//                Bitmap bitmap;
//                Log.e(TAG, "uri: " + uri);
//                Log.e(TAG, "mImageCaptureUri: " + mImageCaptureUri);
//                Log.e(TAG, "data: " + data);
//                Glide.with(this)
//                        .load(uri)
//                        .into(iv_profile);
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
////                    bitmap = crupAndScale(bitmap, 300); // if you mind scaling
//                    iv_profile.setImageBitmap(bitmap);
//
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

//                Glide.with(this)
//                        .load(currentPhotoPath)
//                        .into(iv_profile);

                Log.e(TAG, "크롭 시작 전");
                CropImage.activity(Uri.parse(currentPhotoPath))
                        .start(this);
                Log.e(TAG, "크롭 시작 후");

//                getPictureForPhoto();

//                cropImage();

                break;
            case PICK_FROM_ALBUM:
//                Bitmap photo = null;
//                try {
//                    photo = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                image.setImageBitmap(photo);
//                image_string = getBase64String(photo);


                currentPhotoPath = data.getStringArrayListExtra("result").get(0);

                // start cropping activity for pre-acquired image saved on the device

                Log.e(TAG, "크롭 시작 전");
                CropImage.activity(Uri.parse(currentPhotoPath))
                        .start(this);
                Log.e(TAG, "크롭 시작 후");

//                Glide.with(this)
//                        .load(currentPhotoPath)
//                        .into(iv_profile);


//                cropImage();

                break;
//            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
//
//
//                Log.e(TAG, "크롭 result시작");
//
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                Uri resultUri = result.getUri();
//
//                currentPhotoPath = resultUri.toString();
//
//                Glide.with(this)
//                        .load(resultUri)
//                        .into(iv_profile);
//                break;
//            case CROP_FROM_CAMERA: {
////                image.setImageURI(mImageCaptureUri);
////                Toast.makeText(getApplicationContext(), "사진가져옴 " + mImageCaptureUri, Toast.LENGTH_SHORT).show();
//                try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.
//
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
//                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
//                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                    thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축
//
//
//                    //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.
//
//                    iv_profile.setImageBitmap(thumbImage);
//                } catch (Exception e) {
//                    Log.e("ERROR", e.getMessage().toString());
//                }
//
//            }
//            break;
        }
    }

    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
//                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Log.e(TAG, "패키지 이름: " + getPackageName());
                    photoUri = FileProvider.getUriForFile(this, "com.example.jvhe.petus.fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }

        }
    }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();


        return storageDir;

    }

    //비트맵을 jpg로
    public static void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + foler_name;
        uploadImageFilePath = string_path + file_name;


        File file_path;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path + file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
    }


//    private void getPictureForPhoto() {
//        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(currentPhotoPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int exifOrientation;
//        int exifDegree;
//
//        if (exif != null) {
//            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            exifDegree = exifOrientationToDegrees(exifOrientation);
//        } else {
//            exifDegree = 0;
//        }
//        iv_profile.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
//    }
//
//    private int exifOrientationToDegrees(int exifOrientation) {
//        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            return 90;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            return 180;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            return 270;
//        }
//        return 0;
//    }
//
//    private Bitmap rotate(Bitmap src, float degree) {
//
//// Matrix 객체 생성
//        Matrix matrix = new Matrix();
//// 회전 각도 셋팅
//        matrix.postRotate(degree);
//// 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
//        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
//                src.getHeight(), matrix, true);
//    }

    //    public static  Bitmap crupAndScale (Bitmap source,int scale){
//        int factor = source.getHeight() <= source.getWidth() ? source.getHeight(): source.getWidth();
//        int longer = source.getHeight() >= source.getWidth() ? source.getHeight(): source.getWidth();
//        int x = source.getHeight() >= source.getWidth() ?0:(longer-factor)/2;
//        int y = source.getHeight() <= source.getWidth() ?0:(longer-factor)/2;
//        source = Bitmap.createBitmap(source, x, y, factor, factor);
//        source = Bitmap.createScaledBitmap(source, scale, scale, false);
//        return source;
//    }


//
//    public void cropImage() {
//        this.grantUriPermission("com.android.camera", photoUri,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(photoUri, "image/*");
//
//        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
//        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        int size = list.size();
//        if (size == 0) {
//            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 4);
//            intent.putExtra("aspectY", 3);
//            intent.putExtra("scale", true);
//            File croppedFileName = null;
//            try {
//                croppedFileName = createImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
//            File tempFile = new File(folder.toString(), croppedFileName.getName());
//
//            photoUri = FileProvider.getUriForFile(this,
//                    getPackageName()+".fileprovider", tempFile);
//
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//
//            intent.putExtra("return-data", false);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행
//
//            Intent i = new Intent(intent);
//            ResolveInfo res = list.get(0);
//            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            grantUriPermission(res.activityInfo.packageName, photoUri,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            startActivityForResult(i, CROP_FROM_CAMERA);
//
//
//        }
//
//    }

//
//    private boolean checkPermissions() {
//        int result;
//        List<String> permissionList = new ArrayList<>();
//        for (String pm : permissions) {
//            result = ContextCompat.checkSelfPermission(this, pm);
//            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
//                permissionList.add(pm);
//            }
//        }
//        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
//            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
//            return false;
//        }
//        return true;
//    }
//
//    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
////권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MULTIPLE_PERMISSIONS: {
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < permissions.length; i++) {
//                        if (permissions[i].equals(this.permissions[0])) {
//                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                                showNoPermissionToastAndFinish();
//                            }
//                        } else if (permissions[i].equals(this.permissions[1])) {
//                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                                showNoPermissionToastAndFinish();
//
//                            }
//                        } else if (permissions[i].equals(this.permissions[2])) {
//                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                                showNoPermissionToastAndFinish();
//
//                            }
//                        }
//                    }
//                } else {
//                    showNoPermissionToastAndFinish();
//                }
//                return;
//            }
//        }
//    }
//
//
//    //권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며 해당 Activity를 종료시킵니다.
//    private void showNoPermissionToastAndFinish() {
//        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
//        finish();
//    }
}
