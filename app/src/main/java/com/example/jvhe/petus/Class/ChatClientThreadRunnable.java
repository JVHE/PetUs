package com.example.jvhe.petus.Class;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ChatClientThreadRunnable implements Runnable {

    Socket client;  // 서버와 통신하기 위한 소켓
    ObjectInputStream objectInputStream;  // 서버로부터 데이터를 수신받기 위한 스트림
    String received_data;   // 서버로부터 수신받은 데이터를 저장받기 위한 변수

    // 생성자 선언
    // 접속 요청한 소켓 객체와 입력 스트림이 전달됨
    public ChatClientThreadRunnable(Socket client, ObjectInputStream objectInputStream) {
        this.client = client;
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {
        try {
            //입력 스트림을 통해 데이터를 읽어와서 출력
            while ((received_data = (String)objectInputStream.readObject())!=null) {
                System.out.println(received_data);
            }

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                objectInputStream.close();
                client.close();
                System.out.println("연결 종료");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
