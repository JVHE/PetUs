package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.example.jvhe.petus.Adapter.ChatRecyclerAdapter;
import com.example.jvhe.petus.Class.ChatItem;
import com.example.jvhe.petus.Class.FileUploader;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChatRoomActivity";
    private static final int SHARP_SEARCH = 300;
    private static final int OPEN_MEDIA_PICKER = 201;  // The request code

    //    ChatClient chatClient;
    ChatRecyclerAdapter chatRecyclerAdapter;
    ArrayList<ChatItem> chatItems;
    RecyclerView rv_msg;


    // 검색탭 리니어 레이아웃 두가지 선언
    // 하나는 일반 메세지 보내기, 나머지 하나는 샵검색
    LinearLayout ll_msg, ll_search_sharp;
    TextView tv_action_sharp;
    ImageView iv_send_msg, iv_search_sharp, iv_cancel, iv_cancel_sharp, iv_add;
    EditText et_msg, et_keyword_sharp;

    // 이미지, 비디오 업로드 관련 리사이클러뷰, 어댑터 선언
    ArrayList<String> selected_media_arr = new ArrayList<>();
    SelectedMediaItemRecyclerAdapter selectedMediaItemRecyclerAdapter = new SelectedMediaItemRecyclerAdapter();
    RecyclerView rv_selected;


    SharedPreferences sharedPreferences;
    ChatAsync chatAsync;
    LinearLayoutManager linearLayoutManager;


    Button btn_exit;

    int user_id;    // 유저 id
    String is_private_or_group;    // 채팅 타입 private:갠톡, group: 단톡
    int group_or_user_id;   // 그룹 or 유저 id
    UserInfo user_info_target; // 원하는 타겟의 유저정보

    String name_or_title = "";

    ActionBar bar;

    // 키보드 내리기
    InputMethodManager imm;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        sharedPreferences = getSharedPreferences("current_user", MODE_PRIVATE);

        chatItems = new ArrayList<>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(getApplicationContext(), chatItems);
        rv_msg = findViewById(R.id.rv_msg);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv_msg.setLayoutManager(linearLayoutManager);
        rv_msg.setAdapter(chatRecyclerAdapter);

        // 선택된 이미지 리사이클러 관련 설정
        rv_selected = findViewById(R.id.rv_selected);
        rv_selected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_selected.setAdapter(selectedMediaItemRecyclerAdapter);

        iv_send_msg = findViewById(R.id.iv_send_msg);
        iv_send_msg.setOnClickListener(this);
        iv_send_msg.setVisibility(View.GONE);
        iv_search_sharp = findViewById(R.id.iv_search_sharp);
        iv_search_sharp.setOnClickListener(this);
        ll_msg = findViewById(R.id.ll_msg);
        ll_search_sharp = findViewById(R.id.ll_search_sharp);
        ll_search_sharp.setVisibility(View.GONE);
        tv_action_sharp = findViewById(R.id.tv_action_sharp);
        tv_action_sharp.setOnClickListener(this);
        iv_cancel = findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(this);
        iv_cancel.setVisibility(View.GONE);
        iv_add = findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);

        iv_cancel_sharp = findViewById(R.id.iv_cancel_sharp);
        iv_cancel_sharp.setOnClickListener(this);

        // 키보드 내리기
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        et_msg = findViewById(R.id.et_chat);
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력된 텍스트가 없을 때
                if (s.length() == 0) {
                    iv_send_msg.setVisibility(View.GONE);
                    tv_action_sharp.setVisibility(View.VISIBLE);
                }
                // 입력된 텍스트 길이가 0이 아닐 때
                else {
                    iv_send_msg.setVisibility(View.VISIBLE);
                    tv_action_sharp.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때
            }
        });
        et_keyword_sharp = findViewById(R.id.et_keyword_sharp);
        et_keyword_sharp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력된 텍스트가 없을 때
                if (s.length() == 0) {
                    iv_search_sharp.setVisibility(View.GONE);
                }
                // 입력된 텍스트 길이가 0이 아닐 때
                else {
                    iv_search_sharp.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때
            }
        });
        // 포커스가 오프되면 샵검색창을 다 숨긴다.
        et_keyword_sharp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    iv_cancel_sharp.performClick();
                    Toast.makeText(getApplicationContext(), "포커스 없음", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "포커스 있음", Toast.LENGTH_SHORT).show();
                }
            }
        });

        user_id = getSharedPreferences("current_user", MODE_PRIVATE).getInt("id_db", -100);

