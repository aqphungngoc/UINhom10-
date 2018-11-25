package com.icebear.speechnote.alarmwithreminder;

import java.util.ArrayList;
import java.util.Calendar;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.activity.SpeechNote;
import com.icebear.speechnote.notefile.DatabaseHelper;
import com.icebear.speechnote.notefile.Reminder;
import com.icebear.speechnote.utils.Helper;

public class ReminderManager {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TIME = "time";
    public static final String VIBRATE = "vibrate";
    public static final String TONE = "alarmTone";
    public static final String REPEATE = "repeate";

    String TAG = this.getClass().getSimpleName();
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO Auto-generated method stub
//        setAlarms(context);
//    }

    public static void setAlarms(Context context) {
        Log.i("aaaaa", "set Alarms ");
        cancelAlarms(context);
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        ArrayList<Reminder> reminders = dbHelper.getAllReminder();

        for (Reminder reminder : reminders) {
            Log.i("aaaaa", "reminder: " + reminder.toString());

            PendingIntent pIntent = createPendingIntent(context, reminder);

            long time = reminder.getTime();

//                int reminderMinutes = Integer.parseInt(Helper.getDate(time, NoteConst.DATE_FORMAT_MINUTES));
//                int reminderHour = Integer.parseInt(Helper.getDate(time, NoteConst.DATE_FORMAT_HOUR));
//                String remiderDay = Helper.getDate(time, NoteConst.DATE_FORMAT_DAY);
            long now = System.currentTimeMillis();

            Log.i("xxxxx", time + " " + now);
            boolean alarmSet = false;
            if (time > now) {
                Log.i("xxxxxx", "time > now");
                if (reminder.getRepeatable() == 1) {
                    setAlarmRepeat(context, time, pIntent);
                } else {
                    setAlarm(context, time, pIntent);
                }
                alarmSet = true;
            } else {
                if (reminder.getRepeatable() == 1) {
                    setAlarmRepeat(context, time + 86400000, pIntent);
                }
                alarmSet = true;
            }
            break;
        }

    }

    private static void setAlarmRepeat(Context context, long timemilis, PendingIntent pIntent) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timemilis, AlarmManager.INTERVAL_DAY, pIntent);
    }


    public static void setAlarm(Context context, long timemilis, PendingIntent pIntent) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Log.i("xxxxxx", "time > now");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timemilis, pIntent);

    }

    public static void cancelAlarms(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        ArrayList<Reminder> reminders = dbHelper.getAllReminder();

        long now = System.currentTimeMillis();
        if (reminders != null) {
            for (Reminder reminder : reminders) {
                if (reminder.getTime() < now && reminder.getRepeatable() == 0) {
                    PendingIntent pIntent = createPendingIntent(context, reminder);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    alarmManager.cancel(pIntent);
                }
            }
        }
    }

    private static PendingIntent createPendingIntent(Context context, Reminder model) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(NoteConst.REMINDER_DES, model.getNotedes());
        intent.putExtra(NoteConst.REMINDER_RINGTONE, model.getRingtone());
        intent.putExtra(NoteConst.REMINDER_VIBRATE, model.getVibrate());

        Log.i("aaaaa", "reminder: " + model.toString());
        return PendingIntent.getBroadcast(context, (int) model.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
