package com.example.pillreminder.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.pillreminder.Model.Reminder;

import java.util.Date;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private Context context;
    private AlarmHelper alarmHelper;
    private PreferencesManager preferencesManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        alarmHelper = new AlarmHelper(context);
        preferencesManager = new PreferencesManager(context);

        Bundle data = intent.getExtras();
        Intent startService = new Intent(context, NotificationService.class);
        Reminder reminder = null;
        if (data != null) {
            reminder = data.getParcelable("reminder");
            startService.putExtra("reminder", reminder);
        }
        if (intent.getAction() != null) {
            if (intent.getAction().equals(NotificationService.COMPLETE)) {
                alarmHelper.cancelSnoozeAlarm();
                context.stopService(startService);
            } else if (intent.getAction().equals(NotificationService.SNOOZE)) {
                long snoozeMillis = System.currentTimeMillis() + preferencesManager.getIntegerValue("snooze") * 60 * 1000L;
                alarmHelper.setSnoozeAlarm(reminder, snoozeMillis);
                context.stopService(startService);
            } else {
                context.stopService(startService);
            }
        } else {
            if (reminder != null) {
                if (!Objects.requireNonNull(intent.getExtras()).getBoolean("snooze"))
                    calculateNextAlarm(reminder);
                startService.putExtra("reminder", reminder);
                context.startForegroundService(startService);
                context.startService(startService);
            }
        }
    }

    private void calculateNextAlarm(Reminder reminder) {

        String[] splitRepeatTime = reminder.getReminderHour().split(":");
        int repeatHour = Integer.parseInt(splitRepeatTime[0]);
        int repeatMinute = Integer.parseInt(splitRepeatTime[1]);
        long repeatMillis = repeatHour * 60 * 60 * 1000L + repeatMinute * 60 * 1000L;

        reminder.setFirstMilliSecond(reminder.getFirstMilliSecond() + repeatMillis);
        alarmHelper.setAlarm(reminder);
        Log.i("TAG", "nextTime: " + new Date(reminder.getFirstMilliSecond()));

        if (preferencesManager.checkExistsKey("sendSms")
                && preferencesManager.getBooleanValue("sendSms")
                && preferencesManager.checkExistsKey("phone")) {
            String[] phoneNumbers = preferencesManager.getStringValue("phone").split(",");
            for (String phone: phoneNumbers) {
                try {
                    SmsManager smsManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        smsManager = context.getSystemService(SmsManager.class);
                    } else {
                        smsManager = SmsManager.getDefault();
                    }
                    smsManager.sendTextMessage(
                            phone,
                            null,
                            "زمان مصرف دارو " + reminder.getTitle() + " رسیده است.",
                            null,
                            null);
                } catch (Exception e) {
                    Log.i("TAG", "smsMangerFail:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

}
