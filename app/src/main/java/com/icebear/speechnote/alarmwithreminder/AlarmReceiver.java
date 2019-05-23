package com.icebear.speechnote.alarmwithreminder;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;
import com.icebear.speechnote.activity.MainActivity;

import static com.icebear.speechnote.NoteConst.CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
//        Reminder reminder = (Reminder) intent.getStringExtra(NoteConst.OBJECT);
//        Log.i("xxxxx", "time: " + reminder.getNotedes());
        String reminderdes = intent.getStringExtra(NoteConst.REMINDER_DES);
        int ringtone = intent.getIntExtra(NoteConst.REMINDER_RINGTONE, 1);
        int vibrate = intent.getIntExtra(NoteConst.REMINDER_VIBRATE, 1);
                Log.i("xxxxx", "des: " + reminderdes);
//        createNotification(context, des, context.getString(R.string.noti_message), des);
        createNotification(context, reminderdes, context.getString(R.string.noti_message), ringtone, vibrate);
    }


//    public void createNotification(Context context, String msg, String msgText, String msgAlert) {
    public void createNotification(Context context, String reminderdes, String msgText, int ringtone, int vibrate) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                        "My Notifications", NotificationManager.IMPORTANCE_NONE);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("xxxxxxx", "error createNotifi");
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent(context,
                MainActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

                builder.setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(reminderdes)

                .setLights(Color.BLUE, 3000, 3000)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msgText))
                .setTicker(reminderdes)
                .setContentText(msgText)
                .setContentIntent(pendingIntent);

                if (ringtone == 1){
                    builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                }
                if (vibrate == 1){
                    builder.setVibrate(new long[]{0, 1000, 500, 1000});
                }


        Log.d("xxxxxxx", "aSADASDASD");

        notificationManager.notify(1, builder.build());
    }
}