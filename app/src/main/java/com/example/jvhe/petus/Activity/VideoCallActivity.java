package com.example.jvhe.petus.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.Class.UserInfo;
import com.example.jvhe.petus.R;

import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Endpoint.Participant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

// 영상통화 액티비티.
public class VideoCallActivity extends AppCompatActivity implements Connector.IConnect, View.OnClickListener {

    private static final String TAG = "VideoCallActivity";


    // 이거 커넥터 보니까 asGuest옵션 보면 스트리밍도 가능할 것 같은데?
    private Connector vc;
    private FrameLayout videoFrame;

    SharedPreferences sharedPreferences;

    // 통화와 관련된 버튼들.
    // iv_accept는 받는 상황에서만 나타난다.
    // (받는 시점)통화가 연결되면 iv_mic_off, iv_videocam_off가 나타난다.
    // 통화를 거는 상황과 통화를 받는 상황은 이 액티비티를 실행시킬 인텐트 내부에 is_sent를 사용한다.
    ImageView iv_accept, iv_mic_off, iv_videocam_off, iv_call_end, iv_videocam_switch;

    // 접속한 유저 id, 이름
    String user_name;
    int user_id;

    // 전화하고 싶은 상대방 정보
    UserInfo user_info;

    // 방이름
    Intent intent;
    String room_name;

    LinearLayout ll_call;
    TextView tv_name;

    // 토스트 메세지 쓸 수 있도록 도와주는 핸들러
    Handler msg_handler;

    // 마이크 온오프
    Boolean is_mic_on = true;
    // 스피커폰 여부
    Boolean is_speaker = true;
    // 카메라 전면 여부
    Boolean is_front = true;
    // 카메라 온오프
    Boolean is_camera_on = true;

    // 진동
    Vibrator vibrator;
    // 음악
    MediaPlayer mediaPlayer;

    // 60초동안 통화 안받으면 꺼지게 하는 쓰레드 만들기.
    Thread thread;
    // 통화 받았는지 안받았는지 확인하는 변수. 통화 거는 사람의 경우 통화가 설립되는 순간(onParticipantJoined) true하고
    // 통화 받는 사람의 경우 승낙 버튼에 에 넣어볼까?
    Boolean call_estabilished = false;

    // 고민중인 사항. 방이름은 어떻게 짓고
    // 수신 거절 예외처리는 어떻게 할까?
    // 60초가 지나거나, 거절 버튼을 누르면?
    // 거절 버튼을 누르면 fcm 메세지를 보내나?
    // fcm 메세지 흐름도. http 요청으로
    // 전화를 걸 때 발신인 토큰도 같이 보내서 전화를 거절하고 싶을 때 바로 거절할 수 있도록 만들자.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 액션바 감추기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        sharedPreferences = getSharedPreferences("current_user", Context.MODE_PRIVATE);

        user_name = sharedPreferences.getString("name", "");
        user_id = sharedPreferences.getInt("id_db", -100);


        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = findViewById(R.id.fl_video);

        // 밑에 이미지뷰 전부 동그랗게 해준다.
        iv_accept = findViewById(R.id.iv_accept);
        iv_accept.setOnClickListener(this);
        iv_mic_off = findViewById(R.id.iv_mic_off);
        iv_mic_off.setOnClickListener(this);
        iv_videocam_off = findViewById(R.id.iv_videocam_off);
        iv_videocam_off.setOnClickListener(this);
        iv_call_end = findViewById(R.id.iv_call_end);
        iv_call_end.setOnClickListener(this);
        iv_videocam_switch = findViewById(R.id.iv_videocam_switch);
        iv_videocam_switch.setOnClickListener(this);

        // 발신인 정보 화면
        ll_call = findViewById(R.id.ll_call);
        tv_name = findViewById(R.id.tv_name);

        msg_handler = new Handler();

