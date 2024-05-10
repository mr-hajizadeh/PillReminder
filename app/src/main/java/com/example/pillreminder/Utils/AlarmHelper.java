package com.example.pillreminder.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

        Log.i("TAG", "setAlarm: " + new Date(reminder.getFirstMilliSecond()));
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminder", reminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getFirstMilliSecond(), pendingIntent);
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setSnoozeAlarm(Reminder reminder, long snoozeTime) {

        Log.i("TAG", "setSnoozeAlarm: " + new Date(snoozeTime));
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("snooze", true);
        intent.putExtra("reminder", reminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId() + 1000,
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
    }


    public void cancelAlarm(Reminder reminder) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void cancelSnoozeAlarm(Reminder reminder) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId() + 1000,
                intent,
                PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}
