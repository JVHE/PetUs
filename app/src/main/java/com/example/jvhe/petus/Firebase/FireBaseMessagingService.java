package com.example.jvhe.petus.Firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jvhe.petus.Activity.ChatRoomActivity;
import com.example.jvhe.petus.Activity.MainActivity;
import com.example.jvhe.petus.Activity.VideoCallActivity;
import com.example.jvhe.petus.Class.StaticData;
import com.example.jvhe.petus.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//       if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//           if (true) {
//            } else {
//                handleNow();
//            }
//        }
//
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getBody());
//        }
//    }
//
//    private void handleNow() {
//        Log.d(TAG, "Short lived task is done.");
//    }
//
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//       String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("FCM Message")
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//       NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelName = getString(R.string.default_notification_channel_name);
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//       }
//        notificationManager.notify(0, notificationBuilder.build());
//    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "메세지 받아옴 " + remoteMessage.getData());
        Log.e(TAG, "메세지 바디: " + remoteMessage.getData().get("message"));
        // 메세지 종류에 따라 조건문 나눠서 영상통화인지 채팅인지 확인.

        JSONObject jObject = null;
        try {
            jObject = new JSONObject(remoteMessage.getData().get("message"));
            // 채팅
            if (jObject.getString("type").equals("chat")) {
                sendNotification(remoteMessage.getData().get("message"));
            }
            // 영상 통화
            else {
                videoCall(jObject);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        remoteMessage.getData().get("message");
    }

    private void videoCall(JSONObject jObject) {
//        PowerManager manager = (PowerManager)getSystemService(Context.POWER_SERVICE);
//
//        boolean bScreenOn = manager.isScreenOn();

        Intent intent = new Intent(getApplicationContext(), VideoCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent.putExtra("is_send", false);
            intent.putExtra("room_name", jObject.getString("room_name"));
            intent.putExtra("sender_name", jObject.getString("sender_name"));
            intent.putExtra("sender_token", jObject.getString("sender_token"));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendNotification(String messageBody) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.setDescription("channel description");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.GREEN);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }


        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notifiManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        if (isRunning(getApplicationContext())) {
            notificationIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        }


        JSONObject jObject = null;
        try {
            jObject = new JSONObject(messageBody);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.putExtra("starting_point", "notification");
            notificationIntent.putExtra("is_private_or_group", jObject.getString("is_private_or_group"));
            notificationIntent.putExtra("group_or_user_id", jObject.getInt("group_or_user_id"));
            notificationIntent.putExtra("name_or_title", jObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            builder.setContentTitle(jObject.getString("title")) // required
                    .setContentText(jObject.getString("content"))  // required
                    .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                    .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                    //                .setSound(RingtoneManager
                    //                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.icon)
                    .setLargeIcon(Bitmap.createBitmap(BitmapFactory.decodeStream(new URL(StaticData.url + jObject.getString("link_profile")).openConnection().getInputStream())))
                    .setBadgeIconType(R.drawable.icon)
                    .setContentIntent(pendingIntent);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
//        Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                Glide.with(getApplicationContext())
//                        .asBitmap()
//                        .load(StaticData.url+messageBody.getData().get("link_profile"))
//                        .apply(StaticData.requestOptions_rounded)
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                builder.setContentTitle(messageBody.getData().get("title")) // required
//                                        .setContentText(messageBody.getData().get("content"))  // required
//                                        .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
//                                        .setAutoCancel(true) // 알림 터치시 반응 후 삭제
////                .setSound(RingtoneManager
////                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
////                .setSmallIcon(R.mipmap.ic_launcher)
//                                        .setLargeIcon(resource)
//                                        .setBadgeIconType(R.mipmap.ic_launcher)
//                                        .setContentIntent(pendingIntent);
//                                notifManager.notify(0, builder.build());
//                            }
//                        });
//            }
//        });


        notifiManager.notify(0, builder.build());
        /////////////////////////////////

//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "tes")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("FCM Push Test") // 이부분은 어플 켜놓은 상태에서 알림 메세지 받으면 저 텍스트로 띄워준다.
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

}