// 액션바 객체 정의
        bar = getSupportActionBar();

        // 액션바 속성 정의
        assert bar != null;
        bar.setDisplayShowTitleEnabled(true);   // 액션바 노출 유무

        // 인텐트를 이용하여 정보 받아오기
        // 인텐트에는 갠톡or단톡 유무와 방or유저번호를 받아온다
        Intent intent = getIntent();
        is_private_or_group = intent.getStringExtra("is_private_or_group");
        if (is_private_or_group.equals("private")) {
//            user_info_target = (UserInfo) getIntent().getSerializableExtra("user_info_target");
//            group_or_user_id = user_info_target.getUser_id();
//            bar.setTitle(user_info_target.getName());   // 액션바 타이틀 라벨

            group_or_user_id = intent.getIntExtra("group_or_user_id", -101);
            name_or_title = intent.getStringExtra("name_or_title");
            bar.setTitle(name_or_title);

        } else if (is_private_or_group.equals("group")) {
            group_or_user_id = intent.getIntExtra("group_or_user_id", -101);
            name_or_title = intent.getStringExtra("name_or_title");
            bar.setTitle(name_or_title);
        } else {
            Toast.makeText(this, "잘못된 호출. 액티비티 종료", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "잘못된 호출, 액티비티 종료. is_private_or_group: " + is_private_or_group);
            finish();
        }
        Log.e(TAG, "is_private_or_group: " + is_private_or_group);
        Log.e(TAG, "group_or_user_id: " + group_or_user_id);
        Log.e(TAG, "name_or_title: " + name_or_title);
//        btn_exit = findViewById(R.id.btn_exit);
//        btn_exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HttpAsync httpAsync = new HttpAsync("user_id="+user_id
//                +"$is_private_or_group="+is_private_or_group
//                +"$group_or_user_id="+group_or_user_id
//                ,StaticData.url+"exit_room.php"
//                ,handler_room_exit);/
//                httpAsync.execute();
//            }
//        });

//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//        Log.e(TAG, "^^^^^^^^^^^^^^^^^^^^^^^^^");
//        chatClient = new ChatClient(sharedPreferences.getInt("id_db", -100), StaticData.chat_url);
//        Log.e(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//        chatClient.start();
//        Log.e(TAG, "***********************");
//            }
//        });
//        t.start();