        // 통화 자동 종료 쓰레드 만들기
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(45000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!call_estabilished) {
                    msg_handler.post(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            }
        });
        thread.start();

        // 진동 관련 선언
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 음악 관련 선언
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgm);

        videoFrame.post(new Runnable() {
            @Override
            public void run() {
                // 영상통화 화면을 준비한다.
                //vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
                //vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());

                ready();

                // 통화를 받는 상황인지 아니면 거는 상황인지 확인하는 인텐트.
                intent = getIntent();
                Boolean is_send = intent.getBooleanExtra("is_send", false);
                // 통화를 거는 경우 인텐트로 받아온 유저 정보를 서버에 전송하여 fcm 알람을 해당 유저에게 전송한다.
                // 이 경우 통화 걸기 버튼은 숨긴다.
                if (is_send) {
                    // room_name은 어떻게 만들면 좋을까? 해시와 현재시간 함수, 그리고 내 아이디를 추가해서 만들어볼까?

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    room_name = "room_name_" + user_name + timeStamp + user_id;

                    user_info = (UserInfo) intent.getSerializableExtra("user_info");
                    // 상대방에게 전화를 거는 상황
                    // 전화 알림 보내기.
//                    String param = "user_id=" + user_info.getUser_id() + "&room_name=" + room_name + "&sender_id=" + user_id + "&sender_name=" + user_name + "&sender_token=" + sharedPreferences.getString("device_token", "");
                    String param = "user_id=" + user_info.getUser_id() + "&room_name=" + room_name + "&sender_id=" + user_id + "&sender_name=" + user_name;
                    HttpAsync httpAsync = new HttpAsync(param, StaticData.url + "send_video_call.php");
                    httpAsync.execute();
                    // 전화받기 버튼, 수신자 정보 숨김
                    iv_accept.setVisibility(View.GONE);
                    ll_call.setVisibility(View.GONE);
                    // 상대방에게 전화를 걸고, fcm 메세지를 보낸다.
                    connect(user_name, room_name);

                }
                // 통화를 받야야 하는 상황인 경우
                // 처음에는 통화 받기 버튼만 보여지고, 마이크 끄기와 카메라 끄기 버튼을 숨겨져 있는 상태다.
                // 통화를 받게 되면 통화 받기 버튼이 사라지고 통화 걸기 버튼이 생긴다.
                else {
                    iv_mic_off.setVisibility(View.INVISIBLE);
                    iv_videocam_off.setVisibility(View.INVISIBLE);
                    iv_videocam_switch.setVisibility(View.INVISIBLE);
                    String sender_name = intent.getStringExtra("sender_name") + " 님";
                    tv_name.setText(sender_name);
                    vibrator.vibrate((1000 * 45));
                    mediaPlayer.start();

                }
            }
        });

//        videoFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                videoFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//            }
//        });
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


        disconnect();

        if (mediaPlayer.isPlaying()){
            vibrator.cancel();
            mediaPlayer.stop();
        }
        ConnectorPkg.uninitialize();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_accept:
                // 통화 승인시 발신인과 통화를 한다.
                // 통화 승인시 전화받기 버튼은 사라지고 마이크 끄기와 카메라 숨김 버튼이 생긴다.
                room_name = intent.getStringExtra("room_name");
                connect(user_name, room_name);
                iv_accept.setVisibility(View.GONE);
                ll_call.setVisibility(View.GONE);
                iv_mic_off.setVisibility(View.VISIBLE);
                iv_videocam_off.setVisibility(View.VISIBLE);
                iv_videocam_switch.setVisibility(View.VISIBLE);
                // 통화 승인시 진동도 꺼진다.
                vibrator.cancel();
                mediaPlayer.stop();
                // 통화 승인시 통화 설립 변수 true로 바꾸기
                call_estabilished = true;
                break;
            case R.id.iv_mic_off:
                vc.setMicrophonePrivacy(is_mic_on);
                if (is_mic_on) {
                    iv_mic_off.setImageResource(R.drawable.ic_mic_on);
                } else {
                    iv_mic_off.setImageResource(R.drawable.ic_mic_off);
                }
                is_mic_on = !is_mic_on;
