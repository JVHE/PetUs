package com.example.jvhe.petus.Firebase;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.jvhe.petus.Class.HttpAsync;
import com.example.jvhe.petus.Class.StaticData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceID";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onTokenRefresh() {
        // 토큰을 새로 만들어 준다.
        // 받아온 토큰을 현재 로그인하고 있는 유저의 계정에 집어넣어 준다.
        sharedPreferences = getSharedPreferences("current_user", MODE_PRIVATE);
        editor=sharedPreferences.edit();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
        editor.putString("device_token", refreshedToken);
        editor.apply();

    }

    // 디바이스에서 생성된 토큰 정보를 서버에 전송한다.
    private void sendRegistrationToServer(String token) {
        HttpAsync httpAsync = new HttpAsync("user_id=" + sharedPreferences.getInt("id_db", -100) + "&token=" + token, StaticData.url + "save_token.php", handler);
        httpAsync.execute();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
