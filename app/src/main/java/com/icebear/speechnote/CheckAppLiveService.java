package com.icebear.speechnote;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.icebear.speechnote.activity.MainActivity;

import java.util.Random;

public class CheckAppLiveService extends FirebaseMessagingService {
    public static final String CHANNEL_ID = "icebear channel id";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("aaaaaaaa", "receive icebear system notify");

        try {
            String title = "";
            String body = "";
            if (remoteMessage.getNotification() != null) {
                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();
            }
            createNoti(title, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNoti(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                        "Icebear System Notifications", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description icebear");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);

                Intent notification_intent = new Intent(this, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(notification_intent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

                builder.setSmallIcon(R.drawable.ic_launcher)
                        .setAutoCancel(false)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setOngoing(false);

                notificationManager.notify(new Random().nextInt(10000000), builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