//                // 마이크 오프인데 일단 스피커 옵션으로 해보자. 안되네
//                vc.cycleMicrophone();
//                is_speaker = !is_speaker;
                break;
            case R.id.iv_videocam_off:
                // 카메라 온오프
                vc.setCameraPrivacy(is_camera_on);
                if (is_camera_on) {
                    iv_videocam_off.setImageResource(R.drawable.ic_videocam_on);
                } else {
                    iv_videocam_off.setImageResource(R.drawable.ic_videocam_off);
                }
                is_camera_on = !is_camera_on;
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
            case R.id.iv_call_end:
                // 통화 거부 상황에서는 해당 방을 폭파시켜야 할 것 같은데 어떻게 처리하지..?
                // fcm 알람을 던져서 상대방이 발신중이라면 통화를 강제로 꺼야 하나?
                // 그렇다면 boolean변수를 하나 추가해서 (is_trying) 전화중인지 아닌지에 따라 전화를 끄던가 해야 할 것 같다.
                // 통화 거절과 통화 종료를 나눠야 하는가?


                // 통화 종료
                disconnect();
                break;
        }
    }


    public void ready() {
        vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 1, "warning info@VidyoClient info@VidyoConnector", "", 0);
//        vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
//        videoFrame.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        vc.showViewAt(videoFrame, 0, 0, videoFrame.getMeasuredWidth(), videoFrame.getMeasuredHeight());
        vc.selectDefaultCamera();
//        msg_handler.post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), "창 크기 x=" + videoFrame.getMeasuredWidth() + " y=" + videoFrame.getMeasuredHeight(), Toast.LENGTH_SHORT).show();
//            }
//        });

        vc.registerParticipantEventListener(new Connector.IRegisterParticipantEventListener() {
            @Override
            public void onParticipantJoined(Participant participant) {
                msg_handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "연결되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                // 통화 승인되었다는 상황이므로 통화 설립 변수 true
                call_estabilished = true;
            }

            @Override
            public void onParticipantLeft(Participant participant) {
                vc.disconnect();
            }

            @Override
            public void onDynamicParticipantChanged(ArrayList<Participant> arrayList) {

            }

            @Override
            public void onLoudestParticipantChanged(Participant participant, boolean b) {

            }
        });
        Log.e(TAG, vc.getStatsJson());
    }
//        vc.showViewAt(videoFrame, 0, 0, 600, 600);
    // showViewAt를 이용해서 전체화면에서 서비스를 이용해 바탕화면으로 옮길 수 있을지도 모르겠다.


    public void connect(String name, String room_name) {
        vc.connect("prod.vidyo.io", StaticData.vidyo_token, name, room_name, this);
//        msg_handler.post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), "connect", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void disconnect() {
        vc.disconnect();
        finish();
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
    public void onFailure(Connector.ConnectorFailReason connectorFailReason) {
        msg_handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 영상통화를 종료하고 VideoCallActivity 또한 종료한다.
    // 통화가 끊기면 해당 액티비티 또한 끊자.
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


//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 0) {
//                Toast.makeText(getApplicationContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
//            } else if (msg.what == 1) {
//                String data = msg.getData().getString("data_arr");
//                if (data != null && data.contains("!!fail!!")) {
//                    Toast.makeText(getApplicationContext(), "팔로우 실패. 관리자에게 문의해 주세요", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "팔로우 실패");
//                } else if (data != null && data.contains("!!follow!!")) {
//                    Toast.makeText(getApplicationContext(), "팔로우!", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "팔로우 성공");
//
//                } else if (data != null && data.contains("!!unfollow!!")) {
//                    Toast.makeText(getApplicationContext(), "언팔로우!", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "언팔로우 성공");
//                }
//            }
//        }
//    };
}
