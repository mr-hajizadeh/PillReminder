package com.example.pillreminder.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.pillreminder.Model.Reminder;

import java.util.Date;

public class AlarmHelper {

    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmHelper(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setAlarm(Reminder reminder) {
        int INTENT_FLAG = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        Log.i("TAG", "setAlarm: " + new Date(reminder.getFirstMilliSecond()));
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminder", reminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId(),
                intent,
                INTENT_FLAG);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getFirstMilliSecond(), pendingIntent);
    }

    @SuppressLint("ScheduleExactAlarm")
    public void updateAlarm(Reminder reminder) {
        int INTENT_FLAG = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        cancelAlarm(reminder);

        if (isAlarmSnoozeSet()) {
            cancelSnoozeAlarm();
            long snoozeMillis = System.currentTimeMillis() +
                    new PreferencesManager(context).getIntegerValue("snooze") * 60 * 1000L;
            setSnoozeAlarm(reminder, snoozeMillis);
        }

        Log.i("TAG", "setAlarm: " + new Date(reminder.getFirstMilliSecond()));
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminder", reminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId(),
                intent,
                INTENT_FLAG);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getFirstMilliSecond(), pendingIntent);
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setSnoozeAlarm(Reminder reminder, long snoozeTime) {
        int INTENT_FLAG = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        Log.i("TAG", "setSnoozeAlarm: " + new Date(snoozeTime));
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("snooze", true);
        intent.putExtra("reminder", reminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                7536,
                intent,
                INTENT_FLAG);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
    }


    public void cancelAlarm(Reminder reminder) {
        int INTENT_FLAG = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId(),
                intent,
                INTENT_FLAG);
        alarmManager.cancel(pendingIntent);
    }

    public void cancelSnoozeAlarm() {
        int INTENT_FLAG = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                7536,
                intent,
                INTENT_FLAG);
        alarmManager.cancel(pendingIntent);

    }

    public boolean isAlarmSnoozeSet() {
        int INTENT_FLAG = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                1000,
                intent,
                INTENT_FLAG);

        return pendingIntent != null;
    }


}
