package com.example.jvhe.petus.Class;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

// 채팅 요청 클래스
public class ChatClient extends Thread {

    private static final String TAG = "ChatClient";

    String ip_addr = StaticData.chat_url;   // 접속을 요청할 서버의 ip주소. 나중에 aws로 올리면 그때 다시 바꿀 것
    static final int port = 5000;   // 포트 번호
    Socket client = null;   // 클라이언트 소켓
    BufferedReader reader;  // 키보드로부터 메세지를 읽어올 스트림
    DataOutputStream dataOutputStream;  // 서버에 데이터를 전송하기 위한 스트림
    DataInputStream dataInputStream;    // 서버가 전송한 데이터를 받기 위한 스트림
    String send_data;   //서버로 보낼 데이터를 저장하기 위한 변수
    String received_data;   // 서버로부터 받은 데이터를 저장하기 위한 변수

    int user_id;    // 유저 id
    ChatClientThreadRunnable chatClientThreadRunnable;  // 서버가 보낸 데이터를 받기 위한 러너블 객체
    boolean endflag = false;

    // 생성자 선언
    // 생성자에서 유저의 id와 ip주소를 받아온다.
    public ChatClient(int user_id, String ip_addr) {
        this.user_id = user_id;
        this.ip_addr = ip_addr;


    }

    @Override
    public void run() {
        super.run();

        try {
            System.out.println("-------채팅 서버 요청--------");
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

            Log.e(TAG, "7777777777777777777");
            dataOutputStream.flush();



//            System.out.println("-------채팅 서버 요청--------");
//            // 접속할 서버의 아이피 주소와 포트를 이용해서 클라이언트 소켓 생성
//            client = new Socket(ip_addr, port);
//
//            // 서버로부터 데이터를 수신받기 위해 클라이언트로부터 입력스트림을 얻어 ObjectInputStream으로 변환
//            objectInputStream = new ObjectInputStream(client.getInputStream());
//            // 서버에게 메세지를 송신하기 위해 출력 스트림을 얻어 ObjectOutputStream으로 변환
//            objectOutputStream = new ObjectOutputStream(client.getOutputStream());
//
//            // 서버에게 사용자 아이디를 전송
//            objectOutputStream.writeInt(user_id);
//            objectOutputStream.flush();
//            // 서버가 보낸 데이터를 수신받기 위한 쓰레드 객체 생성
//            chatClientThreadRunnable = new ChatClientThreadRunnable(client, objectInputStream);
//            Thread thread = new Thread(chatClientThreadRunnable);
//            thread.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();

            // 접속 종료 선언인지 확인
            if (message.equals("/quit")) {
                endflag = true;
                dataInputStream.close();
                dataOutputStream.close();
                client.close();
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
