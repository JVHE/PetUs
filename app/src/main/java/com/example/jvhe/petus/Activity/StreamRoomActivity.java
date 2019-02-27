package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jvhe.petus.Adapter.StreamChatRecyclerAdapter;
import com.example.jvhe.petus.Class.GiveStarBalloonDialog;
import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.StreamRoomItem;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Endpoint.ChatMessage;
import com.vidyo.VidyoClient.Endpoint.Participant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class StreamRoomActivity extends AppCompatActivity implements Connector.IConnect, View.OnClickListener {

    private static final String TAG = "StreamRoomActivity";

    // 채팅 기능같은거 추가해 줄 예정이다. 방에 반투명 상태로 스트리머 제목같은거
    // 띄워주고, 밑에는 채팅 목록이랑, 채팅 입력창, 전송 버튼같은거 집어넣을 생각이다.
    // 화면을 클릭하면 방설명이랑 채팅설명은 사라졌다 켜졌다 하게 만들 생각이다.
    // 아마 boolean변수와 setVisibility 설정을 이용하면 될 것 같다.
    // 채팅의 경우 라이브러리 사용해서 간단하게 구현할 예정이다.

    // 방이름
    String room_name;
    // 게스트로 접근시 알 수 있는 방정보.
    StreamRoomItem streamRoomItem;

    // 스트리밍 가즈아
    private Connector vc;
    private LinearLayout videoFrame;

    // 스트리밍 방 생성시 스트리머 id를 알 수 있도록 해주기 위해 선언
    // 스트리밍 방 참가시 유저 name를 알 수 있도록 해주기 위해 선언
    SharedPreferences sharedPreferences;

    // 토스트 메세지 쓸 수 있도록 도와주는 핸들러
    Handler msg_handler;

    // 접속한 유저 id, 이름
    String user_name;
    int user_id;
    boolean is_streamer = false;
    Intent intent;
    LinearLayout ll_chat;
    LinearLayout ll_case;

    // 카메라 앞뒤 전환 버튼
    ImageView iv_videocam_switch;
    // 카메라 전면 여부
    Boolean is_front = true;


    // 상태, 채팅창 관련 변수들
    boolean status_on = true;
    //    ContentFrameLayout ll_stream_room;
    LinearLayout ll_status;
    TextView tv_title, tv_name;
    EditText et_chat;
    Button btn_send;
    RecyclerView rv_chat;
    StreamChatRecyclerAdapter streamChatRecyclerAdapter;
    ArrayList<String> stringArrayList;
    // 키보드 관련 변수
    InputMethodManager imm;

    // 별풍선 관련 변수
    GiveStarBalloonDialog giveStarBalloonDialog;
    ImageView iv_star_balloon;
    String sending_msg = "";
    int giving_number_star_balloons = 0;
    // 별풍선 소리 설정
    TextToSpeech tts;
    // 별풍선 리액션 문자
    String reaction = "를 선물하셨습니다.";

    // 내가 나가는지 아니면 남이 나가는지 확인
    boolean is_left = false;

    // 종료시 게시물 삭제 기능. 디스트로이에 해주자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_room);

        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        sharedPreferences = getSharedPreferences("current_user", Context.MODE_PRIVATE);

        user_name = sharedPreferences.getString("name", "");
        user_id = sharedPreferences.getInt("id_db", -100);


        // 인텐트 정보 가져온다.
        intent = getIntent();
        // 스트리머인지, 뷰어인지 확인하는 변수
        is_streamer = intent.getBooleanExtra("is_streamer", false);

        // 상태, 채팅창 관련 변수들
        ll_status = findViewById(R.id.ll_status);
        tv_title = findViewById(R.id.tv_title);

        tv_name = findViewById(R.id.tv_name);
        et_chat = findViewById(R.id.et_chat);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        ll_case = findViewById(R.id.ll_case);
        ll_case.setOnClickListener(this);
        ll_chat = findViewById(R.id.ll_chat);
        ll_chat.setOnClickListener(this);

        iv_star_balloon = findViewById(R.id.iv_star_balloon);
        iv_star_balloon.setOnClickListener(this);


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });


        iv_videocam_switch = findViewById(R.id.iv_videocam_switch);
        iv_videocam_switch.setOnClickListener(this);

        if (is_streamer) {
            tv_title.setText(intent.getStringExtra("title"));
            tv_name.setText(user_name);
            streamRoomItem = new StreamRoomItem();
            streamRoomItem.setStreamer_info(new UserInfo());
            streamRoomItem.getStreamer_info().setUser_id(user_id);
            streamRoomItem.getStreamer_info().setName(user_name);

            // 스트리머는 별풍쏘기 버튼이 없다.
            iv_star_balloon.setVisibility(View.GONE);
            // 스트리머 리액션 설정
            reaction = intent.getStringExtra("reaction");

        } else {
            streamRoomItem = (StreamRoomItem) intent.getSerializableExtra("stream_room");
            tv_title.setText(streamRoomItem.getTitle());
            tv_name.setText(streamRoomItem.getStreamer_info().getName());
            reaction = streamRoomItem.getReaction();
            iv_videocam_switch.setVisibility(View.GONE);
        }

        rv_chat = findViewById(R.id.rv_chat);
        stringArrayList = new ArrayList<>();
        streamChatRecyclerAdapter = new StreamChatRecyclerAdapter(getApplicationContext(), stringArrayList);
        rv_chat.setAdapter(streamChatRecyclerAdapter);