//        chat_test = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                chatClient = new ChatClient(sharedPreferences.getInt("id_db", -100), StaticData.chat_url);
//                return null;
//
//            }
//
//            public void send(String msg) {
//                chatClient.sendMsg(msg);
//            }
//        };
//        chat_test.execute();


        // 채팅 기록 불러오기
        HttpAsync httpAsync = new HttpAsync("is_private_or_group=" + is_private_or_group
                + "&user_id=" + user_id
                + "&group_or_user_id=" + group_or_user_id
                + "&last_chat_id=0"
                , StaticData.url + "request_chat_history.php"
                , handler_chat_history);
        httpAsync.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SHARP_SEARCH) {
                // 샵검색 공유 결과에 따른 행동 선언 필요
            } else if (requestCode == OPEN_MEDIA_PICKER) {
                // 이미지 선택 결과에 따른 메소드
                ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                for (int i = 0; i < selectionResult.size(); i++) {
                    Log.e(TAG, selectionResult.get(i));
                }

                selected_media_arr.clear();
                selected_media_arr.addAll(selectionResult);
                selectedMediaItemRecyclerAdapter.notifyDataSetChanged();

                // x버튼 활성화, 샵검색 숨기기, 미디어 버튼 숨기기, 보내기 버튼 보여지게
                iv_cancel.setVisibility(View.VISIBLE);
                iv_add.setVisibility(View.GONE);
                tv_action_sharp.setVisibility(View.GONE);
                iv_send_msg.setVisibility(View.VISIBLE);


                if (selected_media_arr.size() == 0) {
                    selectedMediaItemRecyclerAdapter.last_pos = -1;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_send_msg:

                // 이미지, 동영상 관련 전송 메소드 선언 필요
                // 이미지나 동영상이 선택되었다면 해당 이미지나 동영상들을 전부 http방식을 통해 서버로 업로드한다.
                // onActivityResult에서 업로드 후 받아온 url 리스트를 통해 sendImg를 한다.
                // 해당 작업들을 끝내고 et_msg에 입력된 값을 send_msg에 보낸다.
                // 구현 순서는 먼저 여러개 이미지 받아와서 필터링 거치고 체크버튼 눌러서 확인? 아무튼 그렇게 해서 웹서버로 전부 보낸다.
                if (selected_media_arr.size() != 0) {
                    final Handler handler = new Handler();
                    final String url = StaticData.url + "upload_media.php";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    @SuppressLint("StaticFieldLeak") AsyncTask asyncTask = new AsyncTask<Object, Integer, Void>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected Void doInBackground(Object[] objects) {
                            try {
                                Log.e(TAG, "321321");
                                FileUploader fileUploader = new FileUploader(url);


                                Log.e(TAG, "111");
                                // 파일 가져오기
                                File[] uploadFileArray = new File[selected_media_arr.size()];
                                for (int i = 0; i < selected_media_arr.size(); i++) {
                                    uploadFileArray[i] = new File(selected_media_arr.get(i));
                                    Log.e(TAG, "22   " + i);
                                }

                                Log.e(TAG, "3333");
                                for (int i = 0; i < selected_media_arr.size(); i++) {
                                    fileUploader.addFilePart("uploaded_file[]", uploadFileArray[i]);
                                    Log.e(TAG, "파일 업로드함 " + selected_media_arr.get(i));
                                }
                                publishProgress(100);
                                List<String> response = fileUploader.finish();

                                System.out.println("SERVER REPLIED:");

                                for (String line : response) {
//                                    Log.e(TAG, line);
                                    System.out.println(line);
                                    if (!line.contains("!!fail!!"))
                                        chatAsync.sendMedia(line);
                                }
                                selected_media_arr.clear();

                                if (!et_msg.getText().toString().equals("")) {
                                    chatAsync.sendMsg(et_msg.getText().toString());
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        et_msg.setText("");
                                        selectedMediaItemRecyclerAdapter.notifyDataSetChanged();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(Integer... progress) {
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                        }
                    };


                    asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                // 채팅창에 메세지가 입력되어 있다면 메세지를 보낸다.
                else if (!et_msg.getText().toString().equals("")) {
//                    String message = et_msg.getText().toString();
//                    String message = "message!!!" + et_msg.getText().toString();
                    chatAsync.sendMsg(et_msg.getText().toString());
                    et_msg.setText("");
                    // 입력창 내리기
                    hideKeyboard();
                }

                // x버튼 비활성화, 샵검색 보이기, 미디어 버튼 보이기, 보내기 버튼 안보여지게
                iv_cancel.setVisibility(View.GONE);
                iv_add.setVisibility(View.VISIBLE);
                tv_action_sharp.setVisibility(View.VISIBLE);
                iv_send_msg.setVisibility(View.GONE);
                break;
            // 샵검색창 활성화 버튼
            // 해당 버튼을 누르면 메세지 보내기 창이 숨겨지고 샵검색창이 생긴다.
            case R.id.tv_action_sharp:
                ll_msg.setVisibility(View.GONE);
                ll_search_sharp.setVisibility(View.VISIBLE);
                break;
            // 샵검색 액션
            case R.id.iv_search_sharp:
                intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("is_sharp_search", true);
                intent.putExtra("data", et_keyword_sharp.getText().toString());
                iv_cancel_sharp.performClick();
                // 입력창 내리기
                hideKeyboard();
                startActivityForResult(intent, SHARP_SEARCH);
                break;
            // 미디어 추가 관련 액션
            case R.id.iv_add:
                intent = new Intent(getApplicationContext(), Gallery.class);
                // Set the title
                intent.putExtra("title", "이미지 선택");
                // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
                intent.putExtra("mode", 2);
                intent.putExtra("maxSelection", 10); // Optional
                startActivityForResult(intent, OPEN_MEDIA_PICKER);
                break;
            // 미디어 추가 관련 취소 액션
            case R.id.iv_cancel:

                break;
            // 샵검색 취소 관련 액션
            case R.id.iv_cancel_sharp:
                et_keyword_sharp.setText("");
                ll_msg.setVisibility(View.VISIBLE);
                ll_search_sharp.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        chatAsync = new ChatAsync(StaticData.chat_url, sharedPreferences.getInt("id_db", -100));
        chatAsync = new ChatAsync(StaticData.chat_url);
        chatAsync.execute();

        Log.e(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatAsync.stopChat();
    }


//    @SuppressLint("StaticFieldLeak")
//    static AsyncTask asyncTask = new AsyncTask() {
//        ChatClient chatClient;
//        SharedPreferences sharedPreferences;
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            chatClient = new ChatClient(sharedPreferences.getInt("id_db", -100), StaticData.chat_url);
//            return null;
//
//        }
//
//        public void send(String msg) {
//            chatClient.sendMsg(msg);
//        }
//    };


    @SuppressLint("HandlerLeak")
    Handler handler_chat_history = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 메세지를 받아오지 못한 경우
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            // 메세지를 받아온 경우
            else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    chatItems.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
//                            final UserInfo userInfo = new UserInfo();

                            ChatItem chatItem = new ChatItem(
                                    jObject.getInt("user_id")
                                    , jObject.getString("name")
                                    , jObject.getString("link_profile")
                                    , jObject.getInt("chat_type")
                                    , jObject.getString("message")
                                    , jObject.getString("chat_datetime")
                                    , 0);

                            chatItems.add(chatItem);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    chatRecyclerAdapter.notifyDataSetChanged();
                    rv_msg.scrollToPosition(chatItems.size() - 1);
                }
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    public class ChatAsync extends AsyncTask {

        private static final String TAG = "ChatAsync";

        String ip_addr = StaticData.chat_url;   // 접속을 요청할 서버의 ip주소. 나중에 aws로 올리면 그때 다시 바꿀 것
        static final int port = 5000;   // 포트 번호
        Socket client = null;   // 클라이언트 소켓
        //    BufferedReader reader;  // 키보드로부터 메세지를 읽어올 스트림
        public DataOutputStream dataOutputStream;  // 서버에 데이터를 전송하기 위한 스트림
        DataInputStream dataInputStream;    // 서버가 전송한 데이터를 받기 위한 스트림
        String send_data;   //서버로 보낼 데이터를 저장하기 위한 변수
        String received_data;   // 서버로부터 받은 데이터를 저장하기 위한 변수

        //        int user_id;    // 유저 id
        boolean endflag = false;
        Handler handler = new Handler();

//        UserInfo user_info_target;

//        public ChatAsync(String ip_addr, int user_id) {
//            this.ip_addr = ip_addr;
//            this.user_id = user_id;
//        }

        public ChatAsync(String ip_addr) {
            this.ip_addr = ip_addr;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.e(TAG, "1111111111111111111111111");
            try {
                Log.e(TAG, "-------채팅 서버 요청--------");
                // 접속할 서버의 아이피 주소와 포트를 이용해서 클라이언트 소켓 생성
                client = new Socket(ip_addr, port);
                Log.e(TAG, "44444444444444444444");
                // 서버로부터 데이터를 수신받기 위해 클라이언트로부터 입력스트림을 얻어 ObjectInputStream으로 변환
                dataInputStream = new DataInputStream(client.getInputStream());
                Log.e(TAG, "555555555555555555");
                // 서버에게 메세지를 송신하기 위해 출력 스트림을 얻어 ObjectOutputStream으로 변환
                dataOutputStream = new DataOutputStream(client.getOutputStream());

                Log.e(TAG, "66666666666666666666666666666");
                if (dataOutputStream != null) Log.e(TAG, "oos 널 아님");
                else Log.e(TAG, "oos 널임");

                // 서버에게 사용자 아이디를 전송
                dataOutputStream.writeInt(user_id);

                // JDBC를 이용하여 사용자의 정보를 요청할 것이기 때문에 이쪽은 주석처리한다.
//                // 서버에게 사용자 특징 전송
//                // 이름, 단체or개인톡 정보, 방or유저번호 등등
//                // 사용자 이름 전송
//                dataOutputStream.writeUTF(getSharedPreferences("current_user",MODE_PRIVATE).getString("name",""));
//                // 사용자 프로필 링크 전송
//                dataOutputStream.writeUTF(StaticData.url+getSharedPreferences("current_user", MODE_PRIVATE).getString("link_profile", ""));


                // 갠톡인지 단톡인지 전송 private와 group로 나뉜다. // is_private_or_group 전송
                dataOutputStream.writeUTF(is_private_or_group);
                // 번호 전송
//                if (is_private_or_group.equals("private")) {


                dataOutputStream.writeInt(group_or_user_id);
//                }


                Log.e(TAG, "7777777777777777777");
                dataOutputStream.flush();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e + "\n\n\n\nexception 생김");
            }


            Log.e(TAG, "888888888888888888888888888");

            try {
                Log.e(TAG, "999999999999999999999");
                //입력 스트림을 통해 데이터를 읽어와서 출력
                while ((received_data = dataInputStream.readUTF()) != null && !endflag) {
//                    System.out.println(received_data);
                    Log.e(TAG, "받아온 메세지: " + received_data);
                    JSONObject jsonObject = new JSONObject(received_data);
                    ChatItem msg = new ChatItem(
                            jsonObject.getInt("user_id")
                            , jsonObject.getString("name")
                            , jsonObject.getString("link_profile")
                            , jsonObject.getInt("chat_type")
                            , jsonObject.getString("message")
                            , jsonObject.getString("chat_datetime")
                            , 0);
                    chatItems.add(msg);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //지워봄
//                            chatRecyclerAdapter.notifyDataSetChanged();
                            chatRecyclerAdapter.notifyItemInserted(chatItems.size() - 1);
                            if (!rv_msg.canScrollVertically(1)) {
                                rv_msg.smoothScrollToPosition(chatItems.size() - 1);
                            }
                        }
                    });
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    dataInputStream.close();
                    // 데이터 아웃풋도 없애고? 일단 시도
                    dataOutputStream.close();
                    client.close();
                    System.out.println("연결 종료");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public void sendMsg(String message) {
            final String chk = message;
            new Thread(new Runnable() {
                @Override
                public void run() {

//                Log.e(TAG, chk+"       ))))))))))))))");
                    try {
                        dataOutputStream.writeUTF("message!!!");
                        dataOutputStream.writeUTF(chk);
                        dataOutputStream.flush();
                        Log.e(TAG, "메세지 보냄. 내용: " + chk);
                    } catch (Exception e) {
                        Log.e(TAG, e + "\n\n\n\nㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜ");
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public void sendMedia(String url) {
            final String chk = url;
            new Thread(new Runnable() {
                @Override
                public void run() {

//                Log.e(TAG, chk+"       ))))))))))))))");
                    try {
                        dataOutputStream.writeUTF("image!!!!!");
                        dataOutputStream.writeUTF(chk);
                        dataOutputStream.flush();
                        Log.e(TAG, "미디어 보냄. 내용: " + chk);
                    } catch (Exception e) {
                        Log.e(TAG, e + "\n\n\n\nㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜ");
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        public void stopChat() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    endflag = true;
                    try {
                        if (dataOutputStream != null) {
                            dataOutputStream.writeUTF("quit!!!!!!");
                            dataOutputStream.flush();
                            dataInputStream.close();
                            dataOutputStream.close();
                            client.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    public class SelectedMediaItemRecyclerAdapter extends RecyclerView.Adapter<SelectedMediaItemRecyclerAdapter.MyViewHolder> {


        public int last_pos = -1;

        public SelectedMediaItemRecyclerAdapter() {
            super();
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

            Glide.with(getApplicationContext())
                    .load(selected_media_arr.get(position))
                    .apply(new RequestOptions()
                            .override(140, 140)
                            .centerCrop()
//                        .dontAnimate()
                            .skipMemoryCache(true))
                    .transition(withCrossFade())
                    .into(holder.iv_thumbnail);
            if (isImageFile(selected_media_arr.get(position))) {
                holder.iv_type.setImageResource(R.drawable.ic_image);
            } else {
                holder.iv_type.setImageResource(R.drawable.ic_video);
            }


        }

        @Override
        public int getItemCount() {
            return selected_media_arr.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_thumbnail, iv_cancel, iv_type;


            public MyViewHolder(View v) {
                super(v);
                iv_cancel = v.findViewById(R.id.iv_cancel);
                iv_thumbnail = v.findViewById(R.id.iv_thumbnail);
                iv_type = v.findViewById(R.id.iv_type);

                // 이미지 처리등은 나중에 한다.

                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selected_media_arr.remove(getAdapterPosition());
//                        notifyDataSetChanged();//지워봄
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            notifyItemChanged(getAdapterPosition());

                        }
                        last_pos = -1;

                    }

//                }
                });
                // 각 이미지 필터링 처리하거나 체크하는건 나중에 다 하자.
            }

            public ImageView getIv_thumbnail() {
                return iv_thumbnail;
            }
        }

    }
    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(et_msg.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_keyword_sharp.getWindowToken(), 0);
    }


}