//        // 리사이클러뷰 역순으로 출력하는 코드 일단 레이아웃에 설정해봄
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
        rv_chat.setLayoutManager(mLayoutManager);

//        TextToSpeech tts = new TextToSpeech(this, this);
//        tts.setLanguage(Locale.KOREA);

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = findViewById(R.id.ll_stream);

        videoFrame.setOnClickListener(this);

        // 방생성 스트리밍 정보 httpAsync로 보낼 생각이다.


        videoFrame.post(new Runnable() {
            @Override
            public void run() {
                // 영상통화 화면을 준비한다.
                ready();

                // 스트리머라면 방을 만드는 요청을 한다
                if (is_streamer) {
                    // 방이름 선언
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    room_name = "room_name_" + user_name + timeStamp + user_id;
                    // 스트리밍 시작하는 상황
                    String param = "streamer_id=" + user_id +
                            "&room_name=" + room_name +
                            "&streamer_name=" + user_name +
                            "&title=" + intent.getStringExtra("title") +
                            "&desc=" + intent.getStringExtra("desc") +
                            "&reaction=" + intent.getStringExtra("reaction");
                    HttpAsync httpAsync = new HttpAsync(param, StaticData.url + "start_streaming.php", handler);
                    httpAsync.execute();
                    connect(user_name, room_name);
                }
                // 뷰어라면 방에 들어간다.
                else {
                    room_name = streamRoomItem.getRoom_name();
                    connect(user_name, room_name);
                }
            }
        });
        msg_handler = new Handler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vc != null) {
            // Set the VidyoConnector's mode to background
            vc.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Background);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (vc != null) {
            // Set the VidyoConnector's mode to foreground
            vc.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Foreground);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_left = true;
        disconnect();
        ConnectorPkg.uninitialize();

        if (is_streamer) {
            HttpAsync httpAsync = new HttpAsync("streamer_id=" + user_id, StaticData.url + "stream_delete.php");
            httpAsync.execute();
        }
    }

    public void ready() {
        if (is_streamer) {
            vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Tiles, 0, "warning info@VidyoClient info@VidyoConnector", "", 0);

            vc.showViewAt(videoFrame, 0, 0, videoFrame.getMeasuredWidth(), videoFrame.getMeasuredHeight());
        } else {
            vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Tiles, 1, "warning info@VidyoClient info@VidyoConnector", "", 0);
        }

        vc.registerParticipantEventListener(new Connector.IRegisterParticipantEventListener() {
            @Override
            public void onParticipantJoined(Participant participant) {
                final String msg = "!!접속!!" + participant.name + "님이 입장하셨습니다.";

                if (!participant.name.equals(streamRoomItem.getStreamer_info().getName())) {
                    msg_handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 채팅 내용 받아와서 뿌려준다.
                            stringArrayList.add(msg);
                            streamChatRecyclerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onParticipantLeft(Participant participant) {
//                vc.disconnect();
                // 내가 나갈 때 상황 알기.
                Log.e(TAG, participant.name + " 가 떠남.");
                // 방장 나갈 때 방종료 다이얼로그 띄우고 finish하기
                if (!is_left && !is_streamer && participant.getName().equals(streamRoomItem.getStreamer_info().getName())) {
                    final AlertDialog.Builder alert_builder = new AlertDialog.Builder(StreamRoomActivity.this);
                    alert_builder.setTitle("방송 종료")
                            .setCancelable(false)
                            .setMessage(participant.getName() + "님의 방송이 종료되었습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).setCancelable(false);
                    msg_handler.post(new Runnable() {
                        @Override
                        public void run() {
                            alert_builder.create().show();
                        }
                    });
                }
            }

            @Override
            public void onDynamicParticipantChanged(ArrayList<Participant> arrayList) {

            }

            @Override
            public void onLoudestParticipantChanged(Participant participant, boolean b) {

            }
        });

        vc.registerMessageEventListener(new Connector.IRegisterMessageEventListener() {
            @Override
            public void onChatMessageReceived(Participant participant, ChatMessage chatMessage) {
                Log.e(TAG, "onChatMessageReceived 받은 메세지: " + chatMessage);
                Log.e(TAG, "onChatMessageReceived 받은 메세지 바디: " + chatMessage.body);
                if (chatMessage.body.equals("Time to VIDYO!")) return;

                final String msg = chatMessage.body;
                msg_handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 채팅 내용 받아와서 뿌려준다.
                        stringArrayList.add(msg);
                        streamChatRecyclerAdapter.notifyDataSetChanged();
                        if (msg.contains("!!별풍!!")) {
                            String msg2 = msg.substring(6);
                            String number_star_balloons = msg2.split("!!")[0];
                            String name = msg2.split("!!")[1];
                            String message = msg2.split("!!")[2];

                            if (Integer.parseInt(number_star_balloons) >= 10) {
                                // 버전이 23 이상이다.
                                //http://stackoverflow.com/a/29777304
                                ttsGreater21(name + "님 별풍선 " + number_star_balloons + "개" + reaction + message);
                            }
                        }
                    }
                });
            }
        });

        Log.e(TAG, vc.getStatsJson());
    }

    public void connect(String name, String room_name) {
        if (is_streamer) {
            vc.connect("prod.vidyo.io", StaticData.vidyo_token, name, room_name, this);
        } else {
//            vc.setCameraPrivacy(true);
//            vc.disable();
            vc.setSpeakerPrivacy(true);
            vc.setMicrophonePrivacy(true);
            vc.setCameraPrivacy(true);
            vc.connect("prod.vidyo.io", StaticData.vidyo_token, name, room_name, this);
            vc.showViewAt(videoFrame, 0, 0, videoFrame.getMeasuredWidth(), videoFrame.getMeasuredHeight());
        }

        msg_handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disconnect() {
        vc.disconnect();
        finish();
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "뷰 id: " + v.getId());
        switch (v.getId()) {
            case R.id.btn_send:
                if (!et_chat.getText().toString().equals("")) {
                    // 채팅내용 보내기
                    if (is_streamer) {
                        vc.sendChatMessage("!!방장!!" + user_name + ": " + et_chat.getText().toString());
                        stringArrayList.add("!!방장!!" + user_name + ": " + et_chat.getText().toString());
                        Log.e(TAG, "메세지: " + "!!방장!!" + user_name + ": " + et_chat.getText().toString());
                    } else {
                        vc.sendChatMessage("!!뷰어!!" + user_name + ": " + et_chat.getText().toString());
                        stringArrayList.add("!!본인!!" + user_name + ": " + et_chat.getText().toString());
                        Log.e(TAG, "메세지: " + "!!뷰어!!" + user_name + ": " + et_chat.getText().toString());
                    }
                    streamChatRecyclerAdapter.notifyDataSetChanged();
                }
                et_chat.setText("");
                imm.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
                break;
//            case R.id.ll_status:
//                if (status_on) {
//                    ll_status.setVisibility(View.INVISIBLE);
//                    ll_chat.setVisibility(View.INVISIBLE);
//                    rv_chat.setVisibility(View.INVISIBLE);
//
//                } else {
//                    ll_status.setVisibility(View.VISIBLE);
//                    ll_chat.setVisibility(View.VISIBLE);
//                    rv_chat.setVisibility(View.VISIBLE);
//                }
//                status_on = !status_on;
//                Log.e(TAG, "스테이터스 눌림. status_on: " + status_on);
//                break;
            case R.id.ll_case:
                if (status_on) {
                    ll_status.setVisibility(View.INVISIBLE);
                    ll_chat.setVisibility(View.INVISIBLE);
                    rv_chat.setVisibility(View.INVISIBLE);
                    imm.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
                } else {
                    ll_status.setVisibility(View.VISIBLE);
                    ll_chat.setVisibility(View.VISIBLE);
                    rv_chat.setVisibility(View.VISIBLE);
                }
                status_on = !status_on;
                Log.e(TAG, "스테이터스 눌림. status_on: " + status_on);
                break;

            case R.id.iv_videocam_switch:
                // 카메라 전환 옵션으로 해보자.
                vc.cycleCamera();
                if (is_front) {
                    iv_videocam_switch.setImageResource(R.drawable.ic_camera_front);
                } else {
                    iv_videocam_switch.setImageResource(R.drawable.ic_camera_rear);
                }
                is_front = !is_front;
                break;

            case R.id.iv_star_balloon:
                // 별풍선 관련 작업
                HttpAsync httpAsync = new HttpAsync("user_id=" + user_id, StaticData.url + "request_number_star_balloons.php", handler_number_star_balloons);
                httpAsync.execute();
//                giveStarBalloonDialog.setNumber_star_balloons();
//                giveStarBalloonDialog.show();
                break;

        }
    }

    @Override
    public void onSuccess() {
        msg_handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "연결 성공", Toast.LENGTH_SHORT).show();
//                Boolean is_send = intent.getBooleanExtra("is_send", false);
//                // 통화를 거는 경우 인텐트로 받아온 유저 정보를 서버에 전송하여 fcm 알람을 해당 유저에게 전송한다.
//                // 이 경우 통화 걸기 버튼은 숨긴다.
//                if (is_send) {
//
//                    connect(user_name, room_name);
//                }
            }
        });
    }

    @Override
    public void onFailure(final Connector.ConnectorFailReason connectorFailReason) {
        msg_handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "연결 실패" + connectorFailReason, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "연결 실패 " + connectorFailReason);
            }
        });
    }

    @Override
    public void onDisconnected(Connector.ConnectorDisconnectReason connectorDisconnectReason) {
        msg_handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "연결 종료", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler_give_star_balloon = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
                    Toast.makeText(getApplicationContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else if (data.contains("!!success!!")) {
                    // 처리는 php단에서 해준다.
                    // 이펙트는 메시지 리시브랑 여기서 해준다.
                    vc.sendChatMessage("!!별풍!!" + giving_number_star_balloons + "!!" + user_name + "!!" + sending_msg);
                    stringArrayList.add("!!별풍!!" + giving_number_star_balloons + "!!" + user_name + "!!" + sending_msg);
                    Log.e(TAG, "!!별풍!!" + giving_number_star_balloons + "!!" + user_name + "!!" + sending_msg);
                    streamChatRecyclerAdapter.notifyDataSetChanged();

                    if (giving_number_star_balloons >= 10) {
                        // 버전이 23 이상이다.
                        //http://stackoverflow.com/a/29777304
                        ttsGreater21(user_name + "님이 별풍선 " + giving_number_star_balloons + "개를 선물하셨습니다. " + sending_msg);
                    }
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler_number_star_balloons = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                String data = msg.getData().getString("data_arr");
                Log.e("!!!!!!!!!!!!!", data);
                if (data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "실패했습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "실패.", Toast.LENGTH_SHORT).show();
                } else {
                    int i = Integer.parseInt(data.trim());
                    giveStarBalloonDialog = new GiveStarBalloonDialog(StreamRoomActivity.this, streamRoomItem.getStreamer_info().getName(), i);
                    giveStarBalloonDialog.setDialogListener(new GiveStarBalloonDialog.GiveStarBalloonDialogListener() {
                        @Override
                        public void onPositiveClicked(String msg, int number_star_balloons) {
                            // 나말고 나머지 사람들에게 별풍선 이펙트 돌리고
                            // 공지로 몇개 보냈다도 커스텀다이얼로그로 집어넣고
                            // 메세지도 함께 집어넣을까
                            // 메세지 리시브에서 이펙트 집어넣고, 현재 이 코드 밑에 이펙트 집어넣자.
                            // 별풍 메세지는  httpAsync 넣고 응답 오면 그때 작업할까?? 그게 맞는 것 같은데?
                            // 일단 그렇게 옮겼다.
                            giving_number_star_balloons = number_star_balloons;
                            sending_msg = msg;
                            HttpAsync httpAsync = new HttpAsync(
                                    "user_id=" + user_id +
                                    "&streamer_id=" + streamRoomItem.getStreamer_info().getUser_id() +
                                    "&number_star_balloons=" + number_star_balloons,
                                    StaticData.url + "give_star_balloon.php", handler_give_star_balloon);
                            httpAsync.execute();
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    giveStarBalloonDialog.show();
                }
            }
        }
    };


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
